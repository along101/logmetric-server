package com.along101.logmetric.client;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.along101.logmetric.common.IMetric;
import com.google.common.collect.Maps;

public class MultiThreadTest {

	public static void main(String args[]) {

		final IMetric metric = MetricFactory.getInstance().getMetric();
		final Map<String, String> tags = Maps.newHashMap();
		tags.put("aa", "sss");
		int threadSize = 20;
		final CountDownLatch c = new CountDownLatch(threadSize);

		int i = 0;
		final AtomicInteger count = new AtomicInteger(0);
		while (i++ < threadSize) {
			new Thread() {
				public void run() {
					int n = 0;
					while (n++ < 1000) {
						metric.log("test.metric", tags, new Random().nextInt(20));
						count.getAndIncrement();
					}
					c.countDown();
				}
			}.start();
		}
		
		try {
			c.await();
			System.out.println(count.get());
			System.out.println("end");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
