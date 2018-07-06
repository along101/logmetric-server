package com.along101.logmetric.server.common;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 启动器
 * TODO 改造成guava的多线程模型
 * Created by along on 2017/3/19.
 */
public abstract class Starter {
    @Getter
    protected AtomicBoolean running = new AtomicBoolean(false);

    public synchronized void start() throws Exception {
        if (running.compareAndSet(false, true)) {
            doStart();
        }
    }

    public synchronized void stop() throws Exception {
        if (running.compareAndSet(true, false)) {
            doStart();
        }
    }

    protected abstract void doStart() throws Exception;

    protected abstract void doStop() throws Exception;
}
