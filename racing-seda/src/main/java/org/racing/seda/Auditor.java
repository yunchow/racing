package org.racing.seda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouyun on 2016/4/15.
 */
public class Auditor {
    private final static Auditor auditor = new Auditor();
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public static Auditor getAuditor() {
        return auditor;
    }

    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

    public void error(String format, Object... arguments) {
        logger.error(format, arguments);
    }

    public void info(String format, Object... arguments) {
        logger.info(format, arguments);
    }

    public void debug(String format, Object... arguments) {
        logger.debug(format, arguments);
    }

}
