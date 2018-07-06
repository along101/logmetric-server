package com.along101.logmetric.server.handler.log;

import com.alibaba.fastjson.JSON;
import com.along101.logmetric.server.monitor.metrics.MetricService;
import com.along101.logmetric.server.monitor.metrics.TagMetricBuilder;
import com.along101.logmetric.server.worker.IMessageHandler;
import com.along101.logmetric.server.worker.MessageBox;
import com.dianping.cat.Cat;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.UUIDs;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.*;

/**
 * 统计指标包括：写日志数、写入es失败、反序列化失败、写入kafka失败、丢弃数
 * Created by yinzuolong on 2017/5/31.
 */
@Slf4j
public abstract class LogHandler implements IMessageHandler {

    @Autowired
    protected MetricService metricWriter;
    @Resource(name = "elasticClient")
    private Client client;
    @Getter
    @Setter
    private String index;
    @Getter
    @Setter
    private String type="log";
    @Getter
    @Setter
    private int batchSize = 100;
    @Getter
    @Setter
    private String timeStampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    @Getter
    @Setter
    private String indexDateFormat = "yyyy.MM.dd.HH";

    @Override
    public boolean handle(List<MessageBox> messages) {
        List<LogMessage> logMessages = transform(messages);
        if (logMessages == null || logMessages.size() == 0) {
            return false;
        }
        List<LogMessage> temp = new ArrayList<>(batchSize * 2);
        for (int i = 0; i < logMessages.size(); i++) {
            temp.add(logMessages.get(i));
            if (temp.size() >= batchSize) {
                indexLogs(this.index, temp);
                temp.clear();
            }
        }
        if (temp.size() > 0) {
            indexLogs(this.index, temp);
            temp.clear();
        }
        return true;
    }

    private String getIndexNameByDate(String index) {
        return new StringBuilder(index)
                .append("-")
                .append(DateFormatUtils.format(Calendar.getInstance(), indexDateFormat))
                .toString();
    }

    protected abstract String getName();

    protected abstract List<LogMessage> transform(List<MessageBox> messages);

    protected abstract void failedOperate(List<LogMessage> fails);

    public int indexLogs(String index, List<LogMessage> logMessages) {
        String _index = getIndexNameByDate(index);
        Map<String, LogMessage> messageMap = new HashMap<>();
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (LogMessage logMessage : logMessages) {
            if (StringUtils.isBlank(logMessage.getId())) {
                logMessage.setId(UUIDs.base64UUID());
            }
            if (!StringUtils.isBlank(logMessage.getLayoutMessage())) {
                logMessage.setMessage(null);
            }
            messageMap.put(logMessage.getId(), logMessage);
            String json = JSON.toJSONStringWithDateFormat(logMessage, this.timeStampFormat);
            IndexRequestBuilder request = client.prepareIndex(_index, type)
                    .setId(logMessage.getId()).setSource(json, XContentType.JSON);
            bulkRequest.add(request);
            metricWriter.increment(TagMetricBuilder.create("counter.handler.log.indexlogs", 1)
                    .addTag("appId", logMessage.getAppId()).addTag("name", getName()).toDelta());
        }
        List<LogMessage> fails = new ArrayList<>();
        try {
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            log.info("insert {} logs to [{}].", bulkRequest.numberOfActions(), index);
            for (BulkItemResponse item : bulkResponse.getItems()) {
                if (item.isFailed()) {
                    log.error(item.getFailureMessage());
                    LogMessage logMessage = messageMap.get(item.getId());
                    if (logMessage != null) {
                        fails.add(logMessage);
                        metricWriter.increment(TagMetricBuilder.create("counter.handler.log.indexFailed", 1)
                                .addTag("appId", logMessage.getAppId()).addTag("name", getName()).toDelta());
                    }
                }
            }
        } catch (Exception e) {
            log.error("handle log message error.", e);
            Cat.logError("handle log message error.", e);
        }
        if (fails.size() > 0) {
            log.info("error insert {} logs to [{}].", fails.size(), index);
            failedOperate(fails);
        }
        return fails.size();
    }

}
