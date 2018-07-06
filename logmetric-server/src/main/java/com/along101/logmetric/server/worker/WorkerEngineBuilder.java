package com.along101.logmetric.server.worker;

import com.along101.logmetric.server.config.EngineConfig;
import com.along101.logmetric.server.config.WorkUnitConfig;
import com.along101.logmetric.server.monitor.metrics.MetricService;
import com.google.common.base.Splitter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinzuolong on 2017/3/17.
 */
@Component
public class WorkerEngineBuilder implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Autowired
    private EngineConfig config;
    @Autowired
    private MetricService metricWriter;

    @Bean
    public WorkersEngine buildEngine() throws Exception {
        WorkersEngine engine = new WorkersEngine();
        List<WorkUnit> workUnits = new ArrayList<>();
        for (int i = 0; i < config.getWorkUnitConfigs().size(); i++) {
            WorkUnitConfig workUnitConfig = config.getWorkUnitConfigs().get(i);
            workUnits.add(buildWorkUnit(workUnitConfig));
        }
        engine.setWorkUnits(workUnits);
        return engine;
    }

    protected WorkUnit buildWorkUnit(WorkUnitConfig config) {
        WorkUnit workUnit = new WorkUnit();
        String name = config.getName();
        workUnit.setName(name);
        workUnit.setTopics(config.getTopics());
        constractFetchPump(workUnit, config);
        constractPipeline(workUnit, config);
        return workUnit;
    }

    protected void constractFetchPump(WorkUnit workUnit, WorkUnitConfig config) {
        List<AbstractMessageFetcher> fetchers = new ArrayList<>();
        for (int i = 0; i < config.getFetchThreads(); i++) {
            AbstractMessageFetcher fetcher = (AbstractMessageFetcher) applicationContext.getBean(config.getFetcherBean());
            fetcher.setName(workUnit.getName() + "." + "fetcher-" + String.valueOf(i));
            fetcher.setConfig(config.getFetcherConfig());
            fetchers.add(fetcher);
        }
        FetcherPump pump = new FetcherPump();
        pump.setName(workUnit.getName() + ".pump");
        pump.setFetchers(fetchers);
        pump.setTopics(config.getTopics());
        pump.setWorkUnit(workUnit);
        pump.setBatchSize(config.getPumpBatchSize());
        pump.setMetricWriter(this.metricWriter);
        pump.init();
        workUnit.setFetcherPump(pump);
    }

    protected void constractPipeline(WorkUnit workUnit, WorkUnitConfig config) {
        List<HandlerWrapper> handlers = new ArrayList<>();
        Iterable<String> handlerBeans = Splitter.on(",").split(config.getHandlerBeans());
        for (String beanName : handlerBeans) {
            IMessageHandler handler = (IMessageHandler) applicationContext.getBean(beanName);
            HandlerWrapper wrapper = new HandlerWrapper();
            wrapper.setHandler(handler);
            wrapper.setName(workUnit.getName() + ".handler-" + beanName);
            handlers.add(wrapper);
        }
        Pipeline pipeline = new Pipeline();
        pipeline.setName(workUnit.getName() + ".pipeline");
        pipeline.setWorkUnit(workUnit);
        pipeline.setHandlers(handlers);
        pipeline.setWidth(config.getPipelineWidth());
        pipeline.setCapacity(config.getBuffSize());
        pipeline.setMetricWriter(this.metricWriter);
        pipeline.init();
        workUnit.setPipeline(pipeline);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
