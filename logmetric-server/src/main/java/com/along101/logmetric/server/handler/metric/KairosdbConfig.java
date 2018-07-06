package com.along101.logmetric.server.handler.metric;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.impl.client.HttpClientBuilder;
import org.kairosdb.client.HttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by yinzuolong on 2017/3/20.
 */
@Configuration
@ConfigurationProperties(prefix = "logmetric.handler.kairosdb")
public class KairosdbConfig {
    @Setter
    @Getter
    private String url;

    @Bean
    public HttpClient getClient() throws Exception {
        HttpClientBuilder builder = HttpClientBuilder.create()
                .setMaxConnTotal(500)
                .setMaxConnPerRoute(100);
        HttpClient client = new HttpClient(builder, url);
        return client;
    }
}
