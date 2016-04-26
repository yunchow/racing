package org.racing.seda;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhouyun on 2016/4/19.
 */
public class Metrics implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * Stage名称
     */
    private String stageName;

    /**
     * 状态
     */
    private String state;

    /**
     * acceptor状态
     */
    private String acceptorState;

    /**
     * 并行度
     */
    private int paralellism;

    /**
     * 初始化工作大小
     */
    private int workSize;

    /**
     * 工作线程大小
     */
    private int coreSize;

    /**
     * 最大并发大小
     */
    private int maxPoolSize;

    /**
     * 等待处理大小
     */
    private long waitingSinkSize;

    /**
     * 本地缓冲队列大小
     */
    private int localQueueSize;

    /**
     * 共处理事件数量
     */
    private long totalEventCount;

    /**
     * 5分钟内处理事件数量
     */
    private long eventCount5;

    /**
     * 10分钟内处理事件数量
     */
    private long eventCount10;

    /**
     * 30分钟内处理事件数量
     */
    private long eventCount30;

    /**
     * 成功处理计数
     */
    private long successCount;

    /**
     * 处理失败计数
     */
    private long failedCount;

    /**
     * 启动时间
     */
    private Date startTime;

    public String getAcceptorState() {
        return acceptorState;
    }

    public void setAcceptorState(String acceptorState) {
        this.acceptorState = acceptorState;
    }

    public long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public int getParalellism() {
        return paralellism;
    }

    public void setParalellism(int paralellism) {
        this.paralellism = paralellism;
    }

    public int getWorkSize() {
        return workSize;
    }

    public void setWorkSize(int workSize) {
        this.workSize = workSize;
    }

    public long getWaitingSinkSize() {
        return waitingSinkSize;
    }

    public void setWaitingSinkSize(long waitingSinkSize) {
        this.waitingSinkSize = waitingSinkSize;
    }

    public int getLocalQueueSize() {
        return localQueueSize;
    }

    public void setLocalQueueSize(int localQueueSize) {
        this.localQueueSize = localQueueSize;
    }

    public long getTotalEventCount() {
        return totalEventCount;
    }

    public void setTotalEventCount(long totalEventCount) {
        this.totalEventCount = totalEventCount;
    }

    public long getEventCount5() {
        return eventCount5;
    }

    public void setEventCount5(long eventCount5) {
        this.eventCount5 = eventCount5;
    }

    public long getEventCount10() {
        return eventCount10;
    }

    public void setEventCount10(long eventCount10) {
        this.eventCount10 = eventCount10;
    }

    public long getEventCount30() {
        return eventCount30;
    }

    public void setEventCount30(long eventCount30) {
        this.eventCount30 = eventCount30;
    }

    public long getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(long failedCount) {
        this.failedCount = failedCount;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getCoreSize() {
        return coreSize;
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    @Override
    public String toString() {
        return "Metrics{" +
                "stageName='" + stageName + '\'' +
                ", state='" + state + '\'' +
                ", acceptorState='" + acceptorState + '\'' +
                ", paralellism=" + paralellism +
                ", workSize=" + workSize +
                ", coreSize=" + coreSize +
                ", maxPoolSize=" + maxPoolSize +
                ", waitingSinkSize=" + waitingSinkSize +
                ", localQueueSize=" + localQueueSize +
                ", totalEventCount=" + totalEventCount +
                ", eventCount5=" + eventCount5 +
                ", eventCount10=" + eventCount10 +
                ", eventCount30=" + eventCount30 +
                ", successCount=" + successCount +
                ", failedCount=" + failedCount +
                ", startTime=" + startTime +
                '}';
    }
}
