package org.racing.demo.stage;

import com.google.common.collect.Lists;
import org.racing.seda.Handler;
import org.racing.seda.Stage;
import org.racing.seda.provider.OutputHandler;
import org.racing.seda.provider.StringDecoder;
import org.racing.seda.provider.UuidSink;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

/**
 * Created by zhouyun on 2016/4/23.
 */
@Component
public class DemoStage extends Stage<String> {

    private ArrayList<Handler<String>> outputHandlers = Lists.newArrayList();

    public DemoStage() {
        super("DemoStage");
        outputHandlers.add(new OutputHandler<String>());
    }

    @PostConstruct
    public void init() {
        setSink(new UuidSink(2000));
        setDecoder(new StringDecoder());
        setHandlers(outputHandlers);
    }
}
