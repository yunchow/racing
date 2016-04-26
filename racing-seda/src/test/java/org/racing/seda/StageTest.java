package org.racing.seda;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.racing.seda.provider.OutputHandler;
import org.racing.seda.provider.StringDecoder;
import org.racing.seda.provider.UuidSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhouyun on 2016/4/15.
 */
public class StageTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private AtomicInteger counter = new AtomicInteger(0);

    @Test
    public void testOutput() throws InterruptedException {
        Stage<String> stage = new Stage<String>("TestStage");
        stage.setSink(new UuidSink());
        stage.setHandlers(Lists.newArrayList(new OutputHandler()));
        stage.setDecoder(new StringDecoder());
        stage.start();
        Thread.sleep(1000);
        stage.shutdown();
    }
}
