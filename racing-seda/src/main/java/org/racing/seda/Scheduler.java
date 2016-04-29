package org.racing.seda;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.racing.common.NamedThread;
import org.racing.common.TimingWindowCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zhouyun on 2016/4/15.
 */
public class Scheduler<T> implements FutureCallback<Void> {
    private volatile boolean stop = false;
    private Auditor auditor = Auditor.getAuditor();
    private Thread acceptorThread;
    private AtomicInteger executorNameCounter = new AtomicInteger(0);
    private AtomicInteger acceptNameCounter = new AtomicInteger(0);
    private String name;
    private volatile int parallelism = 3;
    private volatile int workSize = 5;
    private List<Handler<T>> handlers;
    private List<Executor> executors;
    private Acceptor acceptor;
    private Decoder<T> decoder;
    private Dispatcher<T> dispatcher;
    private AtomicLong successCount = new AtomicLong(0);
    private AtomicLong failedCount = new AtomicLong(0);
    private TimingWindowCounter twcm5;
    private TimingWindowCounter twcm10;
    private TimingWindowCounter twcm30;

    public Scheduler(String name) {
        this(name, 5, 3);
    }

    public Scheduler(String name, int parallelism) {
        this(name, 5, parallelism);
    }

    public Scheduler(String name, int workSize, int parallelism) {
        this.name = name;
        this.workSize = workSize;
        this.parallelism = parallelism;
    }

    public void renewScheduler() {
        checkArgumentState();
        if (executors != null) { // release old executors
            executors.forEach(executor -> executor.shutdown());
        }
        executors = new CopyOnWriteArrayList<Executor>();
        for (int i = 0; i < parallelism; i++) {
            executors.add(renewExecutor());
        }
        dispatcher = new Dispatcher<T>(executors);

        this.acceptor.reset();
        this.acceptorThread = new NamedThread(Preconditions.checkNotNull(acceptor), name + "-Acceptor");

        twcm5 = new TimingWindowCounter(5, 1);
        twcm10 = new TimingWindowCounter(10, 1);
        twcm30 = new TimingWindowCounter(30, 1);
    }

    public void checkArgumentState() {
        Preconditions.checkNotNull(name);
        Preconditions.checkState(parallelism > 0);
        Preconditions.checkState(workSize > 0);
    }

    public void resetState() {
        this.stop = false;
    }

    public void schedule() {
        Preconditions.checkState(!stop);
        Preconditions.checkState(executors.size() > 0);
        Preconditions.checkState(handlers.size() > 0);
        Preconditions.checkNotNull(acceptor);
        Preconditions.checkNotNull(decoder);
        Preconditions.checkNotNull(dispatcher);

        // start to listen the input message
        // and then dispatch to the handlers
        this.acceptSinkEventAndAwait();

        while (!stop && !Thread.interrupted()) {
            try {
                String message = acceptor.take();
                if (message != null) {
                    HandlerAdapter<T> handlerAdapter = new HandlerAdapter<T>(decode(message), handlers);
                    ListenableFuture<Void> future = dispatcher.dispatch(handlerAdapter);
                    Futures.addCallback(future, this);
                    //auditor.info("[Scheduler][schedule] dispatch message: {}", message);
                }
            } catch (InterruptedException e) {
                auditor.info("[Scheduler][schedule] is interrupted");
            } catch (Exception e) {
                auditor.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void onSuccess(Void result) {
        handleSucessAndCount();
    }

    @Override
    public void onFailure(Throwable t) {
        this.failedCount.incrementAndGet();
        auditor.error(t.getMessage(), t);
    }

    public T decode(String message) {
        return decoder.decode(message);
    }

    private void handleSucessAndCount() {
        twcm5.increase();
        twcm10.increase();
        twcm30.increase();
        successCount.incrementAndGet();
    }

    public void acceptSinkEventAndAwait() {
        this.acceptor.reset();
        this.acceptorThread = new NamedThread(Preconditions.checkNotNull(acceptor), name + "-Acceptor");
        this.acceptorThread.start();
        auditor.info("[Scheduler][{}] acceptor is ready({})", name, acceptorThread.getState().name());
    }

    public void updateWorkSize(int workSize) {
        Preconditions.checkNotNull(executors).forEach(executor -> executor.setWorkSize(workSize));
        setWorkSize(workSize);
        auditor.info("[Scheduler][updateWorkSize] updateWorkSize successfully");
    }

    public void updateParallelism(int parallelism) {
        Preconditions.checkState(parallelism > 0);
        if (executors.size() > parallelism) {
            for (int i = 0; i < executors.size() - parallelism; i++) {
                Executor executor = executors.remove(0);
                executor.shutdown();
            }
        }
        if (executors.size() < parallelism) {
            for (int i = 0; i < parallelism - executors.size(); i++) {
                executors.add(renewExecutor());
            }
        }
        setParallelism(parallelism);
        auditor.info("[Scheduler][updateParallelism] updateParallelism successfully");
    }

    public void shutdown() {
        auditor.info("[Scheuler][{}] is shutting down", name);
        this.acceptor.stop();
        this.acceptorThread.interrupt();
        Preconditions.checkNotNull(executors).forEach(executor -> executor.shutdown());
        this.stop = true;
        twcm5.destroy();
        twcm10.destroy();
        twcm30.destroy();
        auditor.info("[Scheuler][{}] shut down completely", name);
    }

    private Executor renewExecutor() {
        return new Executor(name + "-Executor-" + executorNameCounter.getAndIncrement(), workSize);
    }

    /**
     * 工作线程大小
     * @return
     */
    public int getCurrentWorkSize() {
        return executors.stream().mapToInt(executor -> executor.getCurrentWorkSize()).sum();
    }

    /**
     * 最大线程数
     * @return
     */
    public int getMaxPoolSize() {
        return executors.stream().mapToInt(executor -> executor.getMaxPoolSize()).sum();
    }

    public long getTaskCount() {
        return executors.stream().mapToLong(executor -> executor.getTaskCount()).sum();
    }

    public String acceptorState() {
        return this.acceptorThread.getState().name();
    }

    public long totalM5() {
        return this.twcm5 == null ? 0 : this.twcm5.totalCount();
    }

    public long totalM10() {
        return this.twcm10 == null ? 0 : this.twcm10.totalCount();
    }

    public long totalM30() {
        return this.twcm30 == null ? 0 : this.twcm30.totalCount();
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public int getWorkSize() {
        return workSize;
    }

    public void setWorkSize(int workSize) {
        this.workSize = workSize;
    }

    public boolean isStop() {
        return this.stop;
    }

    public void resetSucessCount() {
        successCount.set(0);
    }

    public void resetFailedCount() {
        failedCount.set(0);
    }

    public long getSuccessCount() {
        return successCount.get();
    }

    public long getFailedCount() {
        return failedCount.get();
    }

    public Thread threadFactory(Runnable r) {
        return new Worker(r, name + "-Acceptor-Thread" + acceptNameCounter.getAndIncrement());
    }

    public Dispatcher<T> getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(Dispatcher<T> dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Acceptor getAcceptor() {
        return acceptor;
    }

    public void setAcceptor(Acceptor acceptor) {
        this.acceptor = acceptor;
    }

    public List<Handler<T>> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<Handler<T>> handlers) {
        this.handlers = new ArrayList<>(handlers);
    }

    public Decoder<T> getDecoder() {
        return decoder;
    }

    public void setDecoder(Decoder<T> decoder) {
        this.decoder = decoder;
    }
}
