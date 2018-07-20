package com.cn.hitec.controller;


import com.cn.hitec.bean.EsQueryBean_web;
import com.cn.hitec.service.MQPFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/MQPF")
public class MQPFController {
    @Autowired
    MQPFService mqpfService;

    @RequestMapping("/")
    public String index(){

        return "MQPF/produce";
    }
    @RequestMapping("/test")
    public String indexTest(){
        return "MQPF/produceTest";
    }


    @RequestMapping(value = "/MQPFAggQuery", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Map lctAggQuery() {
        Map<String, Object> map = mqpfService.MQPFAggQuery();
//        JSON.toJSONStringWithDateFormat(map,"yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);     //fastjson 插件异常处理方法
        return map;
    }

    @RequestMapping(value = "/getHistory", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Map getHistory(@RequestBody EsQueryBean_web esQueryBean) {
        Map<String, Object> map = mqpfService.getHistory(esQueryBean);
        return map;
    }

}
