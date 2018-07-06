package com.along101.logmetric.agent;

import java.util.Properties;

import com.along101.logmetric.common.IAgent;
import com.along101.logmetric.common.bean.StorageType;

public class AgentFactory {
	
	private static AgentFactory instance;
	
	private AgentFactory(){	
	}
	
	public static AgentFactory instance(){
		if(instance == null){
			synchronized(AgentFactory.class){
				if(instance == null){
					instance = new AgentFactory();
				}
			}
		}
		return instance;
	}
	
	
	
	public IAgent getAgent(StorageType storageType, Properties props){
		IAgent agent = null;
		switch(storageType){
			case InfluxDB:
			case KairosDB:
			case OpenTSDB:
			case Kafka:
				agent = new KafkaAgent(props);
		}
		
		return agent;
	}
}
