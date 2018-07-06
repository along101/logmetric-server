package com.along101.logmetric.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuration")
public class LogMetricRootConfig extends BaseRootConfig{

	private LogMetricConfig logMetricConfig;
	
    @XmlElement(name = "logmetric")
	public LogMetricConfig getLogMetricConfig(){
		return this.logMetricConfig;
	}

	public void setLogMetricConfig(LogMetricConfig logMetricConfig) {
		this.logMetricConfig = logMetricConfig;
	}
		
}
