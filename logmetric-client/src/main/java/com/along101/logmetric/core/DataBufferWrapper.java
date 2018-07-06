package com.along101.logmetric.core;

import java.util.List;

import javax.annotation.PostConstruct;

import com.along101.logmetric.common.IEncoder;
import com.along101.logmetric.common.IReporter;
import com.along101.logmetric.common.core.RingBuffer;

import com.along101.logmetric.config.LogMetricConfigManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataBufferWrapper implements AutoCloseable {

	private static volatile DataBufferWrapper instance;

	private RingBuffer<Object> buffer;

	private Dispatcher dispatcher;

	private volatile boolean running = true;

	private Thread reportThread;

	private int fetchSize;

	private long fetchTimeout;

	private DataBufferWrapper() {
		this.buffer = new RingBuffer<Object>(LogMetricConfigManager.instance().getBufferSize());
		this.fetchSize = LogMetricConfigManager.instance().getFetchSize();
		this.fetchTimeout = LogMetricConfigManager.instance().getFetchTimeout();

		this.dispatcher = new Dispatcher(LogMetricConfigManager.instance().getWorkerSize(),
				LogMetricConfigManager.instance().getWorkerBufferSize());

		this.reportThread = new Thread("Dispatcher") {
			public void run() {
				reportOperation();
			}
		};
		reportThread.setDaemon(true);
		reportThread.start();
		this.dispatcher.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				try {
					close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static DataBufferWrapper getInstance() {
		if (instance == null) {
			synchronized (DataBufferWrapper.class) {
				if (instance == null) {
					instance = new DataBufferWrapper();
				}
			}
		}
		return instance;
	}

	public int add(Object data) {
		int miss = this.buffer.add(data);
		return miss;
	}

	private void reportOperation() {
		while (this.running) {
			try {
				List<Object> list = this.buffer.takeForTimeout(this.fetchSize, fetchTimeout);
				if (!list.isEmpty()) {
					int miss = this.dispatcher.report(list);
				}
			} catch (Throwable t) {
				log.error("report data error!", t);
			}
		}
	}

	public void registerEncoder(Class<?> clazz, IEncoder handler) {
		this.dispatcher.register(clazz, handler);
	}
	
	public void registerReporter(IReporter reporter) {
		this.dispatcher.addReporter(reporter);
	}

	@PostConstruct
	public void close() throws Exception {
//		this.running = false;
//		this.dispatcher.stop();
	}

}
