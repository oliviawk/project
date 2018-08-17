package com.cn.hitec.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.domain.DataInfo;
import com.cn.hitec.service.ConfigService;
import com.cn.hitec.tools.Pub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
@RequestMapping("/updStrategyApiWrite")
public class UpdStrategyApi {
	private static final Logger logger = LoggerFactory.getLogger(UpdStrategyApi.class);
	@Autowired
    ConfigService configService;

	@RequestMapping(value = "/initMap", method = RequestMethod.POST, consumes = "application/json")
	public String updInitMap() {
		try {
			//初始化 alertMap
			configService.initAlertMap();
			if (Pub.DI_ConfigMap.size() < 1){
				logger.error("更新告警策略表失败！！");
				return "更新失败!";
			}
			logger.info("更新策略成功");
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}

		return "OK";
	}

	@RequestMapping(value = "/DelectPrepareData", method = RequestMethod.POST, consumes = "application/json")
	public void DeletePrepareData(@RequestBody String json){
		logger.info("传递的参数是："+json);
		JSONObject job = JSON.parseObject(json);
		String serviceType = "";
		if (!job.containsKey("serviceType")){
			logger.info("数据错误datainfo 为空");
		}
		String  datainfostr=job.getString("list");
		List<DataInfo> list = (List<DataInfo>) JSON.parse(datainfostr);
        long  num=configService.deletepreparedata(list);
		logger.info("删除预生成数据条数"+num);

	}

}
