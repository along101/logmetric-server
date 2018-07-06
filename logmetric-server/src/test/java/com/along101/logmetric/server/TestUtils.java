package com.along101.logmetric.server;

import com.alibaba.fastjson.JSON;
import com.along101.logmetric.common.bean.*;
import com.along101.logmetric.common.util.KryoUtil;
import org.junit.Test;

import java.net.InetAddress;
import java.util.*;

/**
 * Created by yinzuolong on 2017/3/30.
 */
public class TestUtils {

    public static LogEvent createTestLogEvent(String name) {
        LogEvent logEvent = new LogEvent();
        logEvent.setLevel(LogLevel.INFO);
        logEvent.setLogName(name);
        try {
            int b = 1 / 0;
        } catch (Exception e) {
            logEvent.setExceptionItem(e);
        }
        logEvent.setMessage("this is a test info message : " + (new Random().nextInt(10) + 1));
        logEvent.setTimeStamp(new Date());
        return logEvent;
    }

    public static Metric createMetric(String metricName, Object value) throws Exception {
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("host", String.valueOf(InetAddress.getLocalHost()));
        Metric metric = new Metric(metricName, tags, System.currentTimeMillis(), value);
        return metric;
    }

    static public Message createMetricMessage(String name, String metricName) throws Exception {
        Message msg = new Message("test");
        msg.setCreateTime(System.currentTimeMillis());
        Header header = new Header();
        header.put("name", name);
        msg.setHeader(header);
        List<Metric> metrics = new ArrayList<Metric>();
        for (int i = 0; i < 10; i++) {
            int count = new Random().nextInt(10) + 1;
            metrics.add(createMetric(metricName, count));
        }
        msg.setPayload(KryoUtil.serialize(metrics));
        msg.setDataCount(metrics.size());
        msg.setType(MessageType.METRIC);
        return msg;
    }

    static public Message createLogMessage(String name) {
        Message msg = new Message("test");
        msg.setAppId("123456");
        msg.setCreateTime(System.currentTimeMillis());
        msg.setSendTime(System.currentTimeMillis());
        Header header = new Header();
        header.put("level", "error");
        header.put("name", name);
        msg.setHeader(header);
        List<LogEvent> logs = new ArrayList<LogEvent>();
        int count = new Random().nextInt(10) + 1;
        for (int i = 0; i < count; i++) {
            LogEvent logEvent = TestUtils.createTestLogEvent(String.valueOf(i));
            logs.add(logEvent);
        }
        msg.setPayload(KryoUtil.serialize(logs));
        msg.setDataCount(logs.size());
        msg.setType(MessageType.LOG);
        return msg;
    }


    public static void printMessage(Message msg) {
        System.out.print(msg.getDataCount() + "\t");
        switch (msg.getType()) {
            case LOG:
                ArrayList logs = KryoUtil.deserialize(msg.getPayload(), ArrayList.class);
                System.out.println(logs);
                break;
            case METRIC:
                ArrayList metrics = KryoUtil.deserialize(msg.getPayload(), ArrayList.class);
                System.out.println(metrics);
                break;
        }
    }

    @Test
    public void name() throws Exception {

        String str = JSON.toJSONString(createLogMessage("test message."));
        System.out.println(str);
    }
}
