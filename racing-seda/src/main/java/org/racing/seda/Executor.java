package org.racing.seda;

import com.google.common.base.Preconditions;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhouyun on 2016/4/15.
 */
public class Executor {
    private final Auditor auditor = Auditor.getAuditor();
    private ThreadPoolExecutor executorService;
    private String name;
    private volatile int workSize = 5;
    private AtomicInteger nameCounter = new AtomicInteger(0);

    public Executor(String name, int workSize) {
        this(name);
        Preconditions.checkState(workSize > 0);
        this.workSize = workSize;
        initExecutor();
    }

    public Executor(String name) {
        this.name = Preconditions.checkNotNull(name);
    }

    public void execute(Runnable command) {
        executorService.execute(command);
    }

    public Future<?> submit(Runnable command) {
        return executorService.submit(command);
    }

    public void shutdown() {
        Preconditions.checkNotNull(executorService).shutdown();
    }

    public void setWorkSize(int workSize) {
        Preconditions.checkState(workSize > 0);
        executorService.setMaximumPoolSize(workSize * 2);
        executorService.setCorePoolSize(workSize);
    }

    /**
     * 初始化大小
     * @return
     */
    public int getWorkSize() {
        return workSize;
    }

    /**
     * 工作线程大小
     * @return
     */
    public int getCurrentWorkSize() {
        return executorService.getPoolSize();
    }

    /**
     * 最大线程数
     * @return
     */
    public int getMaxPoolSize() {
        return executorService.getMaximumPoolSize();
    }

    public long getTaskCount() {
        return executorService.getQueue().size();
    }

    public void initExecutor() {
        executorService = new ThreadPoolExecutor(workSize, workSize * 2, 180, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), this::threadFactory, this::rejectedExecutionHandler);
        executorService.allowCoreThreadTimeOut(true);
    }

    private Thread threadFactory(Runnable r) {
        return new Worker(r, name + "-Worker-" + nameCounter.getAndIncrement());
    }

    private void rejectedExecutionHandler(Runnable r, ThreadPoolExecutor executor) {
        auditor.error("[Executor][rejectedExecutionHandler] {} reject task", name);
    }
}
