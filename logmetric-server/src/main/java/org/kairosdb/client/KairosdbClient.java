package org.kairosdb.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.net.MalformedURLException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * TODO 增加了keep-alive头，性能扛不住可以验证下
 * @author yinzuolong
 */
public class KairosdbClient extends HttpClient {
    private CloseableHttpClient client;
    private int retries = 3;

    /**
     * Creates a client to talk to the host on the specified port.
     *
     * @param url url to the KairosDB server
     * @throws MalformedURLException if url is malformed
     */
    public KairosdbClient(String url) throws MalformedURLException
    {
        super(url);
        HttpClientBuilder builder = HttpClientBuilder.create();
        client = builder.build();
    }

    /**
     * Creates a client to talk to the host on the specified port. This version
     * of the constructor exposes the HttpClientBuilder that can be used to set
     * various properties on the client.
     *
     * @param builder client builder.
     * @param url url to the KairosDB server
     * @throws MalformedURLException if the url is malformed
     */
    public KairosdbClient(HttpClientBuilder builder, String url) throws MalformedURLException
    {
        super(url);
        client = builder.build();
    }

    @Override
    protected ClientResponse postData(String json, String url) throws IOException
    {
        StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        HttpPost postMethod = new HttpPost(url);
        postMethod.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        postMethod.setEntity(requestEntity);

        return execute(postMethod);
    }

    @Override
    protected ClientResponse queryData(String url) throws IOException
    {
        HttpGet getMethod = new HttpGet(url);
        getMethod.addHeader("accept", "application/json");
        getMethod.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);

        return execute(getMethod);
    }

    @Override
    protected ClientResponse delete(String url) throws IOException
    {
        HttpDelete deleteMethod = new HttpDelete(url);
        deleteMethod.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        deleteMethod.addHeader("accept", "application/json");

        return execute(deleteMethod);
    }

    private ClientResponse execute(HttpUriRequest request) throws IOException
    {
        HttpResponse response;

        int tries = retries + 1;
        while (true)
        {
            tries--;
            try
            {
                response = client.execute(request);
                break;
            }
            catch (IOException e)
            {
                if (tries < 1)
                    throw e;
            }
        }

        return new HttpClientResponse(response);
    }

    @Override
    public void shutdown() throws IOException
    {
        client.close();
    }

    @Override
    public int getRetryCount()
    {
        return retries;
    }

    public void setRetryCount(int retries)
    {
        checkArgument(retries >= 0);
        this.retries = retries;
    }

    /**
     * Used for testing only
     * @param client underlying client
     */
    protected void setClient(CloseableHttpClient client)
    {
        this.client = client;
    }

}
