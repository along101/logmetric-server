package com.along101.logmetric.server.config;

import com.along101.logmetric.server.utils.Constant;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * yaml配置Engine测试
 * Created by yinzuolong on 2017/3/17.
 */
public class YamlEngineConfigTest {

    @Test
    public void testConfig() throws Exception {
        try (InputStream in = YamlEngineConfigTest.class.getClassLoader().getResourceAsStream("engine-dev.yml");
             InputStreamReader inputStreamReader = new InputStreamReader(in, Constant.UTF_8)) {
            EngineConfig config = new Yaml(new Constructor(EngineConfig.class)).loadAs(inputStreamReader, EngineConfig.class);
            System.out.println(config);

            Thread.sleep(1000000);
        }
    }
}
