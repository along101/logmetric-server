package com.along101.logmetric.server.handler.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.along101.logmetric.server.monitor.metrics.TagMetricBuilder;
import com.along101.logmetric.server.utils.Constant;
import com.along101.logmetric.server.worker.MessageBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinzuolong on 2017/5/20.
 */
@Slf4j
@Component("handler.jsonlog")
@ConfigurationProperties(prefix = "logmetric.handler.jsonlog")
public class JsonLogHandler extends LogHandler {

    @Autowired
    private FailIndexLogHandler failIndexLogHandler;

    @Override
    protected String getName() {
        return "handler.jsonlog";
    }

    @Override
    public List<LogMessage> transform(List<MessageBox> messages) {
        List<LogMessage> logMessages = new ArrayList<>();
        for (MessageBox messageBox : messages) {
            if (!(messageBox.getData() instanceof byte[])) {
                continue;
            }
            byte[] data = (byte[]) messageBox.getData();
            if (data.length <= 0 || (data[0] != '{' && data[0] != '[')) {
                continue;
            }
            StringBuilder jsonStr;
            JSONArray jsonArr;
            try {
                jsonStr = new StringBuilder(new String(data, Constant.UTF_8));
                if (data[0] == '{') {
                    jsonStr.insert(0, "[").append("]");
                }
                jsonArr = JSON.parseArray(jsonStr.toString());
            } catch (Exception e) {
                metricWriter.increment(TagMetricBuilder.create("counter.handler.log.deserialize.error", 1)
                        .addTag("name", getName()).toDelta());
                continue;
            }
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject json = jsonArr.getJSONObject(i);
                LogMessage logMessage = createLogMessage(json);
                String appId = StringUtils.isBlank(json.getString("appId")) ? this.getType() : json.getString("appId");
                logMessage.setAppId(appId);
                logMessage.getTags().put("pIndex", i);
                logMessage.getTags().putAll(messageBox.getTags());
                logMessages.add(logMessage);
            }
        }
        return logMessages;

    }

    private LogMessage createLogMessage(JSONObject json) {
        LogMessage logMessage = new LogMessage();
//        logMessage.setMessage(json.getString("message"));
        logMessage.setLevel(json.getString("level"));
        logMessage.setTimeStampByLong(json.getLong("timeStamp"));
        logMessage.setLogName(json.getString("logName"));
        logMessage.setThreadName(json.getString("threadName"));
        logMessage.putAllTags(json.getJSONObject("tags"));
        logMessage.putAllHead(json.getJSONObject("head"));
        logMessage.putAllMdc(json.getJSONObject("mdc"));
        logMessage.setStackTrace(json.getString("stackTrace"));
        logMessage.setLayoutMessage(json.getString("layoutMessage"));
        if (StringUtils.isBlank(logMessage.getLayoutMessage())) {
            logMessage.setMessage(json.getString("message"));
        }
        return logMessage;
    }

    @Override
    protected void failedOperate(List<LogMessage> fails) {
        failIndexLogHandler.sendFailIndexLog(fails);
    }
}
