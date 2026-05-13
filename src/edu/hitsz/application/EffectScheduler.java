package edu.hitsz.application;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 延迟任务调度器。
 * 用一个共享的守护线程池处理冰冻恢复、减速恢复等短延时任务，避免每个对象都创建线程导致卡顿。
 */
public final class EffectScheduler {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(2, new ThreadFactory() {
        private int index = 1;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "effect-scheduler-" + index++);
            thread.setDaemon(true);
            return thread;
        }
    });

    private EffectScheduler() {
    }

    public static void schedule(Runnable task, long delayMs) {
        SCHEDULER.schedule(task, delayMs, TimeUnit.MILLISECONDS);
    }
}
