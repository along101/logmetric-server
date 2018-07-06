package com.along101.logmetric.server.monitor.metrics;

import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.reader.MetricReader;

/**
 * Created by yinzuolong on 2017/4/6.
 */
public class MetricServiceReader implements MetricReader {
    @Override
    public Metric<?> findOne(String metricName) {
        return null;
    }

    @Override
    public Iterable<Metric<?>> findAll() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }
}
