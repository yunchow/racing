package org.racing.common;

/**
 * 滑动窗口计数器，支持多个计数器同时计数
 * Created by zhouyun on 2016/2/22.
 */
public class SlidingWindowCounter {
    public static final String DEFAULT_TARGET = "__default_target__";
    private volatile SlotBaseCounter slotBaseCounter;
    private volatile int windowSize;
    private volatile int head;

    public SlidingWindowCounter(int windowSize) {
        resizeWindow(windowSize);
    }

    protected void resizeWindow(int windowSize) {
        this.windowSize = windowSize;
        this.slotBaseCounter = new SlotBaseCounter(windowSize);
        this.head = 0;
    }

    public void increase() {
        increase(DEFAULT_TARGET);
    }

    public void increase(Object target) {
        slotBaseCounter.increaseSlot(target, head);
    }

    public int totalAndAdvance() {
        return totalAndAdvance(DEFAULT_TARGET);
    }

    public int totalAndAdvance(Object target) {
        int total = totalCount(target);
        advance();
        return total;
    }

    public synchronized void advance() {
        int tail = (head + 1) % windowSize;
        slotBaseCounter.wipeSlot(tail);
        head = tail;
    }

    public int totalCount() {
        return totalCount(DEFAULT_TARGET);
    }

    public int totalCount(Object target) {
        return slotBaseCounter.totalCount(target);
    }

    @Override
    public String toString() {
        return "total = " + slotBaseCounter.totalCount() + ", head = " + head + " >> " + slotBaseCounter;
    }
}
