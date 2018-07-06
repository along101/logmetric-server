package com.along101.logmetric.common.serialization;

import com.along101.logmetric.common.bean.Message;
import com.along101.logmetric.common.util.KryoUtil;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Created by yinzuolong on 2017/3/10.
 */
public class MessageSerializer implements Serializer<Message> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, Message data) {
    	return KryoUtil.serialize(data);
    }

    @Override
    public void close() {

    }
}
