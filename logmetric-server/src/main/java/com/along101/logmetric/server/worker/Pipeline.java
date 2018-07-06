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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 流水线
 * Created by along on 2017/3/18.
 */
@Slf4j
public class Pipeline<T> extends Starter {
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private List<HandlerWrapper> handlers;
    /**
     * 带宽（最大并发数）
     */
    @Getter
    @Setter
    private int width = 1;
    /**
     * 队列容量
     */
    @Setter
    @Getter
    private int capacity = 100;

    @Setter
    @Getter
    private WorkUnit workUnit;
    private ThreadPoolExecutor threadPool;
    @Setter
    private MetricService metricWriter;
    private Map<String, Object> publicTags;

    public void init() {
        initTags();
    }


    public void put(final List<MessageBox> messages) {
        Transaction t = Cat.newTransaction("Pipeline", name + ".put");
        try {
            this.threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    callAllhandlers(messages);
                }
            });
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.error("put message error.", e);
            t.setStatus(e);
            Cat.logError("put message error.", e);
        } finally {
            t.complete();
        }

        this.metricWriter.submit(TagMetricBuilder.create("pipeline.ActiveCount", threadPool.getActiveCount())
                .addTags(publicTags).toMetric());
        this.metricWriter.submit(TagMetricBuilder.create("pipeline.queueWaitSize", threadPool.getQueue().size())
                .addTags(publicTags).toMetric());
    }

    private Map<String, Object> initTags() {
        publicTags = new HashMap<>();
        publicTags.put("name", name);
        return publicTags;
    }

    private void callAllhandlers(List<MessageBox> messages) {
        for (HandlerWrapper handler : handlers) {//调用所有的hanlder
            Transaction t = Cat.newTransaction("pipeline.handler", handler.getName() + ".handle");
            try {
                handler.handle(messages);
                t.setStatus(Transaction.SUCCESS);
            } catch (Exception e) {
                log.error("handler message error.", e);
                t.setStatus(e);
                Cat.logError("handler message error.", e);
            } finally {
                t.complete();
            }
            this.metricWriter.increment(TagMetricBuilder.create("counter.pipeline.handle.message", messages.size())
                    .addTags(publicTags).toDelta());
        }
    }

    /**
     * 是否空闲
     *
     * @return
     */
    public boolean isBusy() {
        return threadPool.getQueue().size() >= 1;
    }

    @Override
    protected void doStart() throws Exception {
        if (this.threadPool == null) {
            this.threadPool = new ThreadPoolExecutor(width, width, 1000L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(capacity),
                    new ThreadFactoryBuilder()
                            .setNameFormat(name).build(),
                    new RejectedExecutionHandler() {//队列容量限制，调用put方法阻塞
                        @Override
                        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                            if (!executor.isShutdown()) {
                                try {
                                    log.debug("pipeline '{}' is busy, blocking submit", name);
                                    executor.getQueue().put(r);
                                    log.debug("pipeline '{}' blocking recover", name);
                                } catch (InterruptedException e) {
                                    log.error("rejectedExecution handler error :", e);
                                    Cat.logError("rejectedExecution handler error.", e);
                                }
                            }
                        }
                    });
        }
    }

    @Override
    protected void doStop() throws Exception {
        this.threadPool.shutdown();
        this.threadPool.awaitTermination(10, TimeUnit.SECONDS);
    }
}
