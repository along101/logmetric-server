package com.along101.logmetric.server.handler;

import com.alibaba.fastjson.JSON;
import com.along101.logmetric.common.bean.LogEvent;
import com.along101.logmetric.server.LogmetricServer;
import com.along101.logmetric.server.TestUtils;
import com.along101.logmetric.server.handler.log.JsonLogHandler;
import com.along101.logmetric.server.utils.Constant;
import com.along101.logmetric.server.worker.MessageBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yinzuolong on 2017/5/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LogmetricServer.class)
public class JsonLogHandlerTest {
    @Autowired
    private JsonLogHandler jsonLogHandler;

    @Test
    public void testJsonObject() throws UnsupportedEncodingException {
        List<MessageBox> messageBoxes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LogEvent logEvent = TestUtils.createTestLogEvent("testJson" + i);
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("appId","testJsonObject");
            logMap.put("message", logEvent.getMessage());
            logMap.put("level", logEvent.getLevel());
            logMap.put("timeStamp", System.currentTimeMillis());
            logMap.put("logName", logEvent.getLogName());
            logMap.put("tags", logEvent.getCopyOfTags());
            logMap.put("stackTrace", logEvent.getStackTrace());
            String jsonStr = JSON.toJSONString(logMap);
            MessageBox messageBox = MessageBox.create(jsonStr.getBytes(Constant.UTF_8));
            messageBox.addTag("topic", "jsonHandler");
            messageBoxes.add(messageBox);
        }
        jsonLogHandler.handle(messageBoxes);
    }

    @Test
    public void testJsonArray() throws UnsupportedEncodingException {
        List<MessageBox> messageBoxes = new ArrayList<>();
        List<Map<String, Object>> arrays = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LogEvent logEvent = TestUtils.createTestLogEvent("testJson" + i);
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("appId","testJsonArray");
            logMap.put("message", logEvent.getMessage());
            logMap.put("level", logEvent.getLevel());
            logMap.put("timeStamp", System.currentTimeMillis());
            logMap.put("logName", logEvent.getLogName());
            logMap.put("tags", logEvent.getCopyOfTags());
            logMap.put("stackTrace", logEvent.getStackTrace());
            arrays.add(logMap);
        }
        String jsonStr = JSON.toJSONString(arrays);
        MessageBox messageBox = MessageBox.create(jsonStr.getBytes(Constant.UTF_8));
        messageBox.addTag("topic", "jsonHandler");
        messageBoxes.add(messageBox);
        jsonLogHandler.handle(messageBoxes);
    }
}
