package com.cn.hitec.controller;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.tools.HttpPub;
import com.cn.hitec.tools.Pub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/show")
public class BigScreenShowController {

    @Autowired
    EsQueryService esQueryService;

    @RequestMapping("/")
    public String index() {

        return "bigScreenShow/bigScreenShow";
    }


    /**
     * @param str
     * @return
     */
    @RequestMapping(value = "/getfilesize", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public List<Object> getFileSizeCount(@RequestBody String str) {

        return esQueryService.getFileSizeCount(str);
    }


    /**
     * 根据utl 获取数据 / 防止页面直接调用引起的跨域问题
     *
     * @param url
     * @return
     */
    @RequestMapping(value = "/getoutherdata", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getOutherData(String url) {
        if (url.indexOf("http") == -1) {
            return null;
        }
        Map<String, Object> res = HttpPub.getData(url);
        return res;
    }


    /**
     * 获取到 中心加工数据 所有数据
     * 获取所有数据后，简化、合并数据，返回给页面内
     *
     * @return
     */
    @RequestMapping(value = "/lctdata", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getLCTData() {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> map = esQueryService.getLCTData();

        for (String strKey : map.keySet()) {
            Map<String, Object> tempMap = (Map<String, Object>) map.get(strKey);
            Map<String,Object> tempMap_result = new HashMap<>();
            Pub.mapCopy(tempMap,tempMap_result);
            //转换OCF数据
            if (strKey.equals("OCF")) {

                Map<String, Object> temp_fenfa = (Map<String, Object>) tempMap.get("fenfa");
                Map<String, Object> temp_fenfa_new = new HashMap<>();

                //判断合并后的状态
                boolean h1 = false;
                boolean h3 = false;
                boolean h12 = false;
                boolean ocfFinal = false;
                //开始合并
                for (String fenfaKey : temp_fenfa.keySet()) {
                    Map<String, Object> temp_type = (Map<String, Object>) temp_fenfa.get(fenfaKey);
                    Map<String, Object> temp_type_new = new HashMap<>();
                    Pub.mapCopy(temp_type, temp_type_new);
                    if ("CH_MERGE_1H".equals(fenfaKey) || "AGLB_MERGE_1H".equals(fenfaKey)
                            || "AGLB_1H".equals(fenfaKey) || "CH_1H".equals(fenfaKey) || "OCF1H_ME_L88_GLB".equals(fenfaKey)) {
                        //先判断如果是第一次循环，给状态变为正常
                        if (!temp_fenfa_new.containsKey("1H")) {
                            temp_fenfa_new.put("1H", temp_type_new);
                            h1 = true;
                        }
                        if (!h1) {        //如果状态是异常,跳过此次循环
                            continue;
                        }
                        //判断状态是否正常，
                        String state = temp_type.get("aging_status").toString();
                        if (!"正常".equals(state)) {
                            h1 = false;
                            //存入一条 错误数据   删除其他数据
                            temp_fenfa_new.put("1H", temp_type_new);
                        }
                    } else if ("CH_MERGE_3H".equals(fenfaKey) || "AGLB_MERGE_3H".equals(fenfaKey) || "AGLB_3H".equals(fenfaKey)
                            || "CH_3H".equals(fenfaKey) || "OCF3H_ME_L88_GLB".equals(fenfaKey)) {
                        if (!temp_fenfa_new.containsKey("3H")) {
                            temp_fenfa_new.put("3H", temp_type_new);
                            h3 = true;
                        }
                        if (!h3) {
                            continue;
                        }
                        //判断状态是否正常，
                        String state = temp_type.get("aging_status").toString();
                        if (!"正常".equals(state)) {
                            h3 = false;
                            temp_fenfa_new.put("3H", temp_type_new);

                        }
                    } else if ("CH_MERGE_12H".equals(fenfaKey) || "AGLB_MERGE_12H".equals(fenfaKey) || "AGLB_12H".equals(fenfaKey)
                            || "CH_12H".equals(fenfaKey) || "OCF12H_ME_L88_GLB".equals(fenfaKey)) {
                        //先判断如果是第一次循环，给状态变为正常
                        if (!temp_fenfa_new.containsKey("12H")) {
                            temp_fenfa_new.put("12H", temp_type_new);
                            h12 = true;
                        }
                        if (!h12) {        //如果状态是异常,跳过此次循环
                            continue;
                        }
                        //判断状态是否正常，
                        String state = temp_type.get("aging_status").toString();
                        if (!"正常".equals(state)) {
                            h12 = false;
                            //存入一条 错误数据   删除其他数据
                            temp_fenfa_new.put("12H", temp_type_new);
                        }
                    } else if ("OCF_FINAL".equals(fenfaKey)) {
                        //先判断如果是第一次循环，给状态变为正常
                        if (!temp_fenfa_new.containsKey("FINAL")) {
                            temp_fenfa_new.put("FINAL", temp_type_new);
                            ocfFinal = true;
                        }
                        if (!ocfFinal) {        //如果状态是异常,跳过此次循环
                            continue;
                        }
                        //判断状态是否正常，
                        String state = temp_type.get("aging_status").toString();
                        if (!"正常".equals(state)) {
                            ocfFinal = false;
                            //存入一条 错误数据   删除其他数据
                            temp_fenfa_new.put("FINAL", temp_type_new);

                        }
                    }
                }

                tempMap_result.put("fenfa",temp_fenfa_new);
                resultMap.put(strKey,tempMap_result);
                continue;
            }

            //转换其他数据
            resultMap.put(strKey,tempMap);
        }
        return resultMap;
    }


}
