package com.along101.logmetric.server.handler;

import com.alibaba.fastjson.JSON;
import com.along101.logmetric.common.bean.Message;
import com.along101.logmetric.common.bean.Metric;
import com.along101.logmetric.common.util.KryoUtil;
import com.along101.logmetric.server.LogmetricServer;
import com.along101.logmetric.server.TestUtils;
import com.along101.logmetric.server.handler.metric.KairosdbHandler;
import com.along101.logmetric.server.utils.Constant;
import com.along101.logmetric.server.worker.MessageBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by yinzuolong on 2017/3/30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LogmetricServer.class)
public class KairosdbHandlerTest {

    @Autowired
    private KairosdbHandler kairosdbHandler;

    @Test
    public void testHandle() throws Exception {
        Message message = TestUtils.createMetricMessage("test", "testMetricKryo");
        MessageBox messageBox = MessageBox.create(KryoUtil.serialize(message));
        messageBox.addTag("topic", "testhandle");
        kairosdbHandler.handle(Arrays.asList(messageBox));
    }

    @Test
    public void testJsonMetric() throws Exception {
        Metric metric = TestUtils.createMetric("testMetricJson", new Random().nextInt(10));
        MessageBox messageBox = MessageBox.create(JSON.toJSONString(metric).getBytes(Constant.UTF_8));
        kairosdbHandler.handle(Arrays.asList(messageBox));

        TimeUnit.SECONDS.sleep(1);

        List<Metric> metrics = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            metric = TestUtils.createMetric("testMetricJson", new Random().nextInt(10));
            metrics.add(metric);
            TimeUnit.MILLISECONDS.sleep(300);
        }
        messageBox = MessageBox.create(JSON.toJSONString(metrics).getBytes(Constant.UTF_8));
        kairosdbHandler.handle(Arrays.asList(messageBox));
    }
}
