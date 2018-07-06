package com.along101.logmetric.config;

import java.util.Map;

import org.w3c.dom.Element;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetricConfig extends BaseRootConfig{

	private String server;
	
	private String storageType;

	public Map<String,String> getConfigs(){
		Map<String,String> map = Maps.newHashMap();
		if(getOthers() != null){
			for(Element e:getOthers()){
				map.put(e.getNodeName(), e.getFirstChild().getTextContent());
			}
		}
		return map;
	}
	
	
}
