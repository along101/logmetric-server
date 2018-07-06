package com.along101.logmetric.common.core;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.along101.logmetric.common.util.SleepUtil;
import com.google.common.collect.Lists;

public class RingBuffer<E> implements Iterator {

	private AtomicLong writeIndex = new AtomicLong(-1);
	private AtomicLong readIndex = new AtomicLong(-1);

	private AtomicReference<E>[] buffer;

	private int MASK = 0;

	public RingBuffer(int bufferSize) {
		if (Integer.bitCount(bufferSize) != 1) {
			throw new IllegalArgumentException("bufferSize must be a power of 2");
		}
		this.buffer = new AtomicReference[bufferSize];
		this.MASK = bufferSize - 1;
		for (int i = 0; i < bufferSize; i++) {
			this.buffer[i] = new AtomicReference<E>();
		}

	}

	private int step(AtomicLong curIndex) {
		return step(curIndex, 1);
	}

	private int step(AtomicLong curIndex, long n) {
		return (int) (curIndex.addAndGet(n) & MASK);
	}

	/**
	 * 添加数据，
	 * 
	 * @param e
	 * @return 返回1，意味着丢数据
	 */
	public int add(E e) {
		int miss = 0;
		E old = this.buffer[step(this.writeIndex)].getAndSet(e);
		if (old != null) {
			miss = 1;
		}
		return miss;
	}
	
	public E addAndGet(E e) {
		return this.buffer[step(this.writeIndex)].getAndSet(e);
	}
	/**
	 * 
	 * @param list
	 * @return 返回丢掉的数据条数
	 */
	public int add(List<E> list) {
		int miss = 0;
		for (E e : list) {
			miss += add(e);
		}
		return miss;
	}

	public E takeForTimeout(long timeout) {
		int next = step(this.readIndex);

		E old = this.buffer[next].getAndSet(null);

		if (old == null) {
			SleepUtil.sleep(timeout);
			old = this.buffer[next].getAndSet(null);
			if (old == null) {
				readIndex.decrementAndGet();
			}
		}
		return old;
	}

	public List<E> takeForTimeout(int n, long timeout) {
		List<E> list = Lists.newArrayList();

		for (int i = 0; i < n; i++) {
			E e = takeForTimeout(timeout);
			if (null != e) {
				list.add(e);
			} else {
				break;
			}
		}
		return list;
	}

	@Override
	public boolean hasNext() {
		return this.writeIndex.get() > this.readIndex.get();
	}

	@Override
	public Object next() {
		int next = step(this.readIndex);
		return this.buffer[next];
	}

	@Override
	public void remove() {
	}
}
