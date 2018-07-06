package com.along101.logmetric.server.monitor;

import com.along101.logmetric.server.worker.WorkUnit;
import com.along101.logmetric.server.worker.WorkersEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by yinzuolong on 2017/3/31.
 */
@Component
public class EngineHealthIndicator extends AbstractHealthIndicator {
    @Autowired
    private WorkersEngine engine;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        List<WorkUnit> workUnits = engine.getWorkUnits();
        boolean isRunning = true;
        for (WorkUnit workUnit : workUnits) {
            isRunning &= workUnit.getPipeline().getRunning().get();
            isRunning &= workUnit.getFetcherPump().getRunning().get();
        }
        if (isRunning) {
            builder.status(Status.UP);
        } else {
            builder.status(Status.DOWN);
        }
    }


}
