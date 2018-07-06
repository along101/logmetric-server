package com.along101.logmetric.server.cat;

import com.along101.logmetric.common.util.SleepUtil;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.junit.Test;

import java.util.Random;

/**
 * cat监控埋点测试
 * Created by yinzuolong on 2017/3/24.
 */
public class CatClientTest {

    @Test
    public void testTransaction() {
        for (int i = 0; i < 100; i++) {
            Transaction t = Cat.newTransaction("test", "logmetricTest");
            SleepUtil.sleep(new Random().nextInt(10) * 10);
            t.setStatus(Transaction.SUCCESS);
            t.complete();
            System.out.println("testTransaction:" + i);
        }
    }


    @Test
    public void testEvent() {
        for (int i = 0; i < 100; i++) {
            SleepUtil.sleep(new Random().nextInt(10) * 10);
            Cat.logEvent("test", "test event");
            System.out.println("testEvent:" + i);
        }
    }

    @Test
    public void testErrorEvent() {
        for (int i = 0; i < 100; i++) {
            SleepUtil.sleep(new Random().nextInt(10) * 10);
            Cat.logError("test error" + i, new RuntimeException("test error."));
            System.out.println("testErrorEvent:" + i);
        }
    }
}
