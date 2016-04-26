package org.racing.seda.provider;

import org.racing.seda.Sink;

import java.util.UUID;

/**
 * Created by zhouyun on 2016/4/23.
 */
public class UuidSink implements Sink {
    private int mills = 50;

    public UuidSink() {
    }

    public UuidSink(int mills) {
        this.mills = mills;
    }

    @Override
    public String take() {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {

        }
        return UUID.randomUUID().toString();
    }

    @Override
    public long size() {
        return -1;
    }
}
