package org.racing.boot.web;

import org.racing.seda.NamedTopology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zhouyun on 2016/4/19.
 */
@Controller
@RequestMapping("/topology")
public class TopologyController {

    @Autowired(required = false)
    private List<NamedTopology> topologies;

    @RequestMapping("/dashboard")
    public String dashboard(Model model, @RequestParam(defaultValue = "RacingTopology")  String topName) {
        model.addAttribute("tops", topologies.stream().map(topology -> topology.capatureTop()).collect(Collectors.toList()));
        topologies.stream().filter(topology -> topology.getName().equals(topName)).forEach(top -> {
            model.addAttribute("top", top.capatureTop());
        });
        return "dashboard";
    }

    @RequestMapping("/shutdown/{topName}/{stageName}")
    public String shutdown(@PathVariable String topName, @PathVariable String stageName, Model model) {
        topologies.stream().filter(topology -> topology.getName().equals(topName)).forEach(top -> top.shutdown(stageName));
        return "redirect:/topology/dashboard?topName=" + topName;
    }

    @RequestMapping("/worksize/{topName}/{stageName}/{size}")
    public String setWorkSize(@PathVariable String topName, @PathVariable String stageName, @PathVariable int size, Model model) {
        topologies.stream().filter(topology -> topology.getName().equals(topName)).forEach(top -> top.setWorkSize(stageName, size));
        return "redirect:/topology/dashboard?topName=" + topName;
    }

    @RequestMapping("/parallelism/{topName}/{stageName}/{size}")
    public String setParallelism(@PathVariable String topName, @PathVariable String stageName, @PathVariable int size, Model model) {
        topologies.stream().filter(topology -> topology.getName().equals(topName)).forEach(top -> top.setParallelism(stageName, size));
        return "redirect:/topology/dashboard?topName=" + topName;
    }

    @RequestMapping("/start/{topName}/{stageName}")
    public String start(@PathVariable String topName, @PathVariable String stageName, Model model) {
        topologies.stream().filter(topology -> topology.getName().equals(topName)).forEach(top -> top.start(stageName));
        return "redirect:/topology/dashboard?topName=" + topName;
    }

    @RequestMapping("/reset/{topName}")
    public String reset(@PathVariable String topName, Model model) {
        topologies.stream().filter(topology -> topology.getName().equals(topName)).forEach(top -> top.reset());
        return "redirect:/topology/dashboard?topName=" + topName;
    }

    @RequestMapping("/shutdown/{topName}")
    public String shutdown(@PathVariable String topName, Model model) {
        topologies.stream().filter(topology -> topology.getName().equals(topName)).forEach(top -> top.shutdown());
        return "redirect:/topology/dashboard?topName=" + topName;
    }

    @RequestMapping("/start/{topName}")
    public String start(@PathVariable String topName, Model model) {
        topologies.stream().filter(topology -> topology.getName().equals(topName)).forEach(top -> top.start());
        return "redirect:/topology/dashboard?topName=" + topName;
    }

}
