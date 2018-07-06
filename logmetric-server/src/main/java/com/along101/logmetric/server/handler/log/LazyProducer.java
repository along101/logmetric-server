package com.along101.logmetric.server.handler.log;

import ch.qos.logback.core.spi.ContextAwareBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yinzuolong on 2017/5/25.
 */
public class LazyProducer extends ContextAwareBase {

    private volatile Producer<byte[], byte[]> producer;
    private Map<String, Object> producerConfig;

    public LazyProducer(Map<String, Object> producerConfig) {
        this.producerConfig = new HashMap<>(producerConfig);
        this.producerConfig.put("key.serializer", ByteArraySerializer.class.getName());
        this.producerConfig.put("value.serializer", ByteArraySerializer.class.getName());
    }

    public Producer<byte[], byte[]> get() {
        Producer<byte[], byte[]> result = this.producer;
        if (result == null) {
            synchronized (this) {
                result = this.producer;
                if (result == null) {
                    this.producer = result = this.initialize();
                }
            }
        }

        return result;
    }

    protected Producer<byte[], byte[]> initialize() {
        Producer<byte[], byte[]> producer = null;
        try {
            producer = createProducer();
        } catch (Exception e) {
            addError("error creating producer", e);
        }
        return producer;
    }

    public boolean isInitialized() {
        return producer != null;
    }

    protected Producer<byte[], byte[]> createProducer() {
        return new KafkaProducer<>(new HashMap<>(producerConfig));
    }

}
