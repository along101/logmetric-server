package com.along101.logmetric.server.fetcher.kafka;

import com.along101.logmetric.common.bean.Message;
import com.along101.logmetric.common.util.KryoUtil;
import com.along101.logmetric.server.TestUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * kafka中收取logmetric测试
 * Created by yinzuolong on 2017/3/14.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KafkaLogmetricTest {

    private String logTopic;
    private String metricTopic;
    private String bootstrapServers;
    private int count;

    @Before
    public void init() {
        logTopic = "framework.log1";
        metricTopic = "framework.metric.dev";
//        bootstrapServers = "172.17.2.134:9092,172.17.2.135:9092,172.17.2.136:9092";
        bootstrapServers = "localhost:9092";
        count = 100;
    }


    /**
     * 先消费完队列中的所有消息
     */
    @Test
    public void test0ConsumeAll() {
        KafkaConsumer<String, byte[]> consumer = KafkaClientTest.getKafkaConsumer(bootstrapServers);
        consumer.subscribe(Arrays.asList(logTopic, metricTopic));

        int fetchSize = 0;
        while (true) {
            ConsumerRecords<String, byte[]> records = consumer.poll(3000);
            if (records.count() == 0) {
                break;
            }
            fetchSize += records.count();
            for (ConsumerRecord<String, byte[]> record : records) {
                System.out.printf("offset = %d, key = %s \t", record.offset(), record.key());
                byte[] b = record.value();
                System.out.println(new String(b));
            }
        }
        System.out.println(fetchSize);
        consumer.commitSync();
        consumer.close();
    }

    @Test
    public void test1ProducerLog() throws InterruptedException, TimeoutException, ExecutionException {
        KafkaProducer<String, byte[]> producer = KafkaClientTest.getKafkaProducer(bootstrapServers);
        for (int i = 0; i < count; i++) {
            Message msg = TestUtils.createLogMessage(String.valueOf(i));
            Future<RecordMetadata> future = producer.send(new ProducerRecord<String, byte[]>(logTopic, KryoUtil.serialize(msg)));
            future.get();
        }
        producer.close();
    }

    @Test
    public void test2ConsumerLog() {
        KafkaConsumer<String, byte[]> consumer = KafkaClientTest.getKafkaConsumer(bootstrapServers);
        consumer.subscribe(Arrays.asList(logTopic));
        int fetchSize = 0;
        while (true) {
            ConsumerRecords<String, byte[]> records = consumer.poll(3000);
            if (records.count() == 0) {
                break;
            }
            fetchSize += records.count();
            for (ConsumerRecord<String, byte[]> record : records) {
                System.out.printf("offset = %d, key = %s \t", record.offset(), record.key());
                byte[] b = record.value();
                Message msg = KryoUtil.deserialize(b, Message.class);
                TestUtils.printMessage(msg);
            }
        }
        consumer.commitSync();
        consumer.close();
        Assert.assertEquals(count, fetchSize);
    }

    @Test
    public void test3ProducerMetric() throws Exception {
        KafkaProducer<String, byte[]> producer = KafkaClientTest.getKafkaProducer(bootstrapServers);
        for (int i = 0; i < count; i++) {
            Message msg = TestUtils.createMetricMessage(String.valueOf(i), "logmetric.metric.test");
            Future<RecordMetadata> future = producer.send(new ProducerRecord<String, byte[]>(metricTopic, KryoUtil.serialize(msg)));
            future.get();
        }
        producer.close();
    }

    @Test
    public void test4ConsumerMetric() {

        KafkaConsumer<String, byte[]> consumer = KafkaClientTest.getKafkaConsumer(bootstrapServers);
        consumer.subscribe(Arrays.asList(metricTopic));
        int fetchSize = 0;
        while (true) {
            ConsumerRecords<String, byte[]> records = consumer.poll(3000);
            if (records.count() == 0) {
                break;
            }
            fetchSize += records.count();
            for (ConsumerRecord<String, byte[]> record : records) {
                System.out.printf("offset = %d, key = %s \t", record.offset(), record.key());
                byte[] b = record.value();
                Message msg = KryoUtil.deserialize(b, Message.class);
                TestUtils.printMessage(msg);
            }
        }
        consumer.commitSync();
        consumer.close();
        Assert.assertEquals(count, fetchSize);
    }

}
