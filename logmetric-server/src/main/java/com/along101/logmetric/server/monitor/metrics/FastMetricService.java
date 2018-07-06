package com.along101.logmetric.server.monitor.metrics;

import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.buffer.CounterBuffers;
import org.springframework.boot.actuate.metrics.buffer.GaugeBuffers;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yinzuolong on 2017/4/6.
 */
public class FastMetricService implements MetricService {
    private CounterBuffers counters;
    private GaugeBuffers gauges;

    private final ConcurrentHashMap<String, String> names = new ConcurrentHashMap<String, String>();

    public FastMetricService(CounterBuffers counters, GaugeBuffers gauges) {
        this.counters = counters;
        this.gauges = gauges;
    }

    @Override
    public void submit(Metric metric) {
        gauges.set(wrap(metric.getName()), metric.getValue().doubleValue());
    }

    @Override
    public void increment(Metric metric) {
        counters.increment(wrap(metric.getName()), metric.getValue().longValue());
    }

    @Override
    public void decrement(Metric metric) {
        counters.increment(wrap(metric.getName()), 0 - metric.getValue().longValue());
    }


    @Override
    public void reset(String metricName) {
        counters.reset(wrap(metricName));
    }

    private String wrap(String metricName) {
//        String cached = this.names.get(metricName);
//        if (cached != null) {
//            return cached;
//        }
//        if (metricName.startsWith("counter") || metricName.startsWith("meter")) {
//            return metricName;
//        }
//        String name = "counter." + metricName;
//        this.names.put(metricName, name);
//        return name;
        return metricName;
    }
}
