package com.along101.logmetric.server.handler.metric;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.along101.logmetric.common.bean.Message;
import com.along101.logmetric.common.bean.MessageType;
import com.along101.logmetric.common.bean.Metric;
import com.along101.logmetric.common.util.KryoUtil;
import com.along101.logmetric.server.handler.log.JsonLogHandler;
import com.along101.logmetric.server.handler.log.LogMessage;
import com.along101.logmetric.server.monitor.metrics.MetricService;
import com.along101.logmetric.server.monitor.metrics.TagMetricBuilder;
import com.along101.logmetric.server.utils.Constant;
import com.along101.logmetric.server.worker.IMessageHandler;
import com.along101.logmetric.server.worker.MessageBox;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.kairosdb.client.HttpClient;
import org.kairosdb.client.builder.MetricBuilder;
import org.kairosdb.client.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * metric写入Kairosdb
 * Created by yinzuolong on 2017/3/14.
 */
@Slf4j
@Component("handler.kairosdb")
public class KairosdbHandler implements IMessageHandler {

    @Autowired
    private MetricService metricWriter;
    @Autowired
    private HttpClient client;
    private int batchSize = 100;

    @Autowired
    private JsonLogHandler jsonLogHandler;

    @Override
    public boolean handle(List<MessageBox> messages) {
        List<Metric> metricsAll = new ArrayList<>();
        for (MessageBox messageBox : messages) {
            if (!(messageBox.getData() instanceof byte[])) {
                continue;
            }
            byte[] data = (byte[]) messageBox.getData();
            if (data.length <= 0) {
                continue;
            }
            if (data[0] == '{') {
                metricsAll.addAll(transformJsonObject(messageBox));
            } else if (data[0] == '[') {
                metricsAll.addAll(transformJsonArray(messageBox));
            } else {
                metricsAll.addAll(transformKryo(messageBox));
            }
        }
        int count = 0;
        MetricBuilder builder = MetricBuilder.getInstance();
        for (Metric metric : metricsAll) {
            builder.addMetric(metric.getName())
                    .addTags(metric.getTags())
                    .addDataPoint(metric.getTimeStamp(), metric.getValue());
            metricWriter.increment(TagMetricBuilder.create("counter.handler.kairosdb.metric", 1)
                    .addTag("appId", metric.getTags().get("appId"))
                    .addTag("appHost", metric.getTags().get("host"))
                    .addTag("name", "kairosdb").toDelta());
            count++;
            if (count % batchSize == 0) {
                insertMertics(builder);
                builder = MetricBuilder.getInstance();
            }
        }
        //TODO 记录每个Message中包含的metric个数
//        Cat.logMetricForCount("");
        if (count % batchSize != 0) {
            insertMertics(builder);
        }
        return true;
    }

    private Metric transform(JSONObject jsonObject) {
        try {
            Metric metric = new Metric(jsonObject.getString("name"));
            metric.setValue(jsonObject.get("value"));
            metric.setTimeStamp(jsonObject.getLongValue("timeStamp"));
            JSONObject tags = jsonObject.getJSONObject("tags");
            if (tags != null) {
                for (String key : tags.keySet()) {
                    metric.addTag(key, tags.getString(key));
                }
            } else {
                metric.setTags(new HashMap<String, String>());
            }
            return metric;
        } catch (Exception e) {
            metricWriter.increment(TagMetricBuilder.create("counter.handler.metric.deserialize.error", 1)
                    .addTag("name", "kairosdb").toDelta());
        }
        return null;
    }

    private List<Metric> transformJsonObject(MessageBox messageBox) {
        try {
            String jsonStr = new String((byte[]) messageBox.getData(), Constant.UTF_8);
            Metric metric = transform(JSON.parseObject(jsonStr));
            if (metric != null) {
                return Arrays.asList(metric);
            }
        } catch (Exception e) {
            metricWriter.increment(TagMetricBuilder.create("counter.handler.metric.deserialize.error", 1)
                    .addTag("name", "kairosdb").toDelta());
        }
        return Collections.emptyList();
    }

    private List<Metric> transformJsonArray(MessageBox messageBox) {
        List<Metric> metrics = new ArrayList<>();
        try {
            String jsonStr = new String((byte[]) messageBox.getData(), Constant.UTF_8);
            JSONArray jsonArray = JSON.parseArray(jsonStr);
            for (int i = 0; i < jsonArray.size(); i++) {
                metrics.add(transform(jsonArray.getJSONObject(i)));
            }
        } catch (Exception e) {
            metricWriter.increment(TagMetricBuilder.create("counter.handler.metric.deserialize.error", 1)
                    .addTag("name", "kairosdb").toDelta());
        }
        return metrics;
    }

    private List<Metric> transformKryo(MessageBox messageBox) {
        List<Metric> metricList = new ArrayList<>();
        Message message;
        try {
            message = KryoUtil.deserialize((byte[]) messageBox.getData(), Message.class);
            if (message.getType() != MessageType.METRIC) {
                return metricList;
            }
        } catch (Exception e) {
            metricWriter.increment(TagMetricBuilder.create("counter.handler.kairosdb.deserialize.error", 1)
                    .addTag("topic", messageBox.getTag("topic")).addTag("name", "kairosdb").toDelta());
            return metricList;
        }
        String appId = message.getAppId();
        String host = (String) message.getHeader().get("HOST_IP");
        try {
            byte[] data = message.getPayload();
            ArrayList<Metric> metrics = KryoUtil.deserialize(data, ArrayList.class);
            for (Metric metric : metrics) {
                if (metric.getTags() == null) {
                    metric.setTags(new HashMap<String, String>());
                }
                metric.addTag("appId", appId);
                metric.addTag("host", host);
                metricList.add(metric);
            }
        } catch (Exception e) {
            metricWriter.increment(TagMetricBuilder.create("counter.handler.kairosdb.deserialize.error", 1)
                    .addTag("topic", messageBox.getTag("topic")).addTag("name", "kairosdb").toDelta());
        }
        return metricList;
    }

    private void insertMertics(MetricBuilder builder) {
        Transaction t = Cat.newTransaction("pipeline.handler", "KairosdbHandler" + ".insertMertics");
        try {
            Response response = this.client.pushMetrics(builder);
            log.info("insert {} metrics. status code: {}", builder.getMetrics().size(), response.getStatusCode());
            List<String> errors = response.getErrors();
            if (errors != null && errors.size() > 0) {
                log.info("error insert {} metrics: {}", errors.size(), errors);
            }
            t.setStatus(Transaction.SUCCESS);
//            jsonLogHandler.indexLogs(jsonLogHandler.getIndex(), Arrays.asList(createLogMessage(builder)));

        } catch (Exception e) {
            t.setStatus(e);
            log.error("insert metrics message error.", e);
            Cat.logError("insert metrics message error.", e);
            metricWriter.increment(TagMetricBuilder.create("counter.handler.kairosdb.push.error", 1)
                    .addTag("name", "kairosdb").toDelta());
        } finally {
            t.complete();
        }
    }

    private LogMessage createLogMessage(MetricBuilder builder) {
        LogMessage logMessage = new LogMessage();
        logMessage.setAppId("logmetrics");
        logMessage.setLevel("info");
        logMessage.setTimeStampByLong(System.currentTimeMillis());
        logMessage.setLogName("KairosdbHandler");
        logMessage.setThreadName(Thread.currentThread().getName());

        String message = "messageCount : " + builder.getMetrics().size();

        Map<String, String> tags = builder.getMetrics().get(0).getTags();
        if (tags != null) {
            message += "  tags:" + tags;
        }
        message += "  name: " + builder.getMetrics().get(0).getName();

        message += "  datapointCount: " + builder.getMetrics().get(0).getDataPoints().size();

        long time = builder.getMetrics().get(0).getDataPoints().get(0).getTimestamp();
        message += "  Time:" + DateFormatUtils.format(time, jsonLogHandler.getTimeStampFormat());
        logMessage.setLayoutMessage(message);
        return logMessage;
    }
}