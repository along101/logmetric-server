package com.along101.logmetric.server.worker;

import com.along101.logmetric.server.common.Starter;
import com.along101.logmetric.server.monitor.metrics.MetricService;
import com.along101.logmetric.server.monitor.metrics.TagMetricBuilder;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 水泵
 * Created by yinzuolong on 2017/3/14.
 */
@Slf4j
public class FetcherPump extends Starter {
    @Getter
    @Setter
    private List<AbstractMessageFetcher> fetchers;
    @Getter
    @Setter
    private String topics;
    private ExecutorService executorService;
    @Setter
    private int interval = 1000;
    @Setter
    private int batchSize = 100;
    @Setter
    private WorkUnit workUnit;
    @Setter
    @Getter
    private String name;
    @Setter
    private MetricService metricWriter;
    private Map<String, Object> publicTags;

    public void init() {
        initTags();
    }

    @Override
    protected void doStart() throws Exception {
        int threads = fetchers.size() > 0 ? fetchers.size() + 1 : 10;
        executorService = Executors.newFixedThreadPool(threads, new ThreadFactoryBuilder()
                .setNameFormat(name).build());
        for (AbstractMessageFetcher fetcher : fetchers) {
            fetcher.connect(Arrays.asList(topics.split(",")));
        }
        for (final AbstractMessageFetcher fetcher : fetchers) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    List<MessageBox> messages = new ArrayList<>(batchSize * 2);
                    while (running.get()) {
                        try {
                            List<MessageBox> tmp = fetch(fetcher);
                            if (tmp.isEmpty()) {
                                if (!messages.isEmpty()) {
                                    metricWriter.submit(TagMetricBuilder.create("pump.putSize", messages.size())
                                            .addTags(publicTags).toMetric());
                                    workUnit.getPipeline().put(messages);
                                    messages = new ArrayList<>(batchSize * 2);
                                }
                                log.debug("fetcher '{}' sleep {} .", fetcher.getName(), interval);
                                TimeUnit.MILLISECONDS.sleep(interval);
                            } else {
                                messages.addAll(tmp);
                                if (messages.size() >= batchSize) {
                                    metricWriter.submit(TagMetricBuilder.create("pump.putSize", messages.size())
                                            .addTags(publicTags).toMetric());
                                    workUnit.getPipeline().put(messages);
                                    messages = new ArrayList<>(batchSize * 2);
                                }
                            }
                        } catch (Throwable e) {
                            log.error("fetcher '{}' fetch error : {}.", fetcher.getName(), e);
                            Cat.logError("fetch message error!", e);
                            try {
                                TimeUnit.MILLISECONDS.sleep(interval);
                            } catch (InterruptedException e1) {
                            }
                        }
                    }
                    log.info("fetcher '{}' stopped.", fetcher.getName());
                }
            });
        }
    }

    private Map<String, Object> initTags() {
        publicTags = new HashMap<>();
        publicTags.put("name", name);
        publicTags.put("topics", topics);
        return publicTags;
    }

    private List<MessageBox> fetch(AbstractMessageFetcher fetcher) {
        Transaction t = Cat.newTransaction("fetcher", fetcher.getName());
        try {
            List<MessageBox> messages = fetcher.fetch();
            t.setStatus(Transaction.SUCCESS);
            log.debug("fetcher '{}' fetch records count {}.", fetcher.getName(), messages.size());
            return messages;
        } catch (Exception e) {
            t.setStatus(e);
            throw new RuntimeException("fetch message error!", e);
        } finally {
            t.complete();
        }
    }

    @Override
    protected void doStop() throws Exception {
        for (AbstractMessageFetcher fetcher : fetchers) {
            fetcher.close();
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }
}
