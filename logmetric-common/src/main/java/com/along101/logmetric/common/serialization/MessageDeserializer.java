package com.along101.logmetric.common.serialization;

import com.along101.logmetric.common.bean.Message;
import com.along101.logmetric.common.util.KryoUtil;
import org.apache.kafka.common.serialization.Deserializer;


import java.util.Map;

/**
 * Created by yinzuolong on 2017/3/10.
 */
public class MessageDeserializer implements Deserializer<Message> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public Message deserialize(String topic, byte[] data) {
        return KryoUtil.deserialize(data, Message.class);
    }

    @Override
    public void close() {

    }
}
