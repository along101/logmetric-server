package com.along101.logmetric.server.worker;

import com.along101.logmetric.server.common.Starter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by yinzuolong on 2017/3/17.
 */
@Slf4j
@Setter
@Getter
public class WorkUnit extends Starter {
    private int id;
    private String name;
    private FetcherPump fetcherPump;
    private Pipeline pipeline;
    private String topics;

    @Override
    protected void doStart() throws Exception {
        try {
            //TODO 按照顺序启动，并且要等待启动完成
            List<Starter> starts = Arrays.asList(pipeline, fetcherPump);
            for (final Starter starter : starts) {
                starter.start();
                log.info("workUnit '{},{}' {} started.", name, id, starter.getClass().getSimpleName());
            }
        } catch (Exception e) {
            log.error("workUnit '{},{}' start failed: {} ", name, id, e);
            doStop();
            throw e;
        }
    }

    @Override
    protected void doStop() throws Exception {
        //TODO 按照顺序停止，并且要检查缓存为空才能停止poller和pipeLine
        List<Starter> starts = Arrays.asList(fetcherPump, pipeline);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (final Starter starter : starts) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        starter.stop();
                        log.info("workUnit '{},{}' {} stoped.", name, id, starter.getClass().getSimpleName());
                    } catch (Exception e) {
                        log.error("stop error.", e);
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }
}
