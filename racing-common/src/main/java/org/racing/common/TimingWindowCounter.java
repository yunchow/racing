package org.racing.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.*;

/**
 * 基于时间的滑动窗口计数器 计数窗口在 period 时间间隔(秒)自动向前滑动
 * Created by zhouyun on 2016/2/22.
 */
public class TimingWindowCounter {
	private volatile int windowSize;
	private volatile int slidingPeriod;
	private ScheduledExecutorService scheduledExecutorService;
	private SlidingWindowCounter slidingWindowCounter;

	{
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> new NamedDeamonThread(r, "TimingWindowCounter-Deamon-Thread"));
	}

	public TimingWindowCounter(int size, int period) {
		checkArgument(size > 0);
		checkArgument(period > 0);
		this.windowSize = size;
		this.slidingPeriod = period;
		slidingWindowCounter = new SlidingWindowCounter(this.windowSize);
		installScheduleWorker();
	}

	public void installScheduleWorker() {
		scheduledExecutorService.scheduleAtFixedRate(this::advance, 1, this.slidingPeriod, TimeUnit.SECONDS);
		Hooks.shutdown(this::destroy);
	}

	public void resizeWindow(int windowSize) {
		this.windowSize = windowSize;
		slidingWindowCounter.resizeWindow(windowSize);
	}

	public void increase(Object target) {
		slidingWindowCounter.increase(target);
	}

	public void increase() {
		slidingWindowCounter.increase();
	}

	public int totalAndAdvance(Object target) {
		return slidingWindowCounter.totalAndAdvance(target);
	}

	public int totalAndAdvance() {
		return slidingWindowCounter.totalAndAdvance();
	}

	public void advance() {
		slidingWindowCounter.advance();
	}

	public int totalCount(Object target) {
		return slidingWindowCounter.totalCount(target);
	}

	public int totalCount() {
		return slidingWindowCounter.totalCount();
	}

	public int getWindowSize() {
		return windowSize;
	}

	public int getSlidingPeriod() {
		return slidingPeriod;
	}

	public void destroy() {
		this.scheduledExecutorService.shutdownNow();
	}

	@Override
	public String toString() {
		return slidingWindowCounter.toString();
	}
}
