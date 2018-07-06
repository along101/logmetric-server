package com.along101.logmetric.server.worker;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by yinzuolong on 2017/3/17.
 */
@Slf4j
public class WorkersEngine {

    @Setter
    @Getter
    private List<WorkUnit> workUnits;

    public void start() throws Exception {
        for (WorkUnit workUnit : workUnits) {
            workUnit.start();
            log.info("workUnit '{}' started.", workUnit.getName());
        }
    }
}
