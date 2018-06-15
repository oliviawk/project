package com.cn.hitec.controller;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.EsQueryBean_web;
import com.cn.hitec.service.BasicResource;
import com.cn.hitec.service.LAPSService;
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
     *
     * @param esQueryBean
     * @return
     */
    @RequestMapping(value = "/getData", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Map getData(@RequestBody EsQueryBean_web esQueryBean) {
        Map<String, Object> map = lapsService.getData(esQueryBean);
        return map;
    }


    /**
     * 聚合查询laps流程图各环节状态
     *
     * @return
     */
    @RequestMapping(value = "/lctAggQuery", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Map lctAggQuery() {
        Map<String, Object> map = lapsService.lctAggQuery();
//        JSON.toJSONStringWithDateFormat(map,"yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);     //fastjson 插件异常处理方法
        return map;
    }

    /**
     * LAPS历史数据查询
     *
     * @param esQueryBean
     * @return
     */
    @RequestMapping(value = "/getHistory", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Map getHistory(@RequestBody EsQueryBean_web esQueryBean) {
        Map<String, Object> map = lapsService.getHistory(esQueryBean);
        return map;
    }


    /**
     * LAPS基础资源目录使用情况
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/getDirectoryUsedData", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Object getDirectoryUsedData(@RequestBody String json) {
        Map<String, Object> params = JSON.parseObject(json);
        return basicResource.getDirectoryUsedData(params.get("host").toString());
    }

    /**
     * LAPS基础资源网络使用情况
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/getNetData", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Object getNetData(@RequestBody String json) {
        Map<String, Object> params = JSON.parseObject(json);
        return basicResource.getNetData(params.get("host").toString(), Integer.valueOf(params.get("minute").toString()));
    }

    /**
     * LAPS基础资源CPU使用情况
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/getCpuData", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Object getCpuData(@RequestBody String json) {
        Map<String, Object> params = JSON.parseObject(json);
        return basicResource.getCpuData(params.get("host").toString(), Integer.valueOf(params.get("minute").toString()));
    }

    /**
     * LAPS基础资源内存使用情况
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/getMemoryData", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Object getMemoryData(@RequestBody String json) {
        Map<String, Object> params = JSON.parseObject(json);
        return basicResource.getMemoryData(params.get("host").toString(), Integer.valueOf(params.get("minute").toString()));
    }

    /**
     * LAPS基础资源告警情况
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

}
