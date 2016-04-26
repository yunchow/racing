package org.racing.boot;

import org.racing.seda.NamedTopology;
import org.racing.seda.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by zhouyun on 2016/4/23.
 */
@Configuration
@ComponentScan
public class RacingAutoConfiguration {

    @Autowired(required = false)
    private List<Stage<?>> stages;

    @Bean
    public NamedTopology topologyFactory() {
        NamedTopology topology = new NamedTopology("RacingTopology");
        if (stages != null) {
            topology.setStages(stages);
            topology.start();
        }
        return topology;
    }

}
