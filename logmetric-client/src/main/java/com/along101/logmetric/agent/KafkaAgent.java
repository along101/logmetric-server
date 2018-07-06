package com.along101.logmetric.agent;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.along101.logmetric.common.IAgent;
import com.along101.logmetric.common.bean.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaAgent implements IAgent {

	private Producer<String, Message> producer;

	private String topic;
	private long timeout;

	public KafkaAgent(Properties props) {
		this.producer = new KafkaProducer<String, Message>(props);
		if (log.isInfoEnabled()) {
			log.info("Crete kafka producer: " + producer);
		}
	}

	@Override
	public void init(String... configs) {
		this.topic = configs[0];
		this.timeout = Long.parseLong(configs[1]);
	}

	@Override
	public void send(Message msg) {
		msg.setSendTime(System.currentTimeMillis());
		try {
			Future<RecordMetadata> future = producer.send(new ProducerRecord<String, Message>(topic, msg));
			RecordMetadata metadata = future.get(timeout, TimeUnit.MILLISECONDS);
			if (log.isDebugEnabled()) {
				log.info("Send message: {topic:" + metadata.topic() + ",partition:" + metadata.partition() + ",offset:"+ metadata.offset() + ",type:"+msg.getType()+",count:"+msg.getDataCount()+"}");
			}
		} catch (Exception e) {
			throw new KafkaException("Send message error: " + msg, e);
		}
	}

}
