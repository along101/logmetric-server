package com.along101.logmetric.core;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.along101.logmetric.common.IReporter;
import com.along101.logmetric.common.bean.Message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkerGroup {

	private Worker[] workers;

	private IReporter reporter;

	private volatile boolean running = false;

	private AtomicLong position = new AtomicLong(0);

	public WorkerGroup(String group, int count, int bufferSize) {
		this.workers = new Worker[count];
		for (int i = 0; i < count; i++) {
			this.workers[i] = new Worker(group + i, bufferSize);
			this.workers[i].setDaemon(true);
		}
	}

	public void start() {
		if (!running) {
			this.running = true;
			for (Worker worker : workers) {
				worker.start();
			}
		}
	}

	public void stop() {
		this.running = false;
	}
	
	public void register(IReporter reporter){
		this.reporter = reporter;
	}

	/**
	 * 
	 * @param msg
	 * @return 返回丢失数据的条数
	 */
	public int send(Message msg) {
		int i = 0;
		boolean result = false;
		while (i < workers.length) {
			int index = (int) (position.incrementAndGet() % workers.length);
			// 返回真，表示成功放入队列
			result = workers[index].safePut(msg);

			if (result) {
				break;
			}
		}

		if (!result) {
			return workers[new Random(workers.length).nextInt(workers.length)].forcePut(msg);
		}
		return 0;
	}

	class Worker extends Thread {

		private BlockingQueue<Message> queue;

		public Worker(String name, int bufSize) {
			super(name);
			this.queue = new LinkedBlockingQueue<Message>(bufSize);
		}

		/**
		 * 
		 * @param list
		 * @return 返回丢失的数据条数
		 */
		public int forcePut(Message msg) {
			int miss = 0;
			// 先尝试放一次
			if (!this.safePut(msg)) {

				Message oldMsg = this.queue.remove();
				miss = (oldMsg != null ? oldMsg.getDataCount() : 0);
				if (!this.queue.offer(msg)) {
					log.info("force offer fail.");
					miss += msg.getDataCount();
				}
			}
			return miss;
		}

		/**
		 * 
		 * @return 返回true代表成功放入
		 */
		public boolean safePut(Message msg) {
			return this.queue.offer(msg);
		}

		public void run() {
			while (running) {
				try {
					Message msg = queue.poll(100, TimeUnit.MILLISECONDS);
					if(msg != null){
						// 处理数据数据
						reporter.process(msg);
					}
				} catch (Throwable t) {
					log.error(this.getName() + "process message error!", t);
				}
			}
		}
	}
}
