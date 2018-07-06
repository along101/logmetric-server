package com.along101.logmetric.server.handler;

import com.alibaba.fastjson.JSON;
import com.along101.logmetric.common.bean.LogEvent;
import com.along101.logmetric.server.LogmetricServer;
import com.along101.logmetric.server.TestUtils;
import com.along101.logmetric.server.handler.log.FailIndexLogHandler;
import com.along101.logmetric.server.handler.log.LogMessage;
import com.along101.logmetric.server.utils.Constant;
import com.along101.logmetric.server.worker.MessageBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * Created by yinzuolong on 2017/6/3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LogmetricServer.class)
public class FailIndexLogHandlerTest {
    @Autowired
    private FailIndexLogHandler failIndexLogHandler;

    private List<LogMessage> createLogMessage() {
        List<LogMessage> arrays = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LogEvent logEvent = TestUtils.createTestLogEvent("test fail index log handler" + i);
            LogMessage logMessage = new LogMessage();
            logMessage.setAppId("FailIndexLogHandlerTest");
            logMessage.setMessage(logEvent.getMessage());
            logMessage.setLevel(logEvent.getLevel().getName());
            logMessage.setTimeStampByLong(logEvent.getTimeStamp().getTime());
            logMessage.setLogName(logEvent.getLogName());
            logMessage.putAllTags(logEvent.getCopyOfTags());
            logMessage.setStackTrace(logEvent.getStackTrace());
            arrays.add(logMessage);
        }
        return arrays;
    }

    @Test
    public void testHandle() throws Exception {
        List<MessageBox> messageBoxes = new ArrayList<>();
        List<LogMessage> arrays = createLogMessage();
        String jsonStr = JSON.toJSONString(arrays);
        MessageBox messageBox = MessageBox.create(jsonStr.getBytes(Constant.UTF_8));
        messageBox.addTag("topic", "framework.log.fail");
        messageBoxes.add(messageBox);
        failIndexLogHandler.handle(messageBoxes);
    }

    @Test
    public void testSendKafka() throws Exception {
        failIndexLogHandler.sendKafka(createLogMessage());
    }
}
