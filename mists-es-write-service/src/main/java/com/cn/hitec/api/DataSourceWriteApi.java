package com.cn.hitec.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.bean.EsBean;
import com.cn.hitec.bean.SearchBean;
import com.cn.hitec.bean.UpdatebyIdBean;
import com.cn.hitec.controller.BaseController;
import com.cn.hitec.service.DataSourceService;
import com.cn.hitec.service.ESClientAdminService;
import com.cn.hitec.service.ESService;
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
import java.util.*;

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
@RequestMapping("/datasource")
public class DataSourceWriteApi extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(DataSourceWriteApi.class);
	@Autowired
	DataSourceService dataSourceService;


	/*
	  传入的JSON格式
	   {
			"aging_status": "迟到",
			"occur_time": 1522082042000,
			"should_time": "2018-03-27 00:43:00",
			"name": "国内精细化城镇预报",
			"type": "SEVP_NMC_RFFC_SCON_EME_ACHN_LNO_P9",
			"fields": {
				"data_time": "2018-03-27 00:18:00", //时次
				"file_name": "/home/datamgr/xts_gwyyj/Radar/MSP3_PMSC_RADAR_BREF_L88_CHN_201803270018_00000-00000.PNG", //文件名
				"ip_addr": "10.0.74.226", 	//IP地址
				"file_size": "19509",
				"event_status": "0",
				"data_type": "预报产品",	//数据类型
				"department_name": "气象中心",	//单位名称
				"phone": "5584",	// 电话
				"use_department": "全媒体气象产品室", 	//使用单位
				"system_name": "WXC"	//系统名称
			}
	   }
	 */


	/**
	 * 生成节目表
	 * @param json
	 * @return
	 */
	@RequestMapping(value = "/insertDataSource_DI", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> insertDataSource_DI(@RequestBody String json) {
		long start = 0;
		Map<String,Object> map ;
		List<Map> listData = JSON.parseArray(json,Map.class);
		System.out.println(JSON.toJSONString(listData));
		start = System.currentTimeMillis();
		map = new HashMap<>();
		for (Map dataMap : listData){

			try {
//				Map<String,Object> dataMap = JSON.parseObject(str);
				Map<String,Object> fieldsMap  = (Map<String, Object>) dataMap.get("fields");
				String str_index = Pub.Index_Head + Pub.transform_DateToString(
                        Pub.transform_StringToDate(fieldsMap.get("data_time").toString(),"yyyy-MM-dd HH:mm:ss"),
                        Pub.Index_Food_Simpledataformat);
				String str_type = "DATASOURCE";

				String sub_type = dataMap.get("type").toString();
				String dataTime = fieldsMap.get("data_time").toString();
				String module = fieldsMap.get("module").toString();
				String ipAddr = fieldsMap.get("ip_addr").toString();

				logger.info(str_type+","+sub_type+","+module+","+ipAddr+","+dataTime);

				String str_id = Pub.MD5(str_type+","+sub_type+","+module+","+ipAddr+","+dataTime);

				if (StringUtils.isEmpty(str_index)) {
                    SimpleDateFormat sdf = new SimpleDateFormat(Pub.Index_Food_Simpledataformat);
                    str_index = Pub.Index_Head + (sdf.format(new Date()));
                }
				System.out.println(str_index+","+str_type+","+str_id);
				dataSourceService.add(str_index, str_type, str_id , JSON.toJSONString(dataMap));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		long spend = System.currentTimeMillis() - start;
		outMap.put(KEY_RESULT, VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA, map);
		outMap.put(KEY_MESSAGE, "数据添加成功");
		outMap.put(KEY_SPEND, spend + "mm");
		return outMap;
	}



	/**
	 * 生成节目表
	 * @param json
	 * @return
	 */
	@RequestMapping(value = "/insertList", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> insertList(@RequestBody String json) {
		long start = 0;
		Map map = new HashMap<>();
		JSONObject job = JSON.parseObject(json);
		start = System.currentTimeMillis();
		int num = 0;
		if (job.containsKey("_index") && job.containsKey("_type") && job.containsKey("_data")){
			List<String> listData = JSON.parseArray(job.getString("_data"),String.class);

			num = dataSourceService.add(job.getString("_index"),job.getString("_type"),listData);
		}else{
			System.out.println("数据格式有误");
		}

		map.put("insert_num",num);
		long spend = System.currentTimeMillis() - start;
		outMap.put(KEY_RESULT, VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA, map);
		outMap.put(KEY_MESSAGE, "数据添加成功");
		outMap.put(KEY_SPEND, spend + "mm");
		return outMap;
	}



	/**
	 * 数据入库
	 * @param json
	 * @return
	 */
	@RequestMapping(value = "/insertDataSource", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> insertDataSource(@RequestBody String json) {

		List<Map> listData = JSON.parseArray(json,Map.class);
		long start = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<>();
		dataSourceService.update_dataSource(listData);
		// System.out.printf("Json数据 %s", esBean.getData().toString() + "\n");
		long spend = System.currentTimeMillis() - start;
		outMap.put(KEY_RESULT, VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA, map);
		outMap.put(KEY_MESSAGE, "数据添加成功");
		outMap.put(KEY_SPEND, spend + "mm");
		return outMap;
	}


	/**
	 * 生成节目表
	 * @param json
	 * {
			"_index":"hx_possible_needed_data",
			"_type":"POSSIBLE_NEEDED_DATA",
			"_id":"D120F2AC41817ED13338EFBC0A2C171B"
		}
	 * @return
	 */
	@RequestMapping(value = "/deletebyid", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> deleteByid(@RequestBody String json) {
		long start = 0;
		Map map = new HashMap<>();
		try {
			JSONObject job = JSON.parseObject(json);
			start = System.currentTimeMillis();
			int num = 0;
			if (job.containsKey("_index") && job.containsKey("_type") && job.containsKey("_id")){
                String strIndex = job.getString("_index");
                String strType = job.getString("_type");
                String strId = job.getString("_id");

                num = dataSourceService.deleteById(strIndex,strType,strId);
            }else{
                map.put("insert_num",-1);
                long spend = System.currentTimeMillis() - start;
                outMap.put(KEY_RESULT, VAL_ERROR);
                outMap.put(KEY_RESULTDATA, map);
                outMap.put(KEY_MESSAGE, "数据格式有误");
                outMap.put(KEY_SPEND, spend + "mm");
                return outMap;
            }

			map.put("insert_num",num);
			long spend = System.currentTimeMillis() - start;
			outMap.put(KEY_RESULT, VAL_SUCCESS);
			outMap.put(KEY_RESULTDATA, map);
			outMap.put(KEY_MESSAGE, "删除成功");
			outMap.put(KEY_SPEND, spend + "mm");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("insert_num",-1);
			long spend = System.currentTimeMillis() - start;
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, map);
			outMap.put(KEY_MESSAGE, e.getMessage());
			outMap.put(KEY_SPEND, spend + "mm");
		} finally {
			return outMap;
		}
	}

}
