package com.along101.logmetric.server.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by yinzuolong on 2017/3/17.
 */
@Setter
@Getter
public class WorkUnitConfig {

    private String name;
    private String fetcherBean;
    private String topics;
    private Map<String, String> fetcherConfig;
    private int fetchThreads = 1;
    private int buffSize = 100;
    private int pumpBatchSize = 100;
    private int pipelineWidth = 1;
    private String handlerBeans;

}
