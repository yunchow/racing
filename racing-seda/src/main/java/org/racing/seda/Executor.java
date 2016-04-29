package org.racing.seda;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhouyun on 2016/4/15.
 */
public class Executor {
    private final Auditor auditor = Auditor.getAuditor();
    private ThreadPoolExecutor threadPoolExecutor;
    private String name;
    private volatile int workSize = 5;
    private AtomicInteger nameCounter = new AtomicInteger(0);
    protected ListeningExecutorService listeningExecutorService;

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
        threadPoolExecutor.execute(command);
    }

    public ListenableFuture<Void> submit(Runnable task) {
        return (ListenableFuture<Void>) listeningExecutorService.submit(task);
    }

    public void shutdown() {
        Preconditions.checkNotNull(threadPoolExecutor).shutdown();
    }

    public void setWorkSize(int workSize) {
        Preconditions.checkState(workSize > 0);
        threadPoolExecutor.setMaximumPoolSize(workSize * 2);
        threadPoolExecutor.setCorePoolSize(workSize);
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
        return threadPoolExecutor.getPoolSize();
    }

    /**
     * 最大线程数
     * @return
     */
    public int getMaxPoolSize() {
        return threadPoolExecutor.getMaximumPoolSize();
    }

    public long getTaskCount() {
        return threadPoolExecutor.getQueue().size();
    }

    public void initExecutor() {
        threadPoolExecutor = new ThreadPoolExecutor(workSize, workSize * 2, 180, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), this::threadFactory, this::rejectedExecutionHandler);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        listeningExecutorService = MoreExecutors.listeningDecorator(threadPoolExecutor);
    }

    private Thread threadFactory(Runnable r) {
        return new Worker(r, name + "-Worker-" + nameCounter.getAndIncrement());
    }

    private void rejectedExecutionHandler(Runnable r, ThreadPoolExecutor executor) {
        auditor.error("[Executor][rejectedExecutionHandler] {} reject task", name);
        throw new IllegalStateException("[Executor][rejectedExecutionHandler] " + name + " reject task");
    }
}
