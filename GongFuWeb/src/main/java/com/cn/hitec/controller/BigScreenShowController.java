package com.cn.hitec.controller;

import com.cn.hitec.bean.EsQueryBean_web;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.service.OCFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/show")
public class BigScreenShowController {

    @Autowired
    EsQueryService esQueryService;

    @RequestMapping("/")
    public String index(){

        return "bigScreenShow/bigScreenShow";
    }


    /**
     *
     * @param str
     *
     * @return
     */
    @RequestMapping(value = "/getfilesize", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public List<Object> getFileSizeCount(@RequestBody String str){

        return esQueryService.getFileSizeCount(str);
    }


}
