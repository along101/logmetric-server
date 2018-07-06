package com.along101.logmetric.server.worker;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息包装箱
 * Created by along on 2017/3/18.
 */
public class MessageBox {

    @Getter
    private Object data;
    /**
     * 包装箱的标签
     */
    @Getter
    private Map<String, Object> tags = new HashMap<>();

    public MessageBox(Object data) {
        this.data = data;
    }

    public MessageBox addTag(String tagName, Object tagValue) {
        this.tags.put(tagName, tagValue);
        return this;
    }

    public Object getTag(String tagName) {
        return this.tags.get(tagName);
    }

    public MessageBox addTags(Map<String, Object> tags) {
        this.tags.putAll(tags);
        return this;
    }

    public static MessageBox create(Object data) {
        return new MessageBox(data);
    }
}
