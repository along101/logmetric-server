package com.along101.logmetric.client;

import com.along101.logmetric.adapter.DefaultMetricAdapter;
import com.along101.logmetric.common.IMetric;
import com.along101.logmetric.config.LogMetricConfigManager;
import com.along101.logmetric.core.DataBufferWrapper;
import com.along101.logmetric.send.MetricReporter;
import com.google.common.base.Strings;


public class MetricFactory {
	
	private static MetricFactory instance;
		
	private IMetric metric;
	
	private MetricFactory(){
		if(Strings.isNullOrEmpty(LogMetricConfigManager.instance().getAppId())){
			throw new RuntimeException("appId is null!");
		}
		this.metric = new DefaultMetricAdapter();
		DataBufferWrapper.getInstance().registerReporter(new MetricReporter());
	}
	
	public static MetricFactory getInstance(){
		if(instance == null){
			synchronized(MetricFactory.class){
				instance = new MetricFactory();				
			}
		}
		return instance;
	}

	
	public IMetric getMetric(){		
		return metric;
	}
	
	
}
