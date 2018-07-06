package com.along101.logmetric.server.exportor;

import com.alibaba.fastjson.JSON;
import com.along101.logmetric.common.bean.LogEvent;
import com.along101.logmetric.server.TestUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by yinzuolong on 2017/3/17.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ElasticsearchClientTest {
    private Client client;

    private String indexName = "logmetric-testunit";
    private String indexType = "log.Testunit";
    private String servers = "localhost:9300";

    @Before
    public void init() throws Exception {
        this.client = getClient();
    }

    /**
     * 创建客户端
     *
     * @return
     * @throws Exception
     */
    public Client getClient() throws Exception {
        Settings settings = Settings.builder()
                .put("cluster.name", "gdslog")
                .put("client.transport.sniff", true)
                .put("xpack.security.user", "logmetric:logmetric.write.pass")
                .build();
        PreBuiltXPackTransportClient client = new PreBuiltXPackTransportClient(settings);
        String[] serverUrls = servers.split(",");
        for (String serverUrl : serverUrls) {
            String ip = StringUtils.substringBefore(serverUrl, ":");
            String port = StringUtils.substringAfterLast(serverUrl, ":");
            InetSocketTransportAddress address = new InetSocketTransportAddress(new InetSocketAddress(InetAddress.getByName(ip), Integer.parseInt(port)));
            client.addTransportAddress(address);
        }
        return client;
    }

    @Test
    public void test01Index() {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (int i = 0; i < 100; i++) {
            LogEvent logEvent = TestUtils.createTestLogEvent(i + "");
            logEvent.putTag("appId", "123123");
            String jsonStr = JSON.toJSONStringWithDateFormat(logEvent, "yyyy-MM-dd'T'HH:mm:ss.SSSZ");

            IndexRequestBuilder request = client.prepareIndex(indexName, indexType)
                    .setSource(jsonStr, XContentType.JSON);
            bulkRequest.add(request);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        bulkResponse.getItems();
    }

    @Test
    public void test02Query() {
        QueryBuilder qb1 = QueryBuilders.queryStringQuery("INFO");
        SearchResponse response = client.prepareSearch(indexName)
                .setQuery(qb1)
                .setFrom(0).setSize(60).setExplain(true)
                .execute()
                .actionGet();
        SearchHits hits = response.getHits();
        Assert.assertTrue(hits.getHits().length > 0);
        for (SearchHit hit : hits.getHits()) {
            System.out.println(hit.getSource());
        }
    }

    @Test
    public void test03DeleteIndex() {
        DeleteIndexResponse response = client.admin().indices()
                .prepareDelete(indexName)
                .execute().actionGet();
        System.out.println("删除索引成功");
    }

}
