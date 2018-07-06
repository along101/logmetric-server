package com.along101.logmetric.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Strings;

public class LogMetricConfig  extends BaseRootConfig{

	private LogConfig logConfig;
	
	private MetricConfig metricConfig;
	
	private SettingConfig settingConfig;
	
	private String appid;

	@XmlElement(name = "logging")
	public LogConfig getLogConfig() {
		return logConfig;
	}

	public void setLogConfig(LogConfig logConfig) {
		this.logConfig = logConfig;
	}

	@XmlElement(name = "metric")
	public MetricConfig getMetricConfig() {
		return metricConfig;
	}
	@XmlElement(name = "setting")
	public SettingConfig getSettingConfig() {
		return settingConfig;
	}
	
	public void setMetricConfig(MetricConfig metricConfig) {
		this.metricConfig = metricConfig;
	}
	
    @XmlAttribute
	public String getAppid() {
    	if(Strings.isNullOrEmpty(appid)){
    		throw new RuntimeException("appid is not null");
    	}
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public void setSettingConfig(SettingConfig settingConfig) {
		this.settingConfig = settingConfig;
	}
	
}
