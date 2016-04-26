package org.racing.seda.provider;

import org.racing.seda.Handler;

/**
 * Created by zhouyun on 2016/4/23.
 */
public class OutputHandler<T> implements Handler<T> {
    @Override
    public void handle(T s) {
        System.out.println(s);
    }
}
