package com.cn.hitec.feign.client;


import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_Exsit;
import com.cn.hitec.bean.EsQueryBean_web;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/** 
 * 
 * 
 * @description: TODO(这里用一句话描述这个类的作用) 
 * @author james
 * @since 2017年7月20日 下午2:41:56 
 * @version
 *
 */
@FeignClient("kafka-servers-center")
public interface MySqlService {

    @RequestMapping(value = "/sqlserver/getrelationdata", method = RequestMethod.GET)
    public List<Object> getRelationData();

    @RequestMapping(value = "/sqlserver/getselectsdata", method = RequestMethod.GET)
    public Map getSelectsData();

    @RequestMapping(value = "/sqlserver/getrelationdataview", method = RequestMethod.GET)
    public Map getRelationDataView();


    @RequestMapping(value = "/sqlserver/adddmrelation", method = RequestMethod.POST, consumes = "application/json")
    public JSONObject addDataModuleRelation(@RequestBody String json);


    /**
     *  获取 源数据
     * @return
     */
    @RequestMapping(value = "/sqlserver/getsourcedatainfo", method = RequestMethod.GET)
    public List getSourceDataInfo();

    /**
     * 添加 源数据
     * @param
     * {
     *     "name":"",
     *     "path":"",
     *     "fileName":"",
     *     "subName":"",
     *     "ipAddr":"",
     *     "topic":"",
     * }
     * @return
     */
    @RequestMapping(value = "/sqlserver/addsourcedata", method = RequestMethod.POST, consumes = "application/json")
    public JSONObject addSourceDataInfo(@RequestBody String json);

    @RequestMapping(value = "/sqlserver/delsourcedata", method = RequestMethod.POST, consumes = "application/json")
    public JSONObject delSourceDataInfo(@RequestBody String json);

    @RequestMapping(value = "/sqlserver/deldmrelation", method = RequestMethod.POST, consumes = "application/json")
    public JSONObject deletDataModuleRelation(@RequestBody String json);

}
