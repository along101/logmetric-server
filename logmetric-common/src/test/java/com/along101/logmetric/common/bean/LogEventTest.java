package com.along101.logmetric.common.bean;

import com.along101.logmetric.common.util.KryoUtil;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by yinzuolong on 2017/4/11.
 */
public class LogEventTest {

    @Test
    public void testLogEvent() {
        long n = System.currentTimeMillis();
        int count = 1000000;
        for (int i = 0; i < count; i++) {
            LogEvent log = createLog();
            KryoUtil.serialize(log);
        }
        System.out.println(System.currentTimeMillis() - n);
    }

    @Test
    public void testMultiThread() throws InterruptedException {
        long n = System.currentTimeMillis();
        final int count = 100000;
        int threads = 100;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < count; i++) {
                        LogEvent log = createLog();
                        KryoUtil.serialize(log);
                    }
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.HOURS);

        System.out.println(System.currentTimeMillis() - n);
    }

    private LogEvent createLog() {
        LogEvent log = new LogEvent();
        try {
            int b = 1 / 0;
        } catch (Exception e) {
            log.setExceptionItem(e);
            log.setLevel(LogLevel.ERROR);
            log.setTimeStamp(System.currentTimeMillis());
            log.setMessage(e.getMessage());
        }
        return log;
    }
}
