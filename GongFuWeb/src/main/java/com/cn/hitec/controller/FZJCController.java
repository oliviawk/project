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
@RequestMapping("/fzjc")
public class FZJCController {

    @Autowired
    private FZJCService fzjcService;
    @Autowired
    private BasicResource basicResource;

    @RequestMapping("/sxt")
    public String index() {
        // System.out.println("进入MICAPS4.html");
        return "FuZhuJueCe/fuzhujuece";
    }

    @RequestMapping("/lct")
    public String lct() {
        // System.out.println("进入MICAPS4.html");
        return "FuZhuJueCe/produce";
    }


    @RequestMapping(value = "/findtemp", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String findTemp(@RequestBody EsQueryBean_web esQueryBean) {
        Map<String, Object> map = fzjcService.findData_FC(esQueryBean);
        return JSON.toJSONString(map);
    }

    @RequestMapping(value = "/findalert", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String findAlert(@RequestBody EsQueryBean_web esQueryBean) {
        Map<String, Object> map = fzjcService.findAlertData(esQueryBean);
        return JSON.toJSONString(map);
    }


    /**
     * 云图、雷达 ， 各环节查询方法
     *
     * @param esQueryBean
     * @return
     */
    @RequestMapping(value = "/findData_DI", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Map findData_DI(@RequestBody EsQueryBean_web esQueryBean) {
        Map<String, Object> map = fzjcService.findData_DI(esQueryBean);
//        JSON.toJSONStringWithDateFormat(map,"yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);     //fastjson 插件异常处理方法
        return map;
    }

    /**
     * 云图、雷达 ， 各环节 状态 查询方法
     *
     * @param esQueryBean
     * @return
     */
    @RequestMapping(value = "/findDataNew", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Map findDataNew(@RequestBody EsQueryBean_web esQueryBean) {
        Map<String, Object> map = fzjcService.findDataNew(esQueryBean);
//        JSON.toJSONStringWithDateFormat(map,"yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);     //fastjson 插件异常处理方法
        return map;
    }

    @RequestMapping(value = "/lctAggQuery", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Map lctAggQuery() {
        Map<String, Object> map = fzjcService.lctAggQuery();
//        JSON.toJSONStringWithDateFormat(map,"yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);     //fastjson 插件异常处理方法
        return map;
    }

    /**
     * 云图、雷达 ， 历史数据查询
     *
     * @param esQueryBean
     * @return
     */
    @RequestMapping(value = "/findData_DI_history", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Map findData_DI_history(@RequestBody EsQueryBean_web esQueryBean) {
        Map<String, Object> map = fzjcService.findData_DI_history(esQueryBean);
        return map;
    }

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
     * FZJC基础资源告警情况
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
