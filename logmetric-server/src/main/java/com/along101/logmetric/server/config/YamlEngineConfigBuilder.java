package com.along101.logmetric.server.config;

import com.along101.logmetric.server.utils.Constant;
import com.along101.logmetric.server.worker.WorkerEngineBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by along on 2017/3/19.
 */
@Configuration
@ConditionalOnProperty(name = "logmetric.engine.config", havingValue = "yaml")
@ConfigurationProperties(prefix = "logmetric.engine.config.yaml")
public class YamlEngineConfigBuilder {

    @Setter
    @Getter
    private String configFile = "engine-dev.yml";

    @Bean
    public EngineConfig buildConfig() throws Exception {
        try (InputStream in = WorkerEngineBuilder.class.getClassLoader().getResourceAsStream(configFile);
             InputStreamReader inputStreamReader = new InputStreamReader(in, Constant.UTF_8)) {
            EngineConfig config = new Yaml(new Constructor(EngineConfig.class)).loadAs(inputStreamReader, EngineConfig.class);
            return config;
        }
    }
}
