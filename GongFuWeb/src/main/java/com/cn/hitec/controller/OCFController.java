package com.cn.hitec.controller;

import com.cn.hitec.bean.EsQueryBean_web;
import com.cn.hitec.service.OCFService;
import com.cn.hitec.service.RGFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;


@Controller
@RequestMapping("/OCF")
public class OCFController {

    @Autowired
    OCFService ocfService;

    @RequestMapping("/")
    public String index(){

        return "OCF/produce";
    }


    @RequestMapping(value = "/rgfAggQuery", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Map lctAggQuery() {
        Map<String, Object> map = ocfService.aggQuery();
        return map;
    }

    @RequestMapping(value = "/getHistory", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Map getHistory(@RequestBody EsQueryBean_web esQueryBean) {
        Map<String, Object> map = ocfService.getHistorys(esQueryBean);
        return map;
    }

}
