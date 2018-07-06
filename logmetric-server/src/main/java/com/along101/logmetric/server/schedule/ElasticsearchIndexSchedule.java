package com.along101.logmetric.server.schedule;

import com.alibaba.fastjson.JSON;
import com.along101.logmetric.server.handler.log.JsonLogHandler;
import com.along101.logmetric.server.handler.log.LogMessage;
import com.along101.logmetric.server.monitor.metrics.MetricService;
import com.along101.logmetric.server.monitor.metrics.TagMetricBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yinzuolong on 2017/9/14.
 */
@Component
@Slf4j
public class ElasticsearchIndexSchedule {

    @Autowired
    private MetricService metricWriter;
    @Autowired
    private JsonLogHandler jsonLogHandler;
    @Resource(name = "elasticClient")
    private Client client;

    @Scheduled(cron = "0 * * * * ?")
    public void createIndex() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            String dateStr = DateFormatUtils.format(calendar, jsonLogHandler.getIndexDateFormat());
            String index = jsonLogHandler.getIndex() + "-" + dateStr;
            LogMessage logMessage = createTest("test");
            String json = JSON.toJSONStringWithDateFormat(logMessage, jsonLogHandler.getTimeStampFormat());
            IndexRequestBuilder requestBuilder = client.prepareIndex(index, logMessage.getAppId())
                    .setId(logMessage.getId()).setSource(json, XContentType.JSON);
            client.index(requestBuilder.request());
            metricWriter.increment(TagMetricBuilder.create("counter.testlog", 1).toDelta());
            log.info("index test log");
        } catch (Exception e) {
            log.error("index test log error.", e);
        }
    }

    private LogMessage createTest(String message) {
        LogMessage logMessage = new LogMessage();
        logMessage.setAppId("logmetric");
        logMessage.setMessage(message);
        logMessage.setLogName(this.getClass().getName());
        logMessage.setTimeStamp(new Date());
        return logMessage;
    }
}
