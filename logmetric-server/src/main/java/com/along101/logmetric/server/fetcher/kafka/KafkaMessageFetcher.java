package com.along101.logmetric.server.fetcher.kafka;

import com.along101.logmetric.server.monitor.metrics.MetricService;
import com.along101.logmetric.server.monitor.metrics.TagMetricBuilder;
import com.along101.logmetric.server.worker.AbstractMessageFetcher;
import com.along101.logmetric.server.worker.MessageBox;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.along101.logmetric.common.util.IPUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yinzuolong on 2017/3/14.
 */
@Slf4j
@Component("fetcher.kafka")
@Scope("prototype")
@ConfigurationProperties(prefix = "logmetric.fetcher.kafka")
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaMessageFetcher extends AbstractMessageFetcher {

    @Autowired
    private MetricService metricWriter;
    @Autowired
    private KafkaProperties properties;

    private KafkaConsumer<String, byte[]> consumer;
    @Setter
    private int timeout = 1000;

    @Override
    public synchronized void connect(List<String> topics) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        Map<String, Object> configs = this.properties.buildConsumerProperties();
        if (this.config != null) {
            configs.putAll(this.config);
        }
        configs.put(ConsumerConfig.CLIENT_ID_CONFIG, this.name + "/" + IPUtil.getLocalIP());
        consumer = new KafkaConsumer<>(configs);
        consumer.subscribe(topics);
        log.info("consumer {} connected.", consumer);
    }

    @Override
    public List<MessageBox> fetch() {
        ConsumerRecords<String, byte[]> records = consumer.poll(timeout);
        List<MessageBox> messages = new ArrayList<>();
        if (records.count() > 0) {
            log.debug("{} consumer {} fetch {} messages.", name, consumer, records.count());
            for (ConsumerRecord<String, byte[]> record : records) {
                log.debug("{} : topic = {}, partition = {}, offset = {}, key = {}, value = {}", name, record.topic(), record.partition(), record.offset(), record.key(), record.value());
                messages.add(buildMessageBox(record));
                metricWriter.increment(TagMetricBuilder.create("counter.fetch", 1)
                        .addTag("name", this.name).addTag("topic", record.topic()).toDelta());
            }
        }
        return messages;
    }

    private MessageBox buildMessageBox(ConsumerRecord<String, byte[]> record) {
        return MessageBox.create(record.value()).addTag("topic", record.topic())
                .addTag("partition", record.partition())
                .addTag("offset", record.offset())
                .addTag("key", record.key());
    }

    @Override
    public synchronized void close() {
        consumer.close();
        log.info("consumer '{}' closed.", name);
    }
}
