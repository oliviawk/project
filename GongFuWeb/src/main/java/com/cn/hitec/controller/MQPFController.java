package com.cn.hitec.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/MQPF")
public class MQPFController {

    @RequestMapping("/")
    public String index(){

        return "MQPF/produceTest";
    }

}
