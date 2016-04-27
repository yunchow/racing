package org.racing.seda;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.racing.common.Loops;
import org.racing.common.NamedDeamonThread;

import java.util.List;

/**
 * Created by zhouyun on 2016/4/27.
 */
public class DumpDeamon extends NamedDeamonThread {
    private List<NamedTopology> tops = Lists.newArrayList();

    public DumpDeamon(NamedTopology top) {
        super("DumpDeamon");
        this.tops.add(Preconditions.checkNotNull(top));
    }

    public DumpDeamon(List<NamedTopology> tops) {
        super("DumpDeamon");
        this.tops.addAll(Preconditions.checkNotNull(tops));
    }

    @Override
    public void run() {
        Loops.rateLoop(() -> tops.forEach(NamedTopology::dumpTopology), 30 * 1000);
    }

}
