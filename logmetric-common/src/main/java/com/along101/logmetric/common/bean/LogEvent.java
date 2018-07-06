package com.along101.logmetric.common.bean;

import java.io.*;
import java.util.*;

import com.google.common.collect.Maps;


public class LogEvent {
    private String message;

    private Throwable exceptionItem;
    private LogLevel level;
    private Date timeStamp;
    private String logName;
    private HashMap<String, String> tags;
    private String stackTrace;

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getExceptionItem() {
        return exceptionItem;
    }

    public void setExceptionItem(Throwable exceptionItem) {
        this.exceptionItem = exceptionItem;
        try {
            if (exceptionItem != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exceptionItem.printStackTrace(pw);
                this.stackTrace = sw.toString();
            }
        } catch (Exception e) {

        }
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public Date getTimeStamp() {
        if (timeStamp == null)
            return null;
        return Date.class.cast(timeStamp.clone());
    }

    public void setTimeStamp(Date timeStamp) {
        if (timeStamp == null)
            return;
        this.timeStamp = new Date(timeStamp.getTime());
    }

    public void setTimeStamp(long time) {
        if (timeStamp == null)
            return;
        this.timeStamp = new Date(time);
    }

    public void putTag(String key, String val) {
        if (tags == null)
            tags = new HashMap<String, String>();
        tags.put(key, val);
    }

    public String getTag(String key) {
        if (tags == null)
            tags = new HashMap<String, String>();
        return tags.get(key);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getCopyOfTags() {
        if (tags == null)
            return null;
        return Map.class.cast(tags.clone());
    }

    public void setTags(Map<String, String> contextMap) {
        if (contextMap == null)
            return;
        if (tags == null)
            tags = Maps.newHashMap();
        tags.clear();
        for (Map.Entry<String, String> entry : contextMap.entrySet()) {
            tags.put(entry.getKey(), entry.getValue());
        }
    }

}
