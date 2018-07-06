package com.along101.logmetric.client;

import java.util.concurrent.ConcurrentMap;

import com.along101.logmetric.adapter.DefaultLoggerAdapter;
import com.along101.logmetric.common.ILogger;
import com.along101.logmetric.core.DataBufferWrapper;
import com.along101.logmetric.send.LogEventReporter;
import com.google.common.collect.Maps;

public class LoggerFactory {
	private static ConcurrentMap<String, ILogger> loggerMap = Maps.newConcurrentMap();
	
	static{
		DataBufferWrapper.getInstance().registerReporter(new LogEventReporter());
	}
	
	public static ILogger getLogger(String name){
        ILogger logger = loggerMap.get(name);
        if (logger != null) {
            return logger;
        } else {
        	ILogger newInstance = new DefaultLoggerAdapter(name);
        	ILogger oldInstance = loggerMap.putIfAbsent(name, newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
	}
	
	public static ILogger getLogger(Class<?> clazz){
        ILogger logger = loggerMap.get(clazz.getName());
        if (logger != null) {
            return logger;
        } else {
        	ILogger newInstance = new DefaultLoggerAdapter(clazz.getName());
        	ILogger oldInstance = loggerMap.putIfAbsent(clazz.getName(), newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
	}
}
