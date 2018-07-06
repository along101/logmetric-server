package com.along101.logmetric.server.fetcher.kafka;

import com.alibaba.fastjson.JSON;
import com.along101.logmetric.common.bean.LogEvent;
import com.along101.logmetric.common.bean.Message;
import com.along101.logmetric.common.bean.Metric;
import com.along101.logmetric.common.util.KryoUtil;
import com.along101.logmetric.server.TestUtils;
import com.along101.logmetric.server.handler.log.LogMessage;
import com.along101.logmetric.server.utils.Constant;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 启动LogmetricServer，运行该测试，发送测试消息到kafka，验证metric和log
 * Created by yinzuolong on 2017/6/3.
 */
public class LogMetricProducerTest {

    private String bootstrapServers = "logkafkafat01.along101.com:9092,logkafkafat02.along101.com:9092,logkafkafat03.along101.com:9092,logkafkafat04.along101.com:9092,logkafkafat05.along101.com:9092";
    private String metricTopic = "framework.metric.dev";
    private String kryoLogTopic = "framework.log.dev";
    private String jsonLogTopic = "framework.jsonlog.dev";
    private String failLogTopic = "framework.log.fail";

    private int count = 1000;

    @Test
    public void testSendKryoMetric() throws Exception {
        KafkaProducer<String, byte[]> producer = KafkaClientTest.getKafkaProducer(bootstrapServers);
        for (int i = 0; i < count; i++) {
            Message msg = TestUtils.createMetricMessage(String.valueOf(i), "testSendKryoMetric");
            Future<RecordMetadata> future = producer.send(new ProducerRecord<String, byte[]>(metricTopic, KryoUtil.serialize(msg)));
            future.get();
        }
        producer.close();
    }

    @Test
    public void testSendJsonMetric() throws Exception {
        KafkaProducer<String, byte[]> producer = KafkaClientTest.getKafkaProducer(bootstrapServers);
        List<Metric> metrics = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Metric metric = TestUtils.createMetric("testSendJsonMetric", new Random().nextInt(10));
            String jsonStr = JSON.toJSONString(metric);
            metrics.add(metric);
            //单条
            producer.send(new ProducerRecord<String, byte[]>(metricTopic, jsonStr.getBytes(Constant.UTF_8)));
        }
        TimeUnit.SECONDS.sleep(2);
        //批量
        String jsonStr = JSON.toJSONString(metrics);
        producer.send(new ProducerRecord<String, byte[]>(metricTopic, jsonStr.getBytes(Constant.UTF_8)));
        producer.close();
    }

    @Test
    public void testSendKryoLog() throws Exception {
        KafkaProducer<String, byte[]> producer = KafkaClientTest.getKafkaProducer(bootstrapServers);
        for (int i = 0; i < count; i++) {
            Message msg = TestUtils.createLogMessage("testSendKryoLog");
            msg.setAppId("testSendKryoLog");
            Future<RecordMetadata> future = producer.send(new ProducerRecord<String, byte[]>(kryoLogTopic, KryoUtil.serialize(msg)));
            future.get();
        }
        producer.close();
    }

    @Test
    public void testSendJsonLog() throws Exception {
        KafkaProducer<String, byte[]> producer = KafkaClientTest.getKafkaProducer(bootstrapServers);
        List<Map<String, Object>> logs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            LogEvent logEvent = TestUtils.createTestLogEvent("testSendJsonLog" + i);
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("appId", "testSendJsonLog");
            logMap.put("message", logEvent.getMessage());
            logMap.put("level", logEvent.getLevel());
            logMap.put("timeStamp", System.currentTimeMillis());
            logMap.put("logName", logEvent.getLogName());
            logMap.put("tags", logEvent.getCopyOfTags());
            logMap.put("stackTrace", logEvent.getStackTrace());
            logs.add(logMap);
            //单条发送
            String jsonStr = JSON.toJSONString(logMap);
            producer.send(new ProducerRecord<String, byte[]>(jsonLogTopic, jsonStr.getBytes(Constant.UTF_8)));
        }
        //批量发送
        String jsonStr = JSON.toJSONString(logs);
        producer.send(new ProducerRecord<String, byte[]>(jsonLogTopic, jsonStr.getBytes(Constant.UTF_8)));
        producer.close();
    }

    @Test
    public void testSendFailLog() throws Exception {
        KafkaProducer<String, byte[]> producer = KafkaClientTest.getKafkaProducer(bootstrapServers);
        List<LogMessage> arrays = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            LogEvent logEvent = TestUtils.createTestLogEvent("testSendFailLog");
            LogMessage logMessage = new LogMessage();
            logMessage.setAppId("testSendFailLog");
            logMessage.setMessage(logEvent.getMessage());
            logMessage.setLevel(logEvent.getLevel().getName());
            logMessage.setTimeStampByLong(logEvent.getTimeStamp().getTime());
            logMessage.setLogName(logEvent.getLogName());
            logMessage.putAllTags(logEvent.getCopyOfTags());
            logMessage.setStackTrace(logEvent.getStackTrace());
            arrays.add(logMessage);
        }
        //批量发送
        String jsonStr = JSON.toJSONString(arrays);
        producer.send(new ProducerRecord<String, byte[]>(failLogTopic, jsonStr.getBytes(Constant.UTF_8)));
        producer.close();
    }

    @Test
    public void testSendString() throws UnsupportedEncodingException {
        KafkaProducer<String, byte[]> producer = KafkaClientTest.getKafkaProducer(bootstrapServers);
        String jsonStr = "{\"head\":{\"HOST_IP\":\"yueteng-linuxmint\"},\"source_name\":\"pata_e\",\"level\":\"INFO\",\"timeStamp\":\"1496733333893\",\"tags\":{\"other\":\"other info\"},\"logName\":\"my test\",\"appId\":\"11050002\",\"message\":\"test construct log 3\",\"log_time\":\"2017-06-06 13:58:03\"}";
        for (int i = 0; i < count; i++) {
            System.out.println("send log " + jsonStr);
            producer.send(new ProducerRecord<String, byte[]>("framework.jsonlog", jsonStr.getBytes(Constant.UTF_8)));
        }
        producer.close();
    }
}
