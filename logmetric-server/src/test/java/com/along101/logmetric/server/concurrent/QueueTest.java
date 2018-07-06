package com.along101.logmetric.server.concurrent;

import com.along101.logmetric.common.util.SleepUtil;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 测试LinkedBlockingQueue阻塞队列
 * Created by along on 2017/3/19.
 */
public class QueueTest {

    @Test
    public void testLinkedBlockingQueue() throws InterruptedException {
        final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(10);
        ExecutorService pool = Executors.newCachedThreadPool();
        pool.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        queue.put(String.valueOf(i));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("put " + i);
                    SleepUtil.sleep(100);
                }
            }
        });
        SleepUtil.sleep(2000);
        pool.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        queue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("take " + i);
                    SleepUtil.sleep(100);
                }
            }
        });
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.MINUTES);
    }
}
