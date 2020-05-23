package org.xd.chain.util;

import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * @author rxd
 * @ClassName Timer
 * Description TODO
 * @date 2019-09-24 20:18
 * @Version 1.0
 */
public final class Timer {
    private static final Logger LOGGER = Logger.getLogger(Timer.class.getName());
    private volatile ExecutorService service;
    private volatile Future<Boolean> future;


    private volatile boolean flag = true;

    public boolean startTimer() {
        this.flag = true;
        LOGGER.info(Thread.currentThread().getName()+"******************启动计时器......");
        service = Executors.newSingleThreadExecutor();
        future = service.submit(new Task());
        return flag;
    }


    public boolean resetTime() {
        LOGGER.info(Thread.currentThread().getName()+"******************重启计时器.......");
        shutdownTimer();
        future = service.submit(new Task());
        this.flag = true;
        return this.flag;
    }

    public boolean getFlag() {
        try {
            if (future != null && !future.isCancelled())
                this.flag = future.get(1, TimeUnit.MILLISECONDS);
        } catch (CancellationException | InterruptedException | ExecutionException | TimeoutException e) {
        }
        return this.flag;
    }


    public void shutdownTimer() {
        if (!future.isCancelled()) {
            future.cancel(true);
        }
    }
}
