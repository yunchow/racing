package org.racing.seda;

import com.google.common.base.Preconditions;
import org.racing.common.NamedThread;

import java.util.Date;
import java.util.List;

/**
 * Created by zhouyun on 2016/4/15.
 */
public class Stage<T> implements Runnable {
    private Auditor auditor = Auditor.getAuditor();
    private Thread stageRunner;
    private String name;
    private Sink sink;
    private volatile int parallelism = 3;
    private volatile int workSize = 5;
    private volatile boolean init;
    private State state = State.TERMINATED;
    private Acceptor acceptor = new Acceptor();
    private Decoder<T> decoder;
    private Scheduler<T> scheduler;
    private List<Handler<T>> handlers;
    private Control control;
    private Metrics metrics;

    public Stage(String name) {
        this.name = Preconditions.checkNotNull(name);
    }

    public Stage(String name, Sink sink) {
        this(name);
        this.sink = Preconditions.checkNotNull(sink);
    }

    public Stage(String name, Sink sink, List<Handler<T>> handlers) {
        this(name, sink);
        this.handlers = Preconditions.checkNotNull(handlers);
    }

    public void initStage() {
        if (init) {
            return;
        }
        init = true;
        scheduler = new Scheduler<T>(name, workSize, parallelism);
        scheduler.setDecoder(Preconditions.checkNotNull(decoder));
        scheduler.setHandlers(Preconditions.checkNotNull(handlers));
        control = new Control(scheduler);
        metrics = new Metrics();
        metrics.setStageName(name);
        metrics.setStartTime(new Date());
        acceptor.setSink(getSink());
        scheduler.setAcceptor(Preconditions.checkNotNull(acceptor));
    }

    public void refreshMetrics() {
        metrics.setWorkSize(scheduler.getWorkSize());
        metrics.setCoreSize(scheduler.getCurrentWorkSize());
        metrics.setMaxPoolSize(scheduler.getMaxPoolSize());
        metrics.setParalellism(scheduler.getParallelism());
        metrics.setSuccessCount(scheduler.getSuccessCount());
        metrics.setFailedCount(scheduler.getFailedCount());
        metrics.setState(getState().value());
        metrics.setAcceptorState(scheduler.acceptorState());
        metrics.setTotalEventCount(acceptor.getAcceptEventCount());
        metrics.setWaitingSinkSize(acceptor.getSinkSize());
        metrics.setLocalQueueSize((int)scheduler.getTaskCount());
        metrics.setEventCount5(scheduler.totalM5());
        metrics.setEventCount10(scheduler.totalM10());
        metrics.setEventCount30(scheduler.totalM30());
    }

    public State getState() {
        return this.state;
    }

    public void resetCounters() {
        acceptor.setAcceptEventCount(0);
        scheduler.resetFailedCount();
        scheduler.resetSucessCount();
    }

    public void shutdown() {
        auditor.info("[Stage][{}] is shutting down", name);
        this.state = State.SHUTTING_DOWN;
        scheduler.shutdown();
        stageRunner.interrupt();
        stageRunner = null;
        this.state = State.TERMINATED;
        auditor.info("[Stage][{}] shut down completely", name);
    }

    public void start() {
        if (!init) {
            initStage();
        }
        Preconditions.checkState(State.TERMINATED.equals(state));
        this.state = State.STARTING;
        Preconditions.checkNotNull(acceptor);
        if (stageRunner == null) {
            stageRunner = new NamedThread(this, name + "-Scheduler");
        }
        stageRunner.start();
    }

    @Override
    public void run() {
        this.state = State.RUNNING;
        metrics.setStartTime(new Date());
        auditor.info("[Stage][{}] is starting", name);
        scheduler.renewScheduler();
        scheduler.resetState();
        scheduler.schedule();
    }

    public void updateWorkSize(int workSize) {
        this.scheduler.updateWorkSize(workSize);
    }

    public void updateParallelism(int parallelism) {
        this.scheduler.updateParallelism(parallelism);
    }

    public Acceptor getAcceptor() {
        return acceptor;
    }

    public void setAcceptor(Acceptor acceptor) {
        this.acceptor = acceptor;
    }

    public List<Handler<T>> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<Handler<T>> handlers) {
        this.handlers = handlers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Scheduler<T> getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler<T> scheduler) {
        this.scheduler = scheduler;
    }

    public Sink getSink() {
        return sink;
    }

    public void setSink(Sink sink) {
        this.sink = sink;
    }

    public Control getControl() {
        return control;
    }

    public void setControl(Control control) {
        this.control = control;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public int getWorkSize() {
        return workSize;
    }

    public void setWorkSize(int workSize) {
        this.workSize = workSize;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public Decoder<T> getDecoder() {
        return decoder;
    }

    public void setDecoder(Decoder<T> decoder) {
        this.decoder = decoder;
    }
}
