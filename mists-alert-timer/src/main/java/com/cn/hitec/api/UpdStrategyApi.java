package com.cn.hitec.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.component.ConfigComponent;
import com.cn.hitec.service.ConfigService;
import com.cn.hitec.util.Pub;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @description: TODO(这里用一句话描述这个类的作用)
 * @author fukl
 * @since 2017年8月3日
 * @version
 *
 */
@RestController
@RequestMapping("/updStrategyApi")
public class UpdStrategyApi {
	private static final Logger logger = LoggerFactory.getLogger(UpdStrategyApi.class);
	@Autowired
	ConfigService configService;

	@RequestMapping(value = "/initMap", method = RequestMethod.POST, consumes = "application/json")
	public String updInitMap(@RequestBody String json) {
		try {
            logger.info("传递的参数是："+json);
			JSONObject job = JSON.parseObject(json);
			String serviceType = "";
			if (!job.containsKey("serviceType")){
				return "参数错误";
			}
            serviceType = job.getString("serviceType");
			//初始化 alertMap
			configService.initAlertMap();
			if (Pub.DI_ConfigMap.size() < 1 || StringUtils.isEmpty(serviceType)){
				logger.error("更新告警策略表失败！！");
				return "更新失败!";
			}

//			logger.info("DIMap_collect:{}",JSON.toJSON(Pub.DIMap_collect));
//			logger.info("DIMap_machining:{}",JSON.toJSON(Pub.DIMap_machining));
//			logger.info("DIMap_distribute:{}",JSON.toJSON(Pub.DIMap_distribute));
//			logger.info("DIMap_t639:{}",JSON.toJSON(Pub.DIMap_t639));
//			logger.info("DIMap_DS:{}",JSON.toJSON(Pub.DIMap_DS));
//
//			logger.info("DI_ConfigMap:{}",JSON.toJSON(Pub.DI_ConfigMap));

			Map collect_temp = new HashMap();
			for (String key : Pub.DIMap_collect.keySet()){
                if (key.contains(serviceType)){
                    collect_temp.put(key,Pub.DIMap_collect.get(key));
                }
			}
			logger.info(JSON.toJSONString(collect_temp));

			Map machining_temp = new HashMap();
            for (String key : Pub.DIMap_machining.keySet()){
                if (key.contains(serviceType)){
                    machining_temp.put(key,Pub.DIMap_machining.get(key));
                }
            }
            logger.info(JSON.toJSONString(machining_temp));

            Map distribute_temp = new HashMap();
            for (String key : Pub.DIMap_distribute.keySet()){
                if (key.contains(serviceType)){
                    distribute_temp.put(key,Pub.DIMap_distribute.get(key));
                }
            }
            logger.info(JSON.toJSONString(distribute_temp));

            Map t639_temp = new HashMap();
            for (String key : Pub.DIMap_t639.keySet()){
                if (key.contains(serviceType)){
                    t639_temp.put(key,Pub.DIMap_t639.get(key));
                }
            }
            logger.info(JSON.toJSONString(t639_temp));

            Map datasource_temp = new HashMap();
            for (String key : Pub.DIMap_DS.keySet()){
                if (key.contains(serviceType)){
                    datasource_temp.put(key,Pub.DIMap_DS.get(key));
                }
            }
            logger.info(JSON.toJSONString(datasource_temp));

            //更新全部数据
			if (collect_temp.size() > 0 ){
                configService.createAlertDI("采集", collect_temp,0,new Date());
            }
            if (machining_temp.size() > 0 ){
                configService.createAlertDI("加工", machining_temp,0,new Date());
            }
            if (distribute_temp.size() > 0 ){
                configService.createAlertDI("分发", distribute_temp,0,new Date());
            }
            if (t639_temp.size() > 0 ){
                configService.createT639DI("FZJC",t639_temp,5);
            }
            if (datasource_temp.size() > 0 ){
                configService.makeProjectTable(new Date(),0,datasource_temp,new Date());
            }

//			//更新全部数据
//			configService.createAlertDI("采集", Pub.DIMap_collect,0,new Date());
//			configService.createAlertDI("加工", Pub.DIMap_machining,0,new Date());
//			configService.createAlertDI("分发", Pub.DIMap_distribute,0,new Date());
//
//			configService.createT639DI("FZJC",Pub.DIMap_t639,5);
//			configService.makeProjectTable(new Date(),0,Pub.DIMap_DS,new Date());

			logger.info("更新策略成功");
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}

		return "OK";
	}

}
