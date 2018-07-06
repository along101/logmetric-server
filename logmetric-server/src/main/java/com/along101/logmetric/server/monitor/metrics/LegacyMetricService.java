package com.along101.logmetric.server.monitor.metrics;

import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository;
import org.springframework.boot.actuate.metrics.writer.Delta;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yinzuolong on 2017/4/6.
 */
public class LegacyMetricService implements MetricService {
    private InMemoryMetricRepository inMemoryMetricRepository;

    private final ConcurrentHashMap<String, String> names = new ConcurrentHashMap<String, String>();

    public LegacyMetricService(InMemoryMetricRepository inMemoryMetricRepository) {
        this.inMemoryMetricRepository = inMemoryMetricRepository;
    }

    @Override
    public void submit(Metric metric) {
        inMemoryMetricRepository.set(new Metric<Double>(wrap(metric.getName()), metric.getValue().doubleValue()));
    }

    @Override
    public void increment(Metric metric) {
        inMemoryMetricRepository.increment(new Delta<Long>(wrap(metric.getName()), metric.getValue().longValue()));
    }

    @Override
    public void decrement(Metric metric) {
        inMemoryMetricRepository.increment(new Delta<Long>(wrap(metric.getName()), 0 - metric.getValue().longValue()));
    }

    @Override
    public void reset(String metricName) {
        inMemoryMetricRepository.reset(wrap(metricName));
    }

    private String wrap(String metricName) {
//        String cached = this.names.get(metricName);
//        if (cached != null) {
//            return cached;
//        }
//        if (metricName.startsWith("gauge") || metricName.startsWith("histogram")
//                || metricName.startsWith("timer")) {
//            return metricName;
//        }
//        String name = "gauge." + metricName;
//        this.names.put(metricName, name);
//        return name;
        return metricName;
    }
}
