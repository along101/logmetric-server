package com.along101.logmetric.server.handler.log;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yinzuolong on 2017/5/31.
 */
@Data
public class LogMessage {
    private String id;
    private String appId;
    private String threadName;
    private String logName;
    private String level;
    private String message;
    private String layoutMessage;
    private String stackTrace;
    private Date timeStamp;
    private int version;
    private Map<String, Object> mdc = new HashMap<>();
    private Map<String, Object> tags = new HashMap<>();
    private Map<String, Object> head = new HashMap<>();

    public void setTimeStampByLong(Long time) {
        if (time != null)
            timeStamp = new Date(time);
    }

    public void putAllTags(Map<String, ?> tags) {
        if (tags != null)
            this.tags.putAll(tags);
    }

    public void putAllHead(Map<String, ?> head) {
        if (head != null)
            this.head.putAll(head);
    }

    public void putAllMdc(Map<String, ?> mdc) {
        if (mdc != null)
            this.mdc.putAll(mdc);
    }
}
