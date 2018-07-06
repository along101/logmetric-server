package com.along101.logmetric.common;

public class Constants {
	
	
	public static final String XmlLogMetric = "logmetric.xml";
	
	//kafa配置
	public static final String LogMetricKafka = "logmetric-kafka.properties";
	
	//默认常量
	public static final String DEFAULT_TOPIC = "logmetric";
	public static final int DEFAULT_WORKER_SIZE = 4;
	public static final int DEFAULT_WORKER_BUFFER_SIZE = 4;
	public static final long DEFAULT_FETCH_TIMEOUT = 50;
	public static final int DEFAULT_FETCH_SIZE = 100;
	public static final int DEFAULT_BUFFER_SIZE = 4096;
	
	//metric配置
	public static final String MetricTopic = "topic";
	
	//log配置
	public static final String LogTopic = "topic";
	
	//Topic配置
	public static final String DefaultTopic = "topic";

}
