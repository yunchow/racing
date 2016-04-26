package org.racing.seda;

import com.google.common.base.Preconditions;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhouyun on 2016/4/15.
 */
public class Acceptor implements Runnable {
    protected Auditor auditor = Auditor.getAuditor();
    private volatile boolean stop = false;
    private Sink sink;
    private BlockingQueue<String> taskQueue;
    private volatile int acceptEventCount = 0;

    public Acceptor() {
        taskQueue = new LinkedBlockingQueue<>();
    }

    public Acceptor(Sink sink) {
        this();
        this.sink = Preconditions.checkNotNull(sink);
    }

    @Override
    public final void run() {
        while (!stop && !Thread.interrupted()) {
            /*auditor.debug("[Acceptor][{}] stop = {}, iterrupted = {}",
                    sink, stop, Thread.currentThread().isInterrupted());*/
            String accept = accept();
            if (accept != null) {
                taskQueue.add(accept);
                acceptEventCount++;
                //auditor.info("[Acceptor][{}] accept event: {}", Thread.currentThread().getName(), accept);
                //auditor.info("[Acceptor][{}] queueSize = {}", Thread.currentThread().getName(), taskQueue.size());
            }
        }
        auditor.info("[Acceptor][{}] stoped completely.", Thread.currentThread().getName());
    }

    public String take() throws InterruptedException {
        return taskQueue.poll(50, TimeUnit.MILLISECONDS);
    }

    public int getLocalQueueSize() {
        return this.taskQueue.size();
    }

    public int getAcceptEventCount() {
        return this.acceptEventCount;
    }

    public void setAcceptEventCount(int acceptEventCount) {
        this.acceptEventCount = acceptEventCount;
    }

    public long getSinkSize() {
        return getSink().size();
    }

    public Sink getSink() {
        return sink;
    }

    public void reset() {
        this.stop = false;
    }

    public void stop() {
        this.stop = true;
    }

    public void setSink(Sink sink) {
        this.sink = sink;
    }

    public String accept() {
        return sink.take();
    }
}
