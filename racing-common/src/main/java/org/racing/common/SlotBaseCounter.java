package org.racing.common;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于对象插槽的计数器
 * Created by zhouyun on 2016/2/22.
 */
public class SlotBaseCounter {
    private Map<Object, AtomicInteger[]> slotCounterHolder = Maps.newConcurrentMap();
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> new NamedDeamonThread(r, "SlotBaseCounter-Deamon-Thread"));
    private int slotSize;

    { init(); }

    public SlotBaseCounter(int slotSize) {
        slotSize = slotSize < 1 ? 1 : slotSize;
        this.slotSize = slotSize;
    }

    public void init() {
        scheduledExecutorService.scheduleAtFixedRate(this::wipeZero, 5, 5, TimeUnit.MINUTES);
        Hooks.shutdown(this::destroy);
    }

    public void increaseSlot(Object target, int slot) {
        AtomicInteger[] slotCounter = getSlotCounter(target);
        if (slotCounter.length > slot) {
            slotCounter[slot].incrementAndGet();
        }
    }

    public AtomicInteger[] getSlotCounter(Object target) {
        return slotCounterHolder.computeIfAbsent(target, this::createAtomicInteger);
    }

    private AtomicInteger[] createAtomicInteger(Object key) {
        AtomicInteger[] slotCounter = new AtomicInteger[slotSize];
        Loops.repeat(i -> slotCounter[i] = new AtomicInteger(0), this.slotSize);
        return slotCounter;
    }

    public void wipeSlot(int slot) {
        slotCounterHolder.entrySet().stream().filter(entry -> entry.getValue().length > slot).forEach(entry -> entry.getValue()[slot].set(0));
    }

    public void wipeZero() {
        slotCounterHolder.entrySet().stream().filter(entry -> totalCount(entry.getKey()) == 0).forEach(entry -> slotCounterHolder.remove(entry.getKey()));
    }

    public int totalCount(Object target) {
        return Arrays.stream(getSlotCounter(target)).mapToInt(slotCounter -> slotCounter.get()).sum();
    }

    public int totalCount() {
        return slotCounterHolder.values().stream().flatMap(counter -> Arrays.stream(counter)).mapToInt(slotCounter -> slotCounter.get()).sum();
    }

    public void destroy() {
        slotCounterHolder.clear();
        scheduledExecutorService.shutdownNow();
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(this);
        slotCounterHolder.entrySet().forEach(entry -> toStringHelper.add("" + entry.getKey(), Arrays.toString(entry.getValue())));
        return toStringHelper.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        this.destroy();
    }
}
