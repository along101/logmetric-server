package com.along101.logmetric.config;

import java.util.Map;

import com.along101.logmetric.common.Constants;
import org.w3c.dom.Element;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingConfig extends BaseRootConfig{

	private String topic  = Constants.DEFAULT_TOPIC;
	
	private int workerSize  = Constants.DEFAULT_WORKER_SIZE;
	
	private int bufferSize = Constants.DEFAULT_BUFFER_SIZE;
	
	private int workerBufferSize = Constants.DEFAULT_WORKER_BUFFER_SIZE;
	
	private int fetchSize = Constants.DEFAULT_FETCH_SIZE;
	
	private long fetchTimeout = Constants.DEFAULT_FETCH_TIMEOUT;

	
	public Map<String,String> getConfigs(){
		Map<String,String> map = Maps.newHashMap();
		if(getOthers() != null){
			for(Element e:getOthers()){
				map.put(e.getNodeName(),  e.getFirstChild().getTextContent());
			}
		}
		return map;
	}
}
