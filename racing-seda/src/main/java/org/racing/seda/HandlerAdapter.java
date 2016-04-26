package org.racing.seda;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Created by zhouyun on 2016/4/15.
 */
public class HandlerAdapter<T> implements Runnable {
    private List<Handler<T>> handlers;
    private T message;

    public HandlerAdapter(T message, List<Handler<T>> handlers) {
        this.message = Preconditions.checkNotNull(message);
        this.handlers = Preconditions.checkNotNull(handlers);
    }

    @Override
    public void run() {
        handlers.forEach(handler -> handler.handle(message));
    }
}
