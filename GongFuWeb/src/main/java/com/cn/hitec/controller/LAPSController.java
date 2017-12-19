package com.cn.hitec.controller;

import com.cn.hitec.bean.EsQueryBean_web;
import com.cn.hitec.service.BasicResource;
import com.cn.hitec.service.LAPSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Autowired
    private LAPSService lapsService;
    @Autowired
    private BasicResource basicResource;

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

    /**
     * LAPS数据查询
     * @param esQueryBean
     * @return
     */
    @RequestMapping(value="/getData", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Map getData(@RequestBody EsQueryBean_web esQueryBean){
        Map<String,Object> map = lapsService.getData(esQueryBean);
        return map;
    }


}
