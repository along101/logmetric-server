package com.along101.logmetric.server;

import com.along101.logmetric.server.worker.WorkersEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by yinzuolong on 2017/3/13.
 */
@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
public class LogmetricServer {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(LogmetricServer.class, args);
        WorkersEngine engine = context.getBean(WorkersEngine.class);
        engine.start();
    }
}
