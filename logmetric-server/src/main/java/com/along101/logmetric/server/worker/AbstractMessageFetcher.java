package com.along101.logmetric.server.worker;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by yinzuolong on 2017/3/14.
 */
public abstract class AbstractMessageFetcher {

    @Setter
    @Getter
    protected String name;
    @Setter
    @Getter
    protected Map<String, String> config;


    public abstract void connect(List<String> topics);

    public abstract List<MessageBox> fetch();

    public abstract void close();
}
