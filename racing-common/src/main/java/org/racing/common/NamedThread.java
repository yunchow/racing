package org.racing.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouyun on 2016/2/24.
 */
public class NamedThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public NamedThread() {
        setDaemon(false);
    }

    public NamedThread(String name) {
        super(name);
    }

    public NamedThread(Runnable target) {
        super(target);
    }

    public NamedThread(Runnable target, String name) {
        super(target, name);
    }

    @Override
    public synchronized void start() {
        logger.debug("[NamedThread][{}] started", getName());
        super.start();
    }
}
