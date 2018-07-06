package com.along101.logmetric.send;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import com.along101.logmetric.agent.AgentFactory;
import com.along101.logmetric.common.IAgent;
import com.along101.logmetric.common.IReporter;
import com.along101.logmetric.common.bean.Message;
import com.along101.logmetric.common.bean.MessageType;
import com.along101.logmetric.common.bean.StorageType;
import com.along101.logmetric.config.LogMetricConfigManager;

public class LogEventReporter implements IReporter {

	private IAgent agent;
	
	public LogEventReporter(){
		Properties props = LogMetricConfigManager.instance().getKafkaProperties();
		props.setProperty("bootstrap.servers", LogMetricConfigManager.instance().getLogServer());
		this.agent = AgentFactory.instance().getAgent(StorageType.Kafka,props);
		this.agent.init(LogMetricConfigManager.instance().getLogTopic(),"60000");
	}
	
	public LogEventReporter(String topic,Properties props){
		this.agent = AgentFactory.instance().getAgent(StorageType.Kafka,props);
		this.agent.init(topic,"60000");
	}
	private AtomicInteger count = new AtomicInteger(0);

	
	@Override
	public void process(Message msg) {
		if(msg.getType().ordinal() == MessageType.LOG.ordinal()){
			agent.send(msg);
		}
	}

	public int hashCode(){
		return 0;
	}
}
