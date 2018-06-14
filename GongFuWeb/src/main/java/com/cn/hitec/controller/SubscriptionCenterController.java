package com.cn.hitec.controller;

import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.bean.DataModuleRelation;
import com.cn.hitec.feign.client.MySqlService;
import com.cn.hitec.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 订阅中心
 */
@Controller
@RequestMapping("/subscription")
public class SubscriptionCenterController {
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionCenterController.class);
    @Autowired
    MySqlService mySqlService;

    @RequestMapping("/index")
    public String index() {
        // System.out.println("进入MICAPS4.html");
        return "SubscriptionCenter/produce";
    }

    @RequestMapping("/viewconfig")
    public String serverConfig() {
        // System.out.println("进入MICAPS4.html");
        return "SubscriptionCenter/relationView";
    }

    @RequestMapping("/sourcedata")
    public String dataConfig() {
        // System.out.println("进入MICAPS4.html");
        return "SubscriptionCenter/sourcedata";
    }

    /**
     * 获取 数据、环节 关系数据
     * @return
     */
    @RequestMapping(value="/getdatamodulerelation", method = RequestMethod.GET)
    @ResponseBody
    public List getDataModuleRelation(){

        return mySqlService.getRelationData();
    }

    /**
     * 获取源数据 数据、环节 关系数据
     * @return
     */
    @RequestMapping(value="/getsourcedata", method = RequestMethod.GET)
    @ResponseBody
    public List getsourceData(){
        System.out.println(1111111111);
        ArrayList ls= (ArrayList) mySqlService.getSourceDataInfo();
        System.out.println(ls.get(1));
        return mySqlService.getSourceDataInfo();
    }



    /**
     * 获取 数据、环节 关系图数据
     * @return
     */
    @RequestMapping(value="/getdatamodulerelationView", method = RequestMethod.GET)
    @ResponseBody
    public Map getDataModuleRelationView(){
        return mySqlService.getRelationDataView();
    }

    /**
     * 添加 数据、环节 关系数据
     * @return
     */
    @RequestMapping(value="/adddatamodulerelation", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public JSONObject addDataModuleRelation(@RequestBody String strJson){
        logger.info(strJson);
        JSONObject jsonObject = new JSONObject();
        if(StringUtils.isEmpty(strJson)){
            jsonObject.put("result","操作失败!");
            return jsonObject;
        }
        try {

            jsonObject = mySqlService.addDataModuleRelation(strJson);

        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("result","操作失败!");
        } finally {
            return jsonObject;
        }
    }

    @RequestMapping(value="/deletdatamodulerelation", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public JSONObject deletDataModuleRelation(@RequestBody String id){
        mySqlService.deletDataModuleRelation(id);
        return null;
    }

    @RequestMapping(value="/addsourcedata", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public List addSourceData(@RequestBody String strJson){
        System.out.println(2222);
        mySqlService.addSourceDataInfo(strJson);
        return mySqlService.getSourceDataInfo();
    }

    @RequestMapping(value="/deletsourcedata", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public List deletSourceData(@RequestBody String id){
        System.out.println(id);
        System.out.println(mySqlService.delSourceDataInfo(id));
        return null;
    }
    /**
     * 添加 源数据
     * @return
     */
    @RequestMapping(value="/addmoduleinfo", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String addModuleInfo(@RequestBody String strJson){
        logger.info(strJson);
        return "操作成功";
    }

    /**
     * 添加 环节数据
     * @return
     */
    @RequestMapping(value="/addsourcedatainfo", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String addSourceDataInfo(@RequestBody String strJson){
        logger.info(strJson);
        return "操作成功";
    }


    @RequestMapping(value="/getselectsdata", method = RequestMethod.GET)
    @ResponseBody
    public Map getSelectsData(){
        return mySqlService.getSelectsData();
    }


}
