package com.along101.logmetric.common.core;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class RingBufferTest {
	@Test
	public void testMiss() {
		int bufSize = 128;
		int size = 1000;
		int writeIdx = 0;
		int miss = 0;
		RingBuffer<Integer> buffer = new RingBuffer<Integer>(bufSize);
		while (writeIdx < size) {
			writeIdx++;
			miss += buffer.add(writeIdx);
		}

		Assert.assertEquals(miss, size - bufSize);
		System.out.println(miss + ":" + (size - bufSize));
	}

	@Test
	public void testMultiMiss() {
		int bufSize = 128;
		final int size = 1000;
		int threadsize = 500;
		final AtomicInteger miss = new AtomicInteger(0);
		final RingBuffer<Integer> buffer = new RingBuffer<Integer>(bufSize);
		int i = 0;
		final CountDownLatch c = new CountDownLatch(threadsize);
		while (i < threadsize) {
			new Thread() {
				public void run() {
					int j = 0;
					while (j < size) {
						j++;
						if (1 == buffer.add(j)) {
							miss.incrementAndGet();
						}
					}
					c.countDown();
				}
			}.start();
			i++;
		}
		try {
			c.await();

			Assert.assertEquals(miss.get(), size * threadsize - bufSize);
			System.out.println(miss.get() + ":" + (size * threadsize - bufSize));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testCount() {
		int size = 10;
		int readIdx = 0;
		int writeIdx = 0;
		int miss = 0;
		RingBuffer<Integer> buffer = new RingBuffer<Integer>(1024);

		while (writeIdx < size) {
			writeIdx++;
			miss += buffer.add(writeIdx);
		}

		int success = size - miss;

		while (true) {
			List<Integer> list = buffer.takeForTimeout(50, 50);
			if (!list.isEmpty()) {
				readIdx += list.size();
			} else {
				break;
			}
		}

		Assert.assertEquals(success, readIdx);
		System.out.println("ok");
	}

	@Test
	public void testOverReadCount() {
		int size = 100000;
		int readIdx = 0;
		int writeIdx = 0;
		int miss = 0;
		RingBuffer<Integer> buffer = new RingBuffer<Integer>(64);

		while (writeIdx < size) {
			writeIdx++;
			miss += buffer.add(writeIdx);

		}

		int success = size - miss;
		List<Integer> result = Lists.newArrayList();
		while (true) {
			List<Integer> list = buffer.takeForTimeout(10, 50);
			if (!list.isEmpty()) {
				readIdx += list.size();
				if (readIdx == 32) {
					System.out.println("fdsafasfasd");
				}
				result.addAll(list);
			} else {
				break;
			}
		}

		Assert.assertEquals(success, readIdx);
		System.out.println("ok");
	}

	@Test
	public void testMultiReadCount() {
		int size = 10000;
		final AtomicInteger readIdx = new AtomicInteger(0);
		int writeIdx = 0;
		int miss = 0;
		final RingBuffer<Integer> buffer = new RingBuffer<Integer>(8);

		while (writeIdx < size) {
			writeIdx++;
			miss += buffer.add(writeIdx);

		}

		int success = size - miss;

		int j = 0;
		int threadsize = 1;
		final CountDownLatch c = new CountDownLatch(threadsize);

		while (j < threadsize) {
			new Thread("T" + j) {
				public void run() {
					while (true) {
						List<Integer> list = buffer.takeForTimeout(10, 50);
						if (!list.isEmpty()) {
							readIdx.addAndGet(list.size());
							System.out.println(getName() + ":" + list);
						} else {
							break;
						}
					}
					c.countDown();
				}
			}.start();

			j++;
		}
		try {
			c.await();

			Assert.assertEquals(success, readIdx.get());
			System.out.println("multi:" + success + ":" + readIdx.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testArray() {
		int size = 100;
		int readIdx = 0;
		int writeIdx = 0;
		int miss = 0;
		RingBuffer<Integer> buffer = new RingBuffer<Integer>(1024);
		List<Integer> writeList = Lists.newArrayList();
		while (writeIdx < size) {
			writeIdx++;
			if (buffer.add(writeIdx) == 0) {
				writeList.add(writeIdx);
			} else {
				miss++;
			}
		}

		List<Integer> readList = Lists.newArrayList();
		while (true) {
			List<Integer> list = buffer.takeForTimeout(50, 50);
			if (!list.isEmpty()) {
				readList.addAll(list);
				readIdx += list.size();
				if (readIdx > 36) {
					System.out.println("fdsafasfasd");
				}
			} else {
				break;
			}
		}
		System.out.println(writeList);
		System.out.println(readList);
		Assert.assertArrayEquals(writeList.toArray(), readList.toArray());
		

		System.out.println("ok");
	}
	
	
	@Test
	public void testOverArray() {
		int size = 100;
		int readIdx = 0;
		int writeIdx = 0;
		int miss = 0;
		RingBuffer<Integer> buffer = new RingBuffer<Integer>(64);
		Set<Integer> writeSet = Sets.newHashSet();

		while (writeIdx < size) {
			writeIdx++;
			Integer old = buffer.addAndGet(writeIdx);
			if (old == null) {
				writeSet.add(writeIdx);
			} else {
				miss++;
				writeSet.remove(old);
				writeSet.add(writeIdx);
			}
		}
		

		Set<Integer> readSet =  Sets.newHashSet();
		while (true) {
			List<Integer> list = buffer.takeForTimeout(50, 50);
			if (!list.isEmpty()) {
				readSet.addAll(list);
				readIdx += list.size();
				if (readIdx > 36) {
					System.out.println("fdsafasfasd");
				}
			} else {
				break;
			}
		}

				
		for(Integer n:writeSet){
			if(!readSet.contains(n)){
				Assert.fail();
			}
		}
		Assert.assertEquals(writeSet.size(), readSet.size());
	}

}
