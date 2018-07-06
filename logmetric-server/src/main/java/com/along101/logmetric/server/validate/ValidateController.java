package com.along101.logmetric.server.validate;

import com.alibaba.fastjson.JSON;
import com.along101.logmetric.server.handler.log.LazyProducer;
import com.along101.logmetric.server.utils.Constant;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@EnableConfigurationProperties(KafkaProperties.class)
public class ValidateController {
    @Autowired
    private KafkaProperties properties;
    private LazyProducer lazyProducer;

    @PostConstruct
    public void init() {
        lazyProducer = new LazyProducer(this.properties.buildProducerProperties());
    }

    @PreDestroy
    public void destroy() {
        lazyProducer.get().close();
    }

    @RequestMapping("/sendtest")
    public String test(@RequestParam("topic") String topic, @RequestParam("count") int count) throws UnsupportedEncodingException {
        for (int i = 0; i < count; i++) {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("appId", "testSendJsonLog");
            logMap.put("message", "test message");
            logMap.put("level", "info");
            logMap.put("timeStamp", System.currentTimeMillis());
            logMap.put("logName", "test.log");
            //单条发送
            String jsonStr = JSON.toJSONString(logMap);
            ProducerRecord<byte[], byte[]> record = new ProducerRecord<>(topic, jsonStr.getBytes(Constant.UTF_8));
            lazyProducer.get().send(record);
        }
        return "OK";
    }

}
