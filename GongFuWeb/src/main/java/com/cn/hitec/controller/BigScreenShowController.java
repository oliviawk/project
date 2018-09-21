package com.cn.hitec.controller;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.EsQueryBean_web;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.service.OCFService;
import com.cn.hitec.tools.HttpPub;
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



    /**
     *  根据utl 获取数据 / 防止页面直接调用引起的跨域问题
     * @param url
     *
     * @return
     */
    @RequestMapping(value = "/getoutherdata", method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getOutherData( String url){
        System.out.println(url);
        if(url.indexOf("http") == -1){
            return null;
        }
        Map<String,Object> res = HttpPub.getData(url);
        System.out.println(JSON.toJSONString(res));
        return res;
    }

}
