package com.along101.logmetric.core;

import com.along101.logmetric.common.IEncoder;
import com.along101.logmetric.common.IReporter;
import com.along101.logmetric.common.bean.Message;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Dispatcher implements IReporter {

    private Map<Class<?>, IEncoder> handlerMap;
    private Set<IReporter> reporters;
    private WorkerGroup workerGroup;

    public Dispatcher(int workerCount, int workerBufferSize) {
        this.handlerMap = new ConcurrentHashMap<Class<?>, IEncoder>();
        this.reporters = new HashSet<>();
        this.workerGroup = new WorkerGroup("Reporter-Worker-", workerCount, workerBufferSize);
        this.workerGroup.register(this);


    }

    public void register(Class<?> clazz, IEncoder handler) {
        this.handlerMap.put(clazz, handler);
    }

    public void addReporter(IReporter reporter) {
        this.reporters.add(reporter);
    }

    public void start() {
        workerGroup.start();
    }

    public void stop() {
        workerGroup.stop();
    }

    public int report(List<Object> list) {

        Map<Class<?>, List<Object>> groupMap = Maps.newHashMap();

        for (Object data : list) {
            List<Object> datas = groupMap.get(data.getClass());
            if (datas == null) {
                datas = Lists.newArrayList();
                groupMap.put(data.getClass(), datas);
            }
            datas.add(data);
        }

        int miss = 0;

        for (Entry<Class<?>, List<Object>> entry : groupMap.entrySet()) {
            List<Object> valueList = entry.getValue();
            if (!valueList.isEmpty()) {

                IEncoder encoder = this.handlerMap.get(entry.getKey());

                if (encoder != null) {
                    Message msg = encoder.encode(valueList);
                    miss += workerGroup.send(msg);
                } else {
                    log.warn(entry.getKey().getName() + " has not encoder!");
                    miss += valueList.size();
                }

            }
        }

        return miss;
    }

    @Override
    public void process(Message msg) {
        for (IReporter reporter : reporters) {
            try {
                reporter.process(msg);
            } catch (Throwable t) {
                log.error("Reporter process error!", t);
            }
        }
    }

    @PostConstruct
    public void close() {
        stop();
    }
}
