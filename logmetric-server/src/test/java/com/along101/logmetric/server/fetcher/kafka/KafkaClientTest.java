package com.along101.logmetric.server.fetcher.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.Properties;

/**
 * kafka中收取String测试
 * Created by yinzuolong on 2017/3/14.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KafkaClientTest {

    private String topic;
    private String bootstrapServers;
    private int count;

    @Before
    public void init() {
        topic = "framework.log";
        bootstrapServers = "localhost:9092";
        count = 5;
    }

    public static KafkaConsumer<String, byte[]> getKafkaConsumer(String bootstrapServers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "logmetricTest");
        props.put("client.id", "test1");
        props.put("max.poll.records", 1000);
        props.put("auto.offset.reset", "latest");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        return new KafkaConsumer<>(props);
    }

    public static KafkaProducer<String, byte[]> getKafkaProducer(String bootstrapServers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("client.id", "test");
        props.put("max.block.ms", "3000");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", ByteArraySerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    @Test
    public void test1Producer() {
        Producer<String, byte[]> producer = getKafkaProducer(bootstrapServers);
        for (int i = 0; i < count; i++) {
            String s = "m1:" + i;
            producer.send(new ProducerRecord<String, byte[]>(topic, s.getBytes()));
        }
        producer.close();
    }

    @Test
    public void test2Consumer() {
        KafkaConsumer<String, byte[]> consumer = getKafkaConsumer(bootstrapServers);
        consumer.subscribe(Arrays.asList(topic));
        ConsumerRecords<String, byte[]> records = consumer.poll(3000);
        for (ConsumerRecord<String, byte[]> record : records) {
            System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), new String(record.value()));
        }
        consumer.commitSync();
        consumer.close();
        Assert.assertEquals(count, records.count());
    }
}
