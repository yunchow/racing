package org.racing.seda;

/**
 * Created by zhouyun on 2016/4/22.
 */
public abstract class SinkAdapter implements Sink {
    private String sinkName;

    public SinkAdapter() {
    }

    public SinkAdapter(String sinkName) {
        this.sinkName = sinkName;
    }

    @Override
    public abstract String take();

    @Override
    public abstract long size();

    public String getSinkName() {
        return sinkName;
    }

    public void setSinkName(String sinkName) {
        this.sinkName = sinkName;
    }
}
