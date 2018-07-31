package com.cn.hitec.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.bean.EsBean;
import com.cn.hitec.controller.BaseController;
import com.cn.hitec.service.DataSourceService;
import com.cn.hitec.service.MQPFService;
import com.cn.hitec.tools.Pub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
@RequestMapping("/mqpfsource")
public class MQPFWriteApi extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(MQPFWriteApi.class);
	@Autowired
	MQPFService mqpfService;


	/*
	  传入的JSON格式
	   {
			"fields": {
				"data_time": "2018-07-10 07:56:07.000+0800",
				"end_time": "2018-07-10 15:57:00.000+0800",
				"event_status": "0",
				"file_name": "/radar-base/bz2/Z_RADR_I_Z9476_20180710075607_O_DOR_CD_CAP.bin.bz2",
				"file_size": 104599,
				"ip_addr": "10.30.16.111",
				"module": "采集",
				"start_time": "2018-07-10 15:57:00.000+0800"
			},
			"name": "风流场采集",
			"occur_time": 1531209420000,
			"type": "T639"
	   }
	 */

	@RequestMapping(value = "/insert", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> insertData(@RequestBody EsBean esBean) {
		if (esBean == null || esBean.getData() == null || esBean.getData().size() <= 0) {
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, null);
			outMap.put(KEY_MESSAGE, "mqpf数据添加失败！数据为 null");
			return outMap;
		}
		long start = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<>();
		// System.out.printf("Json数据 %s",esBean.getData().toString() +"\n");

		int num = mqpfService.insertMQPFCollectionData(esBean.getData());
		if (num == 0 || esBean.getData().size() > num) {
			outMap.put(KEY_MESSAGE, "数据修改失败");
		} else {
			outMap.put(KEY_MESSAGE, "数据修改成功");
		}
		map.put("update_number", num);
		long spend = System.currentTimeMillis() - start;
		outMap.put(KEY_RESULT, VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA, map);

		outMap.put(KEY_SPEND, spend + "mm");
		return outMap;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> update(@RequestBody EsBean esBean) {
		if (esBean == null || esBean.getData() == null || esBean.getData().size() <= 0) {
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, null);
			outMap.put(KEY_MESSAGE, "MQPF修改数据失败！数据为 null");
			return outMap;
		}
		long start = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<>();

		int num = mqpfService.insertMQPFCollectionData_220(esBean.getType(), esBean.getData());
		if (num == 0 || esBean.getData().size() > num) {
			outMap.put(KEY_MESSAGE, "数据修改失败");
		} else {
			outMap.put(KEY_MESSAGE, "数据修改成功");
		}
		map.put("update_number", num);
		long spend = System.currentTimeMillis() - start;
		outMap.put(KEY_RESULT, VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA, map);

		outMap.put(KEY_SPEND, spend + "mm");
		return outMap;
	}


}
