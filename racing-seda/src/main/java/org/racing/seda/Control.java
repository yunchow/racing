package org.racing.seda;

/**
 * Created by zhouyun on 2016/4/15.
 */
public class Control {

    private Scheduler<?> scheduler;

    public Control(Scheduler<?> scheduler) {
        this.scheduler = scheduler;
    }

    public int getParallelism() {
        return scheduler.getParallelism();
    }

    public void setParallelism(int parallelism) {
        scheduler.setParallelism(parallelism);
        scheduler.renewScheduler();
    }

    public int getWorkSize() {
        return scheduler.getWorkSize();
    }

    public void setWorkSize(int workSize) {
        scheduler.setWorkSize(workSize);
        scheduler.renewScheduler();
    }

    public void updateWorkSize(int workSize) {
        scheduler.updateWorkSize(workSize);
    }

    public void updateParallelism(int parallelism) {
        scheduler.updateParallelism(parallelism);
    }

}
