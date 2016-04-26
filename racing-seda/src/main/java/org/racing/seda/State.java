package org.racing.seda;

/**
 * Created by zhouyun on 2016/4/20.
 */
public enum State {
    TERMINATED("terminated"), RUNNING("running"), SHUTTING_DOWN("shutingdown"), STARTING("starting");

    private String value;

    State(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
