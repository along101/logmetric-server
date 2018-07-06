package com.along101.logmetric.server.exportor;

import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.client.HttpClient;
import org.kairosdb.client.builder.AggregatorFactory;
import org.kairosdb.client.builder.MetricBuilder;
import org.kairosdb.client.builder.QueryBuilder;
import org.kairosdb.client.builder.TimeUnit;
import org.kairosdb.client.response.QueryResponse;
import org.kairosdb.client.response.Response;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * Created by yinzuolong on 2017/3/17.
 */
public class KairosdbClientTest {

    private String kairosUrl = "http://localhost:8082";

    private HttpClient getClient() throws  Exception {
        HttpClient client = new HttpClient(kairosUrl);
        return client;
    }
    @Test
    public void pushMetric() throws Exception {
        MetricBuilder builder = MetricBuilder.getInstance();
        builder.addMetric("logmetric.testunit").addTag("host", String.valueOf("localhost"))
                .addDataPoint(System.currentTimeMillis(), 10)
                .addDataPoint(System.currentTimeMillis(), 30L);
        HttpClient client = getClient();
        Response response = client.pushMetrics(builder);
        Assert.assertTrue(response.getStatusCode() / 100 == 2);
        client.shutdown();

    }

    @Test
    public void queryDataPoint() throws Exception {
        QueryBuilder builder = QueryBuilder.getInstance();
        builder.setStart(5, TimeUnit.MINUTES).setEnd(1, TimeUnit.MINUTES).addMetric("logmetric.testunit")
                .addAggregator(AggregatorFactory.createSumAggregator(1, TimeUnit.MINUTES));
        HttpClient client = getClient();
        QueryResponse response = client.query(builder);
        String body = response.getBody();
        Assert.assertTrue(response.getStatusCode() / 100 == 2);
        System.out.println(body);
        client.shutdown();
    }


}
