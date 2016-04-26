package org.racing.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouyun on 2016/2/24.
 */
public class NamedDeamonThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public NamedDeamonThread() {
        setDaemon(true);
    }

    public NamedDeamonThread(String name) {
        super(name);
        setDaemon(true);
    }

    public NamedDeamonThread(Runnable target) {
        super(target);
        setDaemon(true);
    }

    public NamedDeamonThread(Runnable target, String name) {
        super(target, name);
        setDaemon(true);
    }

    @Override
    public synchronized void start() {
        logger.debug("[NamedDeamonThread][{}] started", getName());
        super.start();
    }
}
