package org.racing.seda;

/**
 * Created by zhouyun on 2016/4/15.
 */
public interface Handler<T> {

    void handle(T t);

}
