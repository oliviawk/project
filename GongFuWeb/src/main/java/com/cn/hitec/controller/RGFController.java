package com.cn.hitec.controller;


import com.cn.hitec.bean.EsQueryBean_web;
import com.cn.hitec.service.RGFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/RGF")
public class RGFController {

    @Autowired
    RGFService rgfService;

    @RequestMapping("/")
    public String index(){

        return "RGF/produce";
    }


    @RequestMapping(value = "/rgfAggQuery", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Map lctAggQuery() {
        Map<String, Object> map = rgfService.aggQuery();
        return map;
    }

    @RequestMapping(value = "/getHistory", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Map getHistory(@RequestBody EsQueryBean_web esQueryBean) {
        Map<String, Object> map = rgfService.getHistorys(esQueryBean);
        return map;
    }
}
