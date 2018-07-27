package com.cn.hitec.controller;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.EsQueryBean_web;
import com.cn.hitec.service.BasicResource;
import com.cn.hitec.service.FZJCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * ClassName:
 * Description: music
 * author: fukl
 * data: 2017年07月19日 下午5:05
 */
@Controller
@RequestMapping("/basicresource")
public class BasicResourceController {

    @Autowired
    private BasicResource basicResource;


    @RequestMapping(value = "/getDirectoryUsedData", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Object getDirectoryUsedData(@RequestBody String json) {
        Map<String, Object> params = JSON.parseObject(json);
        return basicResource.getDirectoryUsedData(params.get("host").toString());
    }

    @RequestMapping(value = "/getNetData", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Object getNetData(@RequestBody String json) {
        Map<String, Object> params = JSON.parseObject(json);
        return basicResource.getNetData(params.get("host").toString(), Integer.valueOf(params.get("minute").toString()));
    }

    @RequestMapping(value = "/getCpuData", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Object getCpuData(@RequestBody String json) {
        Map<String, Object> params = JSON.parseObject(json);
        return basicResource.getCpuData(params.get("host").toString(), Integer.valueOf(params.get("minute").toString()));
    }

    @RequestMapping(value = "/getMemoryData", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Object getMemoryData(@RequestBody String json) {
        Map<String, Object> params = JSON.parseObject(json);
        return basicResource.getMemoryData(params.get("host").toString(), Integer.valueOf(params.get("minute").toString()));
    }

    /**
     * 基础资源告警情况
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/getBaseEventData", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Object getBaseEventData(@RequestBody String json) {
        Map<String, Object> params = JSON.parseObject(json);
        return basicResource.getEventData(JSON.parseArray(params.get("listIp").toString()), Integer.valueOf(params.get("minute").toString()));
    }

    @RequestMapping(value = "/test")
    @ResponseBody

    public Object test() {
        return basicResource.getCpuData("10.30.16.220", 120);
    }

    @RequestMapping(value = "/test2")
    @ResponseBody
    public Object test2() {
        return basicResource.getMemoryDataSham();
    }
}
