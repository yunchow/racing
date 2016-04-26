package org.racing.seda;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zhouyun on 2016/4/22.
 */
public class NamedTopology {
    protected Auditor auditor = Auditor.getAuditor();
    protected State state = State.TERMINATED;
    protected String name;
    private List<Stage<?>> stages;

    public NamedTopology(String name) {
        this.name = name;
    }

    public void start() {
        Preconditions.checkState(State.TERMINATED.equals(state));
        Preconditions.checkNotNull(stages);
        stages.stream().filter(stage -> stage.getState().equals(State.TERMINATED)).forEach(stage -> stage.start());
        state = State.RUNNING;
        auditor.info("Topology started successfully.");
    }

    public void start(String stageName) {
        stages.stream().filter(stage -> stage.getState().equals(State.TERMINATED))
                .filter(stage -> stage.getName().equals(stageName)).forEach(stage -> stage.start());
    }

    public void setWorkSize(String stageName, int workSize) {
        Preconditions.checkState(workSize > 0);
        stages.stream().filter(stage -> stage.getState().equals(State.RUNNING))
                .filter(stage -> stage.getName().equals(stageName)).forEach(stage -> stage.getControl().updateWorkSize(workSize));
    }

    public void setParallelism(String stageName, int parallelism) {
        Preconditions.checkState(parallelism > 0);
        stages.stream().filter(stage -> stage.getState().equals(State.RUNNING))
                .filter(stage -> stage.getName().equals(stageName)).forEach(stage -> stage.getControl().updateParallelism(parallelism));
    }

    public void reset() {
        stages.forEach(stage -> stage.resetCounters());
    }

    public void shutdown(String stageName) {
        stages.stream().filter(stage -> !stage.getState().equals(State.TERMINATED))
                .filter(stage -> stage.getName().equals(stageName)).forEach(stage -> stage.shutdown());
    }

    public TopologySnapshort capatureTop() {
        TopologySnapshort ts = new TopologySnapshort();
        ts.setName(name);
        ts.setState(state.value());
        ts.setMetricses(getMetrics());
        return ts;
    }

    public List<Metrics> getMetrics() {
        return stages.stream().map(stage -> {
            stage.refreshMetrics();
            return stage.getMetrics();
        }).collect(Collectors.toList());
    }

    public void shutdown() {
        Preconditions.checkState(State.RUNNING.equals(state));
        destroy();
    }

    public void destroy() {
        stages.stream().filter(stage -> !stage.getState().equals(State.TERMINATED)).forEach(stage -> stage.shutdown());
        auditor.info("Topology shutdown successfully.");
        state = State.TERMINATED;
    }

    public List<Stage<?>> getStages() {
        return stages;
    }

    public void setStages(List<Stage<?>> stages) {
        this.stages = stages;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
