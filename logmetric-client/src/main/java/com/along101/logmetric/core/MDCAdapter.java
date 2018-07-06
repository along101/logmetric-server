package com.along101.logmetric.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MDCAdapter {

    final InheritableThreadLocal<Map<String, String>> copyOnInheritThreadLocal = new InheritableThreadLocal<Map<String, String>>();


    public void put(String key, String val) {
        Map<String,String> contextMap = copyOnInheritThreadLocal.get();
        if (contextMap == null) {
            contextMap = Collections.synchronizedMap(new HashMap<String, String>());
            copyOnInheritThreadLocal.set(contextMap);
        }
        contextMap.put(key,val);

    }

    public String get(String key) {
        Map<String,String> contextMap = copyOnInheritThreadLocal.get();
        if (contextMap == null)
            return null;
        return contextMap.get(key);
    }

    
    public void remove(String key) {
        Map<String,String> contextMap = copyOnInheritThreadLocal.get();
        if (contextMap == null)
            return;
        if (contextMap.containsKey(key))
            contextMap.remove(key);
    }

    
    public void clear() {
        Map<String,String> contextMap = copyOnInheritThreadLocal.get();
        if (contextMap == null)
            return;
        contextMap.clear();
    }


    public Map<String, String> getCopyOfContextMap() {
        Map<String,String> contextMap = copyOnInheritThreadLocal.get();
        if (contextMap == null)
            return null;
        return new HashMap<String,String>(contextMap);
    }


    public void setContextMap(Map<String, String> contextMap) {
        Map<String, String> newMap = Collections.synchronizedMap(new HashMap<String, String>());
        newMap.putAll(contextMap);
        copyOnInheritThreadLocal.set(newMap);
    }
}