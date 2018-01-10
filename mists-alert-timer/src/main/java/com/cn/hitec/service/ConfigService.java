package com.cn.hitec.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.DataBean;
import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_Exsit;
import com.cn.hitec.bean.EsWriteBean;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.util.CronPub;
import com.cn.hitec.util.Pub;

@Service
public class ConfigService {
	private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);

	@Autowired
	EsQueryService esQueryService;
	@Autowired
	EsWriteService esWriteService;

	/**
	 * 初始化 采集 DI信息表
	 * 
	 * @return
	 * @throws Exception
	 */
	public int initAlertConfigData_collect() throws Exception {

		List<String> jsonList = new ArrayList<>();
		/*-----------------采集---------------------------*/
		jsonList.add(
				"{\"DI_name\":\"雷达\",\"time_interval\":\"0 0/6 * * * ? *\",\"should_time\":\"1200\",\"last_time\":\"1800\",\"data_type\":\"气象基本资料\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.220\",\"path\":\"/mnt/nmic2017/radar/latlon/\",\"file_name\":\"ACHN.QREF000.yyyymmdd.xxxxxx.latlon\",\"transfer_type\":\"ftp推送\",\"module\":\"采集\",\"serviceType\":\"FZJC\"}");
		jsonList.add(
				"{\"DI_name\":\"云图\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":3000,\"last_time\":3540,\"data_type\":\"气象基本资料\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.220\",\"path\":\"/home/data/satellite/HDF/\",\"file_name\":\"SEVP_NSMC_WXGN_FY2G_E99_ACHN_LNO_P9_*.HDF\",\"transfer_type\":\"ftp推送\",\"module\":\"采集\",\"serviceType\":\"FZJC\"}");
		jsonList.add(
				"{\"DI_name\":\"CIMISS\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":600,\"last_time\":900,\"data_type\":\"自动站\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.242\",\"path\":\"/home/laps/data/rawdata/aws/\",\"file_name\":\"\",\"transfer_type\":\"ftp推送\",\"module\":\"采集\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"T639\",\"time_interval\":\"0 0 2,14 * * ? *\",\"should_time\":600,\"last_time\":900,\"data_type\":\"自动站\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.242\",\"path\":\"/mnt/laps_nfs/laps_3x3/t639/\",\"file_name\":\"\",\"transfer_type\":\"ftp推送\",\"module\":\"采集\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"LSX\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":1500,\"last_time\":2400,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.242\",\"path\":\"/home/laps/laps_data/lapsprd/lsx/\",\"file_name\":\"\",\"transfer_type\":\"\",\"module\":\"采集\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"L1S\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":1500,\"last_time\":2400,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.242\",\"path\":\"/home/laps/laps_data/lapsprd/l1s/\",\"file_name\":\"\",\"transfer_type\":\"\",\"module\":\"采集\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"GR2\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":1500,\"last_time\":2400,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.242\",\"path\":\"/home/laps/laps_data/lapsprd/lgr2/\",\"file_name\":\"\",\"transfer_type\":\"\",\"module\":\"采集\",\"serviceType\":\"LAPS\"}");

		EsWriteBean esWriteBean = new EsWriteBean();
		esWriteBean.setIndex("config");
		// esWriteBean.setType("collect");
		esWriteBean.setData(jsonList);
		Map<String, Object> map = esWriteService.insert1(esWriteBean);
		Map<String, Object> resultMap = (Map<String, Object>) map.get("resultData");
		int n = Integer.valueOf(resultMap.get("insert_number").toString());
		return n;
	}

	/**
	 * 初始化 加工 DI信息表
	 * 
	 * @return
	 * @throws Exception
	 */
	public int initAlertConfigData_machining() throws Exception {

		List<String> jsonList = new ArrayList<>();
		/*-----------------加工---------------------------*/
		jsonList.add(
				"{\"DI_name\":\"ReadFY2NC\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":3600,\"last_time\":4200,\"data_type\":\"气象基本资料\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.223\",\"path\":\"\",\"file_name\":\"SEVP_NSMC_WXGN_FY2G_E99_ACHN_LNO_P9_*.HDF\",\"transfer_type\":\"ftp推送\",\"module\":\"加工\",\"serviceType\":\"FZJC\"}");
		jsonList.add(
				"{\"DI_name\":\"风流场\",\"time_interval\":\"0 0 2/3 * * ? *\",\"should_time\":0,\"last_time\":0,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.223\",\"path\":\"Z:\\\\NoGeography\\\\forecast\\\\t639\\\\\",\"file_name\":\"T639_GMFS_WIND_2017102508.json\",\"transfer_type\":\"\",\"module\":\"加工\",\"serviceType\":\"FZJC\"}");
		jsonList.add(
				"{\"DI_name\":\"炎热指数\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":3360,\"last_time\":3900,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.223\",\"path\":\"Z://NoGeography//live//hotIndex//\",\"file_name\":\"hot*.txt\",\"transfer_type\":\"\",\"module\":\"加工\",\"serviceType\":\"FZJC\"}");
		jsonList.add(
				"{\"DI_name\":\"LapsRain1Hour\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":2700,\"last_time\":3600,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.224\",\"path\":\"\",\"file_name\":\"\",\"transfer_type\":\"\",\"module\":\"加工\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"LapsWSWD\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":2100,\"last_time\":3600,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.224\",\"path\":\"\",\"file_name\":\"\",\"transfer_type\":\"\",\"module\":\"加工\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"LapsTRH\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":2400,\"last_time\":3600,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.224\",\"path\":\"\",\"file_name\":\"\",\"transfer_type\":\"\",\"module\":\"加工\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"LapsTD\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":2700,\"last_time\":3600,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.224\",\"path\":\"\",\"file_name\":\"\",\"transfer_type\":\"\",\"module\":\"加工\",\"serviceType\":\"LAPS\"}");

		EsWriteBean esWriteBean = new EsWriteBean();
		esWriteBean.setIndex("config");
		// esWriteBean.setType("machining");
		esWriteBean.setData(jsonList);
		Map<String, Object> map = esWriteService.insert1(esWriteBean);
		Map<String, Object> resultMap = (Map<String, Object>) map.get("resultData");
		int n = Integer.valueOf(resultMap.get("insert_number").toString());
		return n;
	}

	/**
	 * 初始化 分发 DI信息表
	 * 
	 * @return
	 * @throws Exception
	 */
	public int initAlertConfigData_distribute() throws Exception {

		List<String> jsonList = new ArrayList<>();
		/*-----------------分发---------------------------*/
		jsonList.add(
				"{\"DI_name\":\"云图\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":3600,\"last_time\":4800,\"data_type\":\"气象基本资料\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.0.74.226\",\"path\":\"/home/datamgr/xts_gwyyj/cloudmap/\",\"file_name\":\"SEVP_NSMC_WXGN_FY2G_E99_ACHN_LNO_P9_*.HDF\",\"transfer_type\":\"ftp推送\",\"module\":\"分发\",\"serviceType\":\"FZJC\"}");
		jsonList.add(
				"{\"DI_name\":\"雷达\",\"time_interval\":\"0 0/6 * * * ? *\",\"should_time\":\"1500\",\"last_time\":\"2400\",\"data_type\":\"气象基本资料\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.0.74.226\",\"path\":\"/home/datamgr/xts_gwyyj/Radar/\",\"file_name\":\"MSP3_PMSC_RADAR_BREF_L88_CHN_*.png\",\"transfer_type\":\"ftp推送\",\"module\":\"分发\",\"serviceType\":\"FZJC\"}");
		jsonList.add(
				"{\"DI_name\":\"T639\",\"time_interval\":\"0 0 2/3 * * ? *\",\"should_time\":0,\"last_time\":0,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.0.74.226\",\"path\":\"/home/datamgr/xts_gwyyj/T639/\",\"file_name\":\"T639_GMFS_WIND_*.json\",\"transfer_type\":\"\",\"module\":\"分发\",\"serviceType\":\"FZJC\"}");
		jsonList.add(
				"{\"DI_name\":\"炎热指数\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":3960,\"last_time\":4560,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.0.74.226\",\"path\":\"/home/datamgr/xts_gwyyj/hotIndex/\",\"file_name\":\"hot*.txt\",\"transfer_type\":\"\",\"module\":\"分发\",\"serviceType\":\"FZJC\"}");
		jsonList.add(
				"{\"DI_name\":\"LAPS3KMGEO_PRCPV\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":3000,\"last_time\":4200,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.0.74.226\",\"path\":\"/home/datamgr/laps/jpg/\",\"file_name\":\"\",\"transfer_type\":\"ftp推送\",\"module\":\"分发\",\"serviceType\":\"LAPS\"}");
		// 10.0.74.226服务器的LAP分发
		jsonList.add(
				"{\"DI_name\":\"LAPS3KMGEO_EU4\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":2400,\"last_time\":4200,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.0.74.226\",\"path\":\"/home/datamgr/laps/jpg/\",\"file_name\":\"\",\"transfer_type\":\"ftp推送\",\"module\":\"分发\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"LAPS3KMGEO_TD\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":3300,\"last_time\":4200,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.0.74.226\",\"path\":\"/home/datamgr/laps/jpg/\",\"file_name\":\"\",\"transfer_type\":\"ftp推送\",\"module\":\"分发\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"LAPS3KMGEO_T\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":2700,\"last_time\":4200,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.0.74.226\",\"path\":\"/home/datamgr/laps/jpg/\",\"file_name\":\"\",\"transfer_type\":\"ftp推送\",\"module\":\"分发\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"LAPS3KMGEO_RH\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":2700,\"last_time\":4200,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.0.74.226\",\"path\":\"/home/datamgr/laps/jpg/\",\"file_name\":\"\",\"transfer_type\":\"ftp推送\",\"module\":\"分发\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"LAPS3KM_ME\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":2400,\"last_time\":3600,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.0.74.226\",\"path\":\"/home/datamgr/cvs_new/laps/gr2/\",\"file_name\":\"\",\"transfer_type\":\"ftp推送\",\"module\":\"分发\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"LAPS3KMGEO_PRCPV\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":3000,\"last_time\":4200,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.220\",\"path\":\"/home/datamgr/laps/jpg/\",\"file_name\":\"\",\"transfer_type\":\"ftp推送\",\"module\":\"分发\",\"serviceType\":\"LAPS\"}");
		// 10.30.16.220服务器的LAP分发
		jsonList.add(
				"{\"DI_name\":\"LAPS3KMGEO_EU4\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":2400,\"last_time\":4200,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.220\",\"path\":\"/home/datamgr/laps/jpg/\",\"file_name\":\"\",\"transfer_type\":\"ftp推送\",\"module\":\"分发\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"LAPS3KMGEO_TD\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":3300,\"last_time\":4200,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.220\",\"path\":\"/home/datamgr/laps/jpg/\",\"file_name\":\"\",\"transfer_type\":\"ftp推送\",\"module\":\"分发\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"LAPS3KMGEO_T\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":2700,\"last_time\":4200,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.220\",\"path\":\"/home/datamgr/laps/jpg/\",\"file_name\":\"\",\"transfer_type\":\"ftp推送\",\"module\":\"分发\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"LAPS3KMGEO_RH\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":2700,\"last_time\":4200,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.220\",\"path\":\"/home/datamgr/laps/jpg/\",\"file_name\":\"\",\"transfer_type\":\"ftp推送\",\"module\":\"分发\",\"serviceType\":\"LAPS\"}");
		jsonList.add(
				"{\"DI_name\":\"LAPS3KM_ME\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":2400,\"last_time\":3600,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.220\",\"path\":\"/home/datamgr/cvs_new/laps/gr2/\",\"file_name\":\"\",\"transfer_type\":\"ftp推送\",\"module\":\"分发\",\"serviceType\":\"LAPS\"}");

		EsWriteBean esWriteBean = new EsWriteBean();
		esWriteBean.setIndex("config");
		// esWriteBean.setType("distribute");
		esWriteBean.setData(jsonList);
		Map<String, Object> map = esWriteService.insert1(esWriteBean);
		Map<String, Object> resultMap = (Map<String, Object>) map.get("resultData");
		int n = Integer.valueOf(resultMap.get("insert_number").toString());
		return n;
	}

	/**
	 * 查询
	 * 
	 * @return
	 */
	public List<Map> getConfigAlert() {
		List<Map> resultList = null;

		try {
			EsQueryBean esQueryBean = new EsQueryBean();
			esQueryBean.setIndices(new String[] { "config" });

			Map<String, Object> resultMap = esQueryService.getAlertData(esQueryBean);
			if (resultMap == null) {
				logger.warn("getConfigAlert is null");
				return null;
			} else {
				if (resultMap.get("result").equals("success")) {
					resultList = (List<Map>) resultMap.get("resultData");
				} else {
					logger.warn(resultMap.get("message").toString());
					return null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			resultList = null;
		} finally {
			return resultList;
		}
	}

	public void initAlertMap() {
		List<Map> listMap_Config = getConfigAlert();
		if (listMap_Config != null) {
			for (Map map : listMap_Config) {
				if (map.containsKey("DI_name")) {
					String DI_name = map.get("DI_name").toString();
					String IP = map.get("IP").toString();
					String serviceType = map.get("serviceType").toString();
					String module = map.get("module").toString();
					if ("T639".equals(DI_name) || "风流场".equals(DI_name)) {
						Pub.DIMap_t639.put(DI_name + "," + IP + "," + serviceType + "," + module, map);
					} else if ("采集".equals(module)) {
						Pub.DIMap_collect.put(DI_name + "," + IP + "," + serviceType + "," + module, map);
					} else if ("加工".equals(module)) {
						Pub.DIMap_machining.put(DI_name + "," + IP + "," + serviceType + "," + module, map);
					} else if ("分发".equals(module)) {
						Pub.DIMap_distribute.put(DI_name + "," + IP + "," + serviceType + "," + module, map);
					}
				}
			}
		}

		/*---------------------采集------------------------*/
		/*
		 * List<Map> listMap_collect = getConfigAlert("collect"); for (Map map :
		 * listMap_collect) { String DI_name = map.get("DI_name").toString(); if
		 * ("T639".equals(DI_name) || "风流场".equals(DI_name)) {
		 * Pub.DIMap_t639.put(DI_name, map); } else {
		 * Pub.DIMap_collect.put(DI_name, map); }
		 * 
		 * } List<Map> listMap_machining = getConfigAlert("machining"); for (Map
		 * map : listMap_machining) { String DI_name =
		 * map.get("DI_name").toString(); if ("T639".equals(DI_name) ||
		 * "风流场".equals(DI_name)) { Pub.DIMap_t639.put(DI_name, map); } else {
		 * Pub.DIMap_machining.put(DI_name, map); }
		 * 
		 * } List<Map> listMap_distribute = getConfigAlert("distribute"); for
		 * (Map map : listMap_distribute) { String DI_name =
		 * map.get("DI_name").toString(); if ("T639".equals(DI_name) ||
		 * "风流场".equals(DI_name)) { Pub.DIMap_t639.put(DI_name, map); } else {
		 * Pub.DIMap_distribute.put(DI_name, map); }
		 * 
		 * }
		 */
	}

	/**
	 * 生成 ID 数据表（节目表）
	 * 
	 * @param type
	 * @param module
	 */
	public void createAlertDI(String module, Map<String, Object> DIMap) {

		if (DIMap == null || DIMap.size() < 1) {
			logger.warn("createAlertItem is fail ： alertMap is null or 0 in length");
			return;
		}
		try {

			// 生成日历插件， 计算出 第二天的开始时间和结束时间
			Calendar calendar = Calendar.getInstance();
			Date date = new Date();
			calendar.setTime(date);

			calendar.add(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			Date startDate = calendar.getTime();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			Date endDate = calendar.getTime();

			String strIndex = Pub.Index_Head + Pub.transform_DateToString(startDate, Pub.Index_Food_Simpledataformat);
			Map<String, Object> map = null;
			// 循环 告警配置信息
			for (String key : DIMap.keySet()) {
				map = (Map<String, Object>) DIMap.get(key); // 获取单条配置信息

				try {
					String cron = map.get("time_interval").toString();
					List<Date> timeList = CronPub.getTimeBycron_Date(cron, startDate, endDate);
					List<String> listDataBean = new ArrayList<>();
					String serviceType = map.get("serviceType").toString();
					String subType = map.get("DI_name").toString();
					// if(!"炎热指数".equals(subType)){
					// continue;
					// }
					String name = "";
					String IP = map.get("IP").toString();
					String path = map.get("path").toString();
					for (Date dt : timeList) {
						DataBean dataBean = new DataBean();
						dataBean.setName(name);
						dataBean.setServiceType(serviceType);
						dataBean.setType(subType);
						// 这里需要封装一个方法，根据不同的 数据源、时次，生成不同的应到时间和最晚时间
						int cron_shouldTime = Integer.valueOf(map.get("should_time").toString());
						int cron_lastTime = Integer.valueOf(map.get("last_time").toString());
						dataBean.setShould_time(Pub.transform_longDataToString(dt.getTime() / 1000 + cron_shouldTime,
								"yyyy-MM-dd HH:mm:ss.SSSZ"));
						dataBean.setLast_time(Pub.transform_longDataToString(dt.getTime() / 1000 + cron_lastTime,
								"yyyy-MM-dd HH:mm:ss.SSSZ"));

						dataBean.setAging_status("未处理");

						Map<String, Object> fields = new HashMap<>();
						fields.put("module", module);

						// 对资料的时区不是北京时的数据进行加减处理
						if (("LAPS_CIMISS".equals(subType) && "采集".equals(module))
								|| ("LAPS_LSX".equals(subType) && "采集".equals(module))
								|| ("LAPS_L1S".equals(subType) && "采集".equals(module))
								|| ("LAPS_GR2".equals(subType) && "采集".equals(module))) {
							fields.put("data_time", Pub.transform_DateToString(setWorldTime(dt, -8),
									"yyyy-MM-dd HH:mm:ss.SSS" + "+0000"));
						} else if ("LAPS_T639".equals(subType) && "采集".equals(module)) {
							fields.put("data_time",
									Pub.transform_DateToString(setWorldTime(dt, -14), "yyyy-MM-dd HH:mm:ss.SSS")
											+ "+0000");
						} else {
							fields.put("data_time", Pub.transform_DateToString(dt, "yyyy-MM-dd HH:mm:ss.SSSZ"));
						}

						fields.put("ip_addr", IP);
						fields.put("file_name", path);
						dataBean.setFields(fields);

						// logger.info(JSON.toJSONString(dataBean));
						listDataBean.add(JSON.toJSONString(dataBean));
					}
					System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					// 分批次录入数据
					EsWriteBean esWriteBean = new EsWriteBean();
					esWriteBean.setIndex(strIndex);
					// esWriteBean.setType(type);
					esWriteBean.setData(listDataBean);
					Map<String, Object> response = esWriteService.insert1(esWriteBean);
					Map<String, Object> responseData = (Map<String, Object>) response.get("resultData");
					if (response.get(Pub.KEY_RESULT).toString().equals(Pub.VAL_SUCCESS)) {
						logger.info(
								module + "->" + map.get("DI_name") + "->录入数据条数：" + responseData.get("insert_number"));
					} else {
						logger.error(module + "->" + map.get("DI_name") + "->" + response.get(Pub.KEY_MESSAGE));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			return;
		}
	}

	/**
	 * 生成T639 DI数据 只适用于T639
	 * 
	 * @param DIMapObj
	 */
	public void createT639DI(String type, Map<String, Object> DIMapObj, int numDay) {
		if (DIMapObj == null || DIMapObj.size() < 1) {
			logger.warn("createT639DI is fail ： alertMap is null or 0 in length");
			return;
		}

		try {
			// 生成日历插件， 计算出 第二天的开始时间和结束时间
			Calendar calendar = Calendar.getInstance();
			Date date = new Date();
			calendar.setTime(date);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			Date startDate = calendar.getTime();
			calendar.add(Calendar.DAY_OF_MONTH, numDay + 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			Date endDate = calendar.getTime();

			// String strIndex =
			// Pub.Index_Head+Pub.transform_DateToString(startDate,Pub.Index_Food_Simpledataformat);
			Map<String, Object> map = null;
			// 循环 告警配置信息
			for (String key : DIMapObj.keySet()) {
				String module = "T639".equals(key) ? "分发" : "加工";
				int addNum = 0;
				map = (Map<String, Object>) DIMapObj.get(key); // 获取单条配置信息

				String cron = map.get("time_interval").toString();
				List<Date> timeList = CronPub.getTimeBycron_Date(cron, startDate, endDate);
				String subType = map.get("DI_name").toString();
				String serviceType = map.get("serviceType").toString();
				String name = "";
				String IP = map.get("IP").toString();
				String path = map.get("path").toString();

				Map<String, Object> indexMap = new HashMap<>();
				for (Date dt : timeList) {
					String indexKey = Pub.Index_Head + Pub.transform_DateToString(dt, Pub.Index_Food_Simpledataformat);
					// 判断是否预生成过，没有的话生成
					if (isExist_DI_Data(indexKey, type, key)) {
						continue;
					}
					if (!indexMap.containsKey(indexKey)) {
						indexMap.put(indexKey, new ArrayList<>());
					}
					DataBean dataBean = new DataBean();
					dataBean.setName(name);
					dataBean.setType(subType);
					dataBean.setServiceType(serviceType);

					dataBean.setAging_status("未处理");
					Map<String, Object> fields = new HashMap<>();
					fields.put("module", module);
					fields.put("data_time", Pub.transform_DateToString(dt, "yyyy-MM-dd HH:mm:ss.SSSZ"));
					fields.put("ip_addr", IP);
					fields.put("file_name", path);
					fields.put("end_time", Pub.transform_DateToString(date, "yyyy-MM-dd HH:mm:ss.SSSZ"));
					dataBean.setFields(fields);

					((List<String>) indexMap.get(indexKey)).add(JSON.toJSONString(dataBean));
				}
				logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

				for (String strIndex : indexMap.keySet()) {
					// 分批次录入数据
					EsWriteBean esWriteBean = new EsWriteBean();
					esWriteBean.setIndex(strIndex);
					// esWriteBean.setType(type);
					esWriteBean.setData((List<String>) indexMap.get(strIndex));
					Map<String, Object> response = esWriteService.insert1(esWriteBean);
					Map<String, Object> responseData = (Map<String, Object>) response.get("resultData");
					if (response.get(Pub.KEY_RESULT).toString().equals(Pub.VAL_SUCCESS)) {
						addNum += (int) responseData.get("insert_number");
					} else {
						logger.error(module + "->" + subType + "->" + response.get(Pub.KEY_MESSAGE));
					}
				}

				logger.info(module + "->" + subType + "->录入数据条数：" + addNum);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 判断是否有 预生成数据
	 * 
	 * @param index
	 * @param type
	 * @param subType
	 * @return
	 * @throws Exception
	 */
	public boolean isExist_DI_Data(String index, String type, String subType) throws Exception {
		boolean flag = false;

		EsQueryBean_Exsit esQueryBean_exsit = new EsQueryBean_Exsit();
		esQueryBean_exsit.setIndex(index);
		esQueryBean_exsit.setType(type);
		esQueryBean_exsit.setSubType(subType);
		Map<String, Object> resultMap = esQueryService.indexIsExist(esQueryBean_exsit);
		if (!"success".equals(resultMap.get("result"))) {
			throw new Exception("查询发生了错误,错误信息:" + resultMap.get("message"));
		}
		flag = (Boolean) resultMap.get("resultData");

		return flag;
	}

	/**
	 * 获取指定时区的时间
	 * 
	 * @param date
	 * @param hours
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public Date setWorldTime(Date date, int hours) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		date = calendar.getTime();
		return date;
	}

}
