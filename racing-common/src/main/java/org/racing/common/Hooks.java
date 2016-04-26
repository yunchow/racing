package org.racing.common;

/**
 * Created by zhouyun on 2016/2/22.
 */
public final class Hooks {

    private Hooks() {}

    public static void shutdown(Runnable runnable) {
        Runtime.getRuntime().addShutdownHook(new Thread(runnable));
    }

}
