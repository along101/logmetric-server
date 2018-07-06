package com.along101.logmetric.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Created by yinzuolong on 2017/3/17.
 */
@Getter
@Setter
@ConfigurationProperties("logmetric.engine")
public class EngineConfig {
    private List<WorkUnitConfig> workUnitConfigs;
}
