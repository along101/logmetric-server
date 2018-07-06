package com.along101.logmetric.server.monitor.metrics;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Created by yinzuolong on 2017/4/5.
 */
@Getter
public class TagMetricBuilder<T extends Number> {

    private String name;
    private T value;
    private Date timestamp;
    private Map<String, Object> tags = new HashMap<>();

    public static <T extends Number> TagMetricBuilder<T> create(String name, T value) {
        return new TagMetricBuilder<>(name, value);
    }

    public static <T extends Number> TagMetricBuilder<T> create(String name, T value, Date timestamp) {
        return new TagMetricBuilder<>(name, value, timestamp);
    }

    private TagMetricBuilder(String name, T value) {
        this(name, value, new Date());
    }

    private TagMetricBuilder(String name, T value, Date timestamp) {
        Assert.notNull(name, "Name must not be null");
        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
    }

    public TagMetricBuilder addTag(String name, Object value) {
        checkNotNullOrEmpty(name);
        if (value != null)
            tags.put(name, value);

        return this;
    }

    public TagMetricBuilder addTags(Map<String, Object> tags) {
        checkNotNull(tags);
        this.tags.putAll(tags);
        return this;
    }

    public Metric<T> toMetric() {
        String tagsJson = JSON.toJSONString(this.tags);
        String name = new StringBuilder(this.name).append("$$").append(tagsJson).toString();
        return new Metric<>(name, this.value, this.timestamp);
    }

    public static <T extends Number> TagMetricBuilder<T> fromMetric(Metric<T> metric) {
        String name = StringUtils.substringBefore(metric.getName(), "$$");
        String tagsJson = StringUtils.substringAfter(metric.getName(), "$$");
        TagMetricBuilder<T> tagMetric = new TagMetricBuilder<>(name, metric.getValue(), metric.getTimestamp());
        if (StringUtils.startsWith(tagsJson, "{")) {
            HashMap tags = JSON.parseObject(tagsJson, HashMap.class);
            tagMetric.addTags(tags);
        }
        return tagMetric;
    }


    public Delta<T> toDelta() {
        String tagsJson = JSON.toJSONString(this.tags);
        String name = new StringBuilder(this.name).append("$$").append(tagsJson).toString();
        return new Delta<>(name, this.value, this.timestamp);
    }

    public static <T extends Number> TagMetricBuilder<T> fromDelta(Delta<T> delta) {
        String name = StringUtils.substringBefore(delta.getName(), "$$");
        String tagsJson = StringUtils.substringAfter(delta.getName(), "$$");
        TagMetricBuilder<T> tagMetric = new TagMetricBuilder<>(name, delta.getValue(), delta.getTimestamp());
        if (StringUtils.startsWith(tagsJson, "{")) {
            HashMap tags = JSON.parseObject(tagsJson, HashMap.class);
            tagMetric.addTags(tags);
        }
        return tagMetric;
    }

}
