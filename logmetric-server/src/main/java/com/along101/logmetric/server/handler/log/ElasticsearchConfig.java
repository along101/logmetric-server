package com.along101.logmetric.server.handler.log;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yinzuolong on 2017/8/2.
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "logmetric.handler.elasticsearch")
public class ElasticsearchConfig {

    private static final String COLON = ":";
    private static final String COMMA = ",";
    @Setter
    @Getter
    private String clusterName;

    @Setter
    @Getter
    private String clusterNodes;

    @Setter
    @Getter
    private Map<String, String> properties = new HashMap<>();

    @Bean("elasticClient")
    @Primary
    public Client getClient() throws Exception {
        PreBuiltXPackTransportClient client = new PreBuiltXPackTransportClient(settings());
        Assert.hasText(clusterNodes, "[Assertion failed] clusterNodes settings missing.");
        for (String clusterNode : StringUtils.split(clusterNodes, COMMA)) {
            String hostName = StringUtils.substringBeforeLast(clusterNode, COLON);
            String port = StringUtils.substringAfterLast(clusterNode, COLON);
            Assert.hasText(hostName, "[Assertion failed] missing host name in 'clusterNodes'");
            Assert.hasText(port, "[Assertion failed] missing port in 'clusterNodes'");
            log.info("adding transport node : " + clusterNode);
            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostName), Integer.valueOf(port)));
        }
        client.connectedNodes();
        return client;
    }


    private Settings settings() {
        Settings.Builder builder = Settings.builder();
        if (properties != null) {
            builder.put(properties);
        }
        return builder.put("cluster.name", clusterName)
                .build();
    }
}
