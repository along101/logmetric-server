package com.along101.logmetric.server.monitor.metrics;

import org.springframework.boot.actuate.metrics.Metric;

/**
 * Created by yinzuolong on 2017/4/6.
 */
public interface MetricService {

    void submit(Metric metric);

    void increment(Metric metric);

    void decrement(Metric metric);

    void reset(String metricName);
}
