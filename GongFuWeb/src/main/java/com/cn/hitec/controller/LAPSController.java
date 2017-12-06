package com.cn.hitec.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * ClassName:
 * Description: LAPS controller
 * author: fukl
 * data: 2017年12月6日 下午3:05
 */
@Controller
@RequestMapping("/laps")
public class LAPSController {
    private static final Logger logger = LoggerFactory.getLogger(LAPSController.class);

    @RequestMapping("/sxt")
    public String index() {
        // System.out.println("进入MICAPS4.html");
        return "LAPS/fuzhujuece";
    }

    @RequestMapping("/lct")
    public String lct() {
        // System.out.println("进入MICAPS4.html");
        return "LAPS/produce";
    }


}
