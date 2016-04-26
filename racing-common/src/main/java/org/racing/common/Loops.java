package org.racing.common;

import com.google.common.base.Throwables;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by zhouyun on 2016/2/22.
 */
public final class Loops {

    private Loops() {}

    public static void dieLoop(Runnable runnable) {
        while (true) {
            run(runnable);
        }
    }

    public static void rateLoop(Runnable runnable, int mills) {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(mills);
            } catch (InterruptedException e) {

            }
            run(runnable);
        }
    }

    public static void repeat(Consumer<Integer> consumer, int loop) {
        for (int i = 0; i < loop; i++) {
            consumer.accept(i);
        }
    }

    public static void repeat(Runnable runnable, int loop) {
        for (int i = 0; i < loop; i++) {
            runnable.run();
        }
    }

    private static void run(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            Throwables.propagate(e);
        }
    }
}
