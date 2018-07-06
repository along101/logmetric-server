package com.along101.logmetric.server.handler;

import com.along101.logmetric.common.bean.Message;
import com.along101.logmetric.common.util.KryoUtil;
import com.along101.logmetric.server.LogmetricServer;
import com.along101.logmetric.server.TestUtils;
import com.along101.logmetric.server.handler.log.KryoLogHandler;
import com.along101.logmetric.server.worker.MessageBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by yinzuolong on 2017/3/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LogmetricServer.class)
public class KryoLogHandlerTest {
    @Autowired
    private KryoLogHandler kryoLogHandler;

    @Test
    public void testHandle() {
        Message message = TestUtils.createLogMessage("KryoLogHandlerTest");
        MessageBox messageBox = MessageBox.create(KryoUtil.serialize(message));
        messageBox.addTag("topic", "testhandle");
        kryoLogHandler.handle(Arrays.asList(messageBox));
    }

    @Test
    public void testMultiThread() throws InterruptedException {
        Message message = TestUtils.createLogMessage("KryoLogHandlerTest");
        final MessageBox messageBox = MessageBox.create(KryoUtil.serialize(message));
        ExecutorService pool = Executors.newFixedThreadPool(1000);
        for (int i = 0; i < 300; i++) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 1000000; j++) {
                        kryoLogHandler.handle(Arrays.asList(messageBox));
                        if (j % 100 == 0)
                            System.out.println(j);
                    }
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(100, TimeUnit.SECONDS);
    }

}
