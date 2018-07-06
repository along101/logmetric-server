package com.along101.logmetric.server.handler.log;

import com.alibaba.fastjson.JSON;
import com.along101.logmetric.common.bean.Header;
import com.along101.logmetric.common.bean.LogEvent;
import com.along101.logmetric.common.bean.Message;
import com.along101.logmetric.common.util.KryoUtil;
import com.along101.logmetric.server.monitor.metrics.TagMetricBuilder;
import com.along101.logmetric.server.worker.MessageBox;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志写入Elasticsearch
 * Created by yinzuolong on 2017/3/14.
 */
@Slf4j
@Component("handler.kryoLog")
@ConfigurationProperties(prefix = "logmetric.handler.kryoLog")
public class KryoLogHandler extends LogHandler {

    @Autowired
    private FailIndexLogHandler failIndexLogHandler;

    @Override
    protected String getName() {
        return "handler.kryoLog";
    }

    @Override
    public List<LogMessage> transform(List<MessageBox> messages) {
        List<LogMessage> logMessages = new ArrayList<>();
        for (MessageBox messageBox : messages) {
            if (!(messageBox.getData() instanceof byte[])) {
                continue;
            }
            byte[] data = (byte[]) messageBox.getData();
            if (data.length <= 0 || data[0] == '{' || data[0] == '[') {
                continue;
            }
            Message message;
            try {
                message = KryoUtil.deserialize((byte[]) messageBox.getData(), Message.class);
            } catch (Exception e) {
                metricWriter.increment(TagMetricBuilder.create("counter.handler.log.deserialize.error", 1)
                        .addTag("topic", messageBox.getTag("topic")).addTag("name", getName()).toDelta());
                continue;
            }
            String appId = StringUtils.isBlank(message.getAppId()) ? this.getType() : message.getAppId();
            ArrayList<LogEvent> logs = null;
            try {
                logs = KryoUtil.deserialize(message.getPayload(), ArrayList.class);
            } catch (Exception e) {
                Cat.logEvent("message.payload.error", message.getAppId() + JSON.toJSONString(message.getHeader()));
                Cat.logError(JSON.toJSONString(message), e);
                continue;
            }
            int pIndex = 0;
            for (LogEvent logEvent : logs) {
                LogMessage logMessage = createLogMessage(logEvent);
                logMessage.setAppId(appId);
                if (message.getHeader() == null) {
                    message.setHeader(new Header());
                }
                message.getHeader().put("pIndex", pIndex++);
                logMessage.putAllHead(message.getHeader());
                logMessages.add(logMessage);
            }
        }
        return logMessages;
    }

    private LogMessage createLogMessage(LogEvent logEvent) {
        LogMessage logMessage = new LogMessage();
        logMessage.setMessage(logEvent.getMessage());
        logMessage.setLevel(logEvent.getLevel().getName());
        logMessage.setTimeStamp(logEvent.getTimeStamp());
        logMessage.setLogName(logEvent.getLogName());
        logMessage.putAllTags(logEvent.getCopyOfTags());
        logMessage.setStackTrace(logEvent.getStackTrace());
        return logMessage;
    }

    @Override
    protected void failedOperate(List<LogMessage> fails) {
        failIndexLogHandler.sendFailIndexLog(fails);
    }
}
