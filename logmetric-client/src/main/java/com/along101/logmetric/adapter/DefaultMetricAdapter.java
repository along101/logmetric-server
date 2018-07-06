package com.along101.logmetric.adapter;

import java.util.Map;

import com.along101.logmetric.common.IMetric;
import com.along101.logmetric.common.bean.Metric;
import com.along101.logmetric.core.DataBufferWrapper;
import com.along101.logmetric.encode.MetricEncoder;

public class DefaultMetricAdapter implements IMetric {
	

	public DefaultMetricAdapter(){
		DataBufferWrapper.getInstance().registerEncoder(Metric.class, new MetricEncoder());
	}
	
	@Override
	public void log(String name,  Map<String, String> tags, int value) {
		log(name,tags,System.currentTimeMillis(),value);
	}

	@Override
	public void log(String name, Map<String, String> tags, long time, int value) {
		addMetric(new Metric(name,tags,time,value));
	}

	@Override
	public void log(String name, Map<String, String> tags, long value) {
		log(name,tags,System.currentTimeMillis(),value);
	}

	@Override
	public void log(String name,  Map<String, String> tags, long time, long value) {
		addMetric(new Metric(name,tags,time,value));
	}

	@Override
	public void log(String name, Map<String, String> tags, double value) {
		log(name,tags,System.currentTimeMillis(),value);
	}

	@Override
	public void log(String name,  Map<String, String> tags, long time, double value) {
		addMetric(new Metric(name,tags,time,value));
	}

	@Override
	public void log(String name,  Map<String, String> tags, float value) {
		log(name,tags,System.currentTimeMillis(),value);
	}

	@Override
	public void log(String name,  Map<String, String> tags, long time, float value) {
		addMetric(new Metric(name,tags,time,value));
	}

	@Override
	public void addMetric(Metric metric) {
		DataBufferWrapper.getInstance().add(metric);
	}
}
