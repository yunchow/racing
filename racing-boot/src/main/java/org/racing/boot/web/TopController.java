package org.racing.boot.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

/**
 * Created by zhouyun on 2016/4/23.
 */
@Controller
public class TopController {

    @RequestMapping("/")
    public String dashboard() {
        return "/topology/dashboard";
    }

    @ResponseBody
    @RequestMapping("/ping")
    public String ping() {
        return "pong: " + LocalDateTime.now();
    }
}
