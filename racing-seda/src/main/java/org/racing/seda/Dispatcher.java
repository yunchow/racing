package org.racing.seda;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhouyun on 2016/4/15.
 */
public class Dispatcher<T> {
    private List<Executor> executors;
    private DispatcherStrategy strategy = this::roundExecutor;
    private AtomicInteger roundCounter = new AtomicInteger(0);

    public Dispatcher(List<Executor> executors) {
        this.executors = Preconditions.checkNotNull(executors);
        Preconditions.checkState(this.executors.size() > 0);
    }

    public ListenableFuture<Void> dispatch(HandlerAdapter<T> handlerAdapter) {
        Preconditions.checkNotNull(strategy);
        return strategy.selectExecutor(executors).submit(handlerAdapter);
    }

    public Executor randomExecutor(List<Executor> executors) {
        return executors.get(new Random(executors.size()).nextInt());
    }

    public Executor roundExecutor(List<Executor> executors) {
        int size = executors.size();
        return executors.get(roundCounter.getAndUpdate(i -> (i + 1) % size) % size);
    }

    public DispatcherStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(DispatcherStrategy strategy) {
        this.strategy = strategy;
    }
}
