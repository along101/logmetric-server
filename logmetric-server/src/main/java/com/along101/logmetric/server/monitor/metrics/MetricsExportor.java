package com.along101.logmetric.server.monitor.metrics;

import com.along101.logmetric.common.util.IPUtil;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.kairosdb.client.HttpClient;
import org.kairosdb.client.builder.MetricBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.writer.CounterWriter;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.GaugeWriter;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * Created by yinzuolong on 2017/4/5.
 */
@Component
@ExportMetricWriter
@Slf4j
public class MetricsExportor implements GaugeWriter, CounterWriter {

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private HttpClient client;

    @Override
    public void increment(Delta<?> delta) {
        write(TagMetricBuilder.fromDelta(delta));
    }

    @Override
    public void reset(String s) {

    }

    @Override
    public void set(Metric<?> metric) {
        write(TagMetricBuilder.fromMetric(metric));
    }

    private void write(TagMetricBuilder tagMetricBuilder) {
        try {
            MetricBuilder builder = MetricBuilder.getInstance();
            builder.addMetric(applicationName + "." + tagMetricBuilder.getName())
                    .addTag("host", IPUtil.getLocalIP())
                    .addTags(tagMetricBuilder.getTags())
                    .addDataPoint(tagMetricBuilder.getTimestamp().getTime(), tagMetricBuilder.getValue());
            client.pushMetrics(builder);
        } catch (Exception e) {
            log.error("error export metrics", e);
            Cat.logError("error export metrics", e);
        }
    }
}
