package com.along101.logmetric.server.fetcher.kafka;

import com.along101.logmetric.common.bean.Message;
import com.along101.logmetric.common.util.KryoUtil;
import com.along101.logmetric.server.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 序列化消息、消息内容测试
 * Created by yinzuolong on 2017/3/16.
 */
public class MessageSerializeTest {

    @Test
    public void testSerializeList() {
        List<String> logs = new ArrayList<String>();
        int count = new Random().nextInt(10) + 1;
        for (int i = 0; i < count; i++) {
            String text = "this is a test message : test-" + i;
            logs.add(text);
        }

        byte[] data = KryoUtil.serialize(logs);
        ArrayList newLogs = KryoUtil.deserialize(data, ArrayList.class);
        Assert.assertEquals(count, newLogs.size());
    }


    @Test
    public void testSerializeLogMessage() {
        Message logMsg = TestUtils.createLogMessage("testLog");
        byte[] data = logMsg.getPayload();
        ArrayList newLogs = KryoUtil.deserialize(data, ArrayList.class);
        Assert.assertTrue(newLogs.size() > 0);
    }


    @Test
    public void testSerializeMerticMessage() throws Exception {
        Message logMsg = TestUtils.createMetricMessage("testMetric", "testMetric");
        byte[] data = logMsg.getPayload();
        ArrayList newLogs = KryoUtil.deserialize(data, ArrayList.class);
        Assert.assertTrue(newLogs.size() > 0);
    }
}
