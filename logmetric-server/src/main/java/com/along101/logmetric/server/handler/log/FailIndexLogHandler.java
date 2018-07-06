package com.along101.logmetric.server.handler.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.along101.logmetric.server.monitor.metrics.TagMetricBuilder;
import com.along101.logmetric.server.utils.Constant;
import com.along101.logmetric.server.worker.MessageBox;
import com.dianping.cat.Cat;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * 写入es失败的日志处理
 * Created by yinzuolong on 2017/5/22.
 */
@Slf4j
@Component("handler.failIndexLog")
@ConfigurationProperties(prefix = "logmetric.handler.failIndexLog")
@EnableConfigurationProperties(KafkaProperties.class)
public class FailIndexLogHandler extends LogHandler {

    @Autowired
    private KafkaProperties properties;
    private LazyProducer lazyProducer;
    @Getter
    @Setter
    private int maxVersion = 5;
    @Getter
    @Setter
    private String topic;

    @PostConstruct
    public void init() {
        lazyProducer = new LazyProducer(this.properties.buildProducerProperties());
    }

    @PreDestroy
    public void destroy() {
        lazyProducer.get().close();
    }

    @Override
    protected String getName() {
        return "handler.failIndexLog";
    }

    public void sendFailIndexLog(List<LogMessage> fails) {
        this.indexLogs(this.getIndex(), fails);
    }

    @Override
    public List<LogMessage> transform(List<MessageBox> messages) {
        List<LogMessage> logMessages = new ArrayList<>();
        for (MessageBox messageBox : messages) {
            if (!(messageBox.getData() instanceof byte[])) {
                continue;
            }
            byte[] data = (byte[]) messageBox.getData();
            try {
                String jsonStr = new String(data, Constant.UTF_8);
                List<LogMessage> lms = JSONObject.parseArray(jsonStr, LogMessage.class);
                for (LogMessage logMessage : lms) {
                    if (logMessage.getVersion() >= maxVersion) {
                        Cat.logEvent("failIndexLog", "discard");
                        metricWriter.increment(TagMetricBuilder.create("counter.handler.log.discard", 1)
                                .addTag("appId", logMessage.getAppId()).addTag("name", getName()).toDelta());
                        continue;
                    }
                    logMessages.addAll(lms);
                }
            } catch (Exception e) {
                metricWriter.increment(TagMetricBuilder.create("counter.handler.log.deserialize.error", 1)
                        .addTag("name", getName()).toDelta());
                continue;
            }
        }
        return logMessages;
    }

    @Override
    protected void failedOperate(List<LogMessage> fails) {
        sendKafka(fails);
    }

    /**
     * 写入kafka
     *
     * @param fails
     */
    public void sendKafka(List<LogMessage> fails) {
        if (fails == null || fails.size() == 0) {
            return;
        }
        //版本号增加
        for (LogMessage logMessage : fails) {
            logMessage.setVersion(logMessage.getVersion() + 1);
        }
        try {
            ProducerRecord<byte[], byte[]> record = new ProducerRecord<>(topic, JSON.toJSONString(fails).getBytes(Constant.UTF_8));
            Future<RecordMetadata> future = lazyProducer.get().send(record);
            future.get();
            metricWriter.increment(TagMetricBuilder.create("counter.handler.failIndexLog.sendKafka", fails.size())
                    .addTag("name", getName()).toDelta());
        } catch (Exception e) {
            metricWriter.increment(TagMetricBuilder.create("counter.handler.failIndexLog.sendKafka.error", fails.size())
                    .addTag("name", getName()).toDelta());
            log.error("send Kafka message error.", e);
            Cat.logError("send Kafka message error.", e);
        }
    }
}
