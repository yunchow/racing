package org.racing.seda;

import java.util.Date;
import java.util.List;

/**
 * Created by zhouyun on 2016/4/20.
 */
public class TopologySnapshort {
    private String name;
    private String state;
    private List<Metrics> metricses;
    private Date time = new Date();

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Metrics> getMetricses() {
        return metricses;
    }

    public void setMetricses(List<Metrics> metricses) {
        this.metricses = metricses;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
