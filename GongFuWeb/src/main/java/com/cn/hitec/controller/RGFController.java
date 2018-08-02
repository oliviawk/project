package com.cn.hitec.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/RGF")
public class RGFController {

    @RequestMapping("/")
    public String index(){

        return "RGF/produce";
    }
}
