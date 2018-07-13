package com.cn.hitec.service;

import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.cn.hitec.domain.DataInfo;
import com.cn.hitec.feign.client.DataSourceEsInterface;
import com.cn.hitec.repository.jpa.DataInfoRepository;
import org.apache.commons.lang.StringUtils;
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
	@Autowired
	DataInfoRepository dataInfoRepository;
	@Autowired
	DataSourceEsInterface dataSourceEsInterface;


	public void initAlertMap() {

		//清空列表
		Pub.DIMap_DS = Collections.synchronizedMap(new HashMap());
		Pub.DIMap_t639 = Collections.synchronizedMap(new HashMap());
		Pub.DIMap = Collections.synchronizedMap(new HashMap());
		Pub.DI_ConfigMap = Collections.synchronizedMap(new HashMap());

		/*  ------------ > 3.7日修改的新代码 < ------------------*/
		List<DataInfo> listDataInfo = dataInfoRepository.findAllChilden(3);

		//循环所有数据,区分是采集、加工、分发 的数据，分别存入不同的map
		for (DataInfo di : listDataInfo){
			Map<String ,Object> map = new HashMap<>();
			map.put("DI_name",di.getName());
			map.put("sub_name",di.getSub_name());
			map.put("time_interval",di.getMonitor_times());
			map.put("should_time",di.getShould_time());
			map.put("last_time",di.getTimeout_threshold());
			map.put("IP",di.getIp());
			map.put("path",di.getFile_path());
			map.put("module",di.getModule());
			map.put("serviceType",di.getService_type());
			map.put("startMonitor",di.getStart_moniter());
			map.put("regular",di.getRegular());
			map.put("size_define",StringUtils.isEmpty(di.getFile_size_define()) ? "" : di.getFile_size_define());
			map.put("name_define",StringUtils.isEmpty(di.getFile_name_define()) ? "" : di.getFile_name_define());

			if ("FZJC".equals(di.getService_type()) && ("T639".equals(di.getName()) || "风流场".equals(di.getName()))) {
				Pub.DIMap_t639.put(di.getName() + "," + di.getIp() + "," + di.getService_type() + "," + di.getModule(), map);

//			} else if ("采集".equals(di.getModule())) {
//				Pub.DIMap.put(di.getName() + "," + di.getIp() + "," + di.getService_type() + "," + di.getModule(), map);
//
//			} else if ("加工".equals(di.getModule())) {
//				Pub.DIMap_machining.put(di.getName() + "," + di.getIp() + "," + di.getService_type() + "," + di.getModule(), map);
//
//			} else if ("分发".equals(di.getModule())) {
//				Pub.DIMap_distribute.put(di.getName() + "," + di.getIp() + "," + di.getService_type() + "," + di.getModule(), map);

			} else if("DS".equals(di.getModule())){
				Pub.DIMap_DS.put(di.getName() + "," + di.getIp() + "," + di.getService_type() + "," + di.getModule(), map);

			} else{
				Pub.DIMap.put(di.getName() + "," + di.getIp() + "," + di.getService_type() + "," + di.getModule(), map);
			}

		}

		List<Object> listStrategy = dataInfoRepository.findDataStrategyAll();
		for (Object di : listStrategy){
			Map<String ,Object> map = new HashMap<>();

			List list = JSON.parseArray(JSON.toJSONString(di),String.class);
			if(list.size() == 11 ){
				map.put("serviceType",list.get(0));
				map.put("DI_name",list.get(1));
				map.put("module",list.get(2));
				map.put("ip",list.get(3));
				map.put("strategy_name",list.get(4));
				map.put("wechart_send_enable",list.get(5));
				map.put("wechart_content",list.get(6));
				map.put("sms_send_enable",list.get(7));
				map.put("sms_content",list.get(8));
				map.put("send_users",list.get(9));
				map.put("regular",list.get(10));

				Pub.DI_ConfigMap.put(list.get(0)+","+list.get(1)+","+list.get(2)+","+list.get(3), map);
			}

		}

	}

	/**
	 * 生成 ID 数据表（节目表）
	 * 
	 * @param DIMap    基础数据配置
	 */
	public void createAlertDI(Map<String, Map> DIMap,int day,Date runDate) throws Exception{

		if (DIMap == null || DIMap.size() < 1) {
			logger.warn("createAlertDI is fail ： alertMap is null or 0 in length");
			return;
		}
		// 生成日历插件， 计算出 第二天的开始时间和结束时间
		Calendar calendar = Calendar.getInstance();

		Date date = new Date();

		calendar.setTime(date);

		calendar.add(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Date startDate = calendar.getTime();

		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date endDate = calendar.getTime();

		Calendar calendarYesterday = Calendar.getInstance();
		calendarYesterday.setTime(startDate);
		calendarYesterday.add(Calendar.DAY_OF_MONTH, -1);
		Date startDateYesterday = calendarYesterday.getTime();

		String strIndex = Pub.Index_Head + Pub.transform_DateToString(startDate, Pub.Index_Food_Simpledataformat);
		String strIndexYesterday = Pub.Index_Head
				+ Pub.transform_DateToString(startDateYesterday, Pub.Index_Food_Simpledataformat);
		Map<String, Object> map = null;
		// 循环 告警配置信息
		for (String key : DIMap.keySet()) {
			map = (Map<String, Object>) DIMap.get(key); // 获取单条配置信息
			String module = map.get("module").toString();
			try {
				String cron = map.get("time_interval").toString();
				List<Date> timeList = CronPub.getTimeBycron_Date(cron, startDate, endDate);
				List<String> listDataBean = new ArrayList<>();
				List<String> listDataBeanYesterday = new ArrayList<>();
				String serviceType = map.get("serviceType").toString();
				String subType = map.get("DI_name").toString();
				// if(!"炎热指数".equals(subType)){
				// continue;
				// }
				String name = map.get("sub_name").toString();
				String IP = map.get("IP").toString();
				String path = map.get("path").toString();

				for (Date dt : timeList) {
					if (runDate.getTime() > dt.getTime()){
						continue;
					}
					DataBean dataBean = new DataBean();
					dataBean.setName(name);
					dataBean.setServiceType(serviceType);
					dataBean.setType(subType);
					// 这里需要封装一个方法，根据不同的 数据源、时次，生成不同的应到时间和最晚时间
					int cron_shouldTime = Integer.valueOf(map.get("should_time").toString()) * 60;
					int cron_lastTime = Integer.valueOf(map.get("last_time").toString()) * 60;
					dataBean.setShould_time(Pub.transform_longDataToString(dt.getTime() / 1000 + cron_shouldTime,
							"yyyy-MM-dd HH:mm:ss.SSSZ"));
					dataBean.setLast_time(Pub.transform_longDataToString(dt.getTime() / 1000 + cron_shouldTime + cron_lastTime,
							"yyyy-MM-dd HH:mm:ss.SSSZ"));

					dataBean.setAging_status("未处理");
					dataBean.setStartMoniter(map.get("startMonitor").toString());

					Map<String, Object> fields = new HashMap<>();
					fields.put("module", module);
					fields.put("ip_addr", IP);
//					fields.put("file_name", path);

					// 用beforeHour和afterHour获取时区转换后是否变成了昨天
					int beforeHour = dt.getHours();
					fields.put("data_time", Pub.transform_DateToString(dt, "yyyy-MM-dd HH:mm:ss.SSSZ"));
//
					//添加文件大小范围和文件名
					fields.put("file_size_define",map.get("size_define").toString());
					String nameDefine = map.get("name_define").toString();
					String fileName = changeFileName(nameDefine,dt);

					fields.put("file_name",path+fileName);

					int afterHour = dt.getHours();

					dataBean.setFields(fields);

					if (beforeHour < afterHour) {
						listDataBeanYesterday.add(JSON.toJSONString(dataBean));
					} else {
						listDataBean.add(JSON.toJSONString(dataBean));
					}
				}
				System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				// 统计录入数据条数
				int addNum = 0;
				// 分批次录入数据,当天
				EsWriteBean esWriteBean = new EsWriteBean();
				esWriteBean.setIndex(strIndex);
				// esWriteBean.setType(type);
				esWriteBean.setData(listDataBean);
				Map<String, Object> response = esWriteService.insert1(esWriteBean);
				Map<String, Object> responseData = (Map<String, Object>) response.get("resultData");
				if (response.get(Pub.KEY_RESULT).toString().equals(Pub.VAL_SUCCESS)) {
					addNum += (int) responseData.get("insert_number");
				} else {
					logger.error(module + "->" + map.get("DI_name") + "->" + response.get(Pub.KEY_MESSAGE));
				}
				// 分批次录入数据,当天的前一天
				esWriteBean.setIndex(strIndexYesterday);
				esWriteBean.setData(listDataBeanYesterday);
				response = esWriteService.insert1(esWriteBean);
				responseData = (Map<String, Object>) response.get("resultData");
				if (response.get(Pub.KEY_RESULT).toString().equals(Pub.VAL_SUCCESS)) {
					addNum += (int) responseData.get("insert_number");
				} else {
					logger.error(module + "->" + map.get("DI_name") + "->" + response.get(Pub.KEY_MESSAGE));
				}

				logger.info(module + "->" + map.get("DI_name") + "->录入数据条数：" + addNum);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 定时生成数据源节目表
	 * @param date
	 * @param num
	 */
	public void makeProjectTable(Date date , int num , Map<String, Map> DIMap,Date runDate){
		if (DIMap == null || DIMap.size() < 1) {
			logger.warn("makeProjectTable is fail ： alertMap is null or 0 in length");
			return;
		}

		try {
			List<Object> outData = new ArrayList<Object>();

			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH, num);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			Date startDate = cal.getTime();
			cal.add(Calendar.DAY_OF_MONTH, 1);
			Date endDate = cal.getTime();


			Map<String, Object> map = null;
			// 循环 告警配置信息
			for (String key : DIMap.keySet()) {
				try {
					map = (Map<String, Object>) DIMap.get(key); // 获取单条配置信息

					String cron = map.get("time_interval").toString();
					String subType = map.get("DI_name").toString();
					String name = map.get("sub_name").toString();;
					String IP = map.get("IP").toString();
					String path = map.get("path").toString();
					String[] shuld_time = map.get("should_time").toString().split(",");
					String[] last_time = map.get("last_time").toString().split(",");

					List<String> timerList = CronPub.getTimeBycron_String(cron, "yyyy-MM-dd HH:mm:ss.SSSZ", startDate, endDate);
					int regular = Integer.parseInt(map.get("regular").toString());
					if(regular == 2){
						if (shuld_time.length < 1 || shuld_time.length != timerList.size() || shuld_time.length != last_time.length){
							logger.warn("------> 应到时间、最晚到达时间 和 数据时次 个数不匹配!!!");
							continue;
						}
					}

					for (int i = 0; i < timerList.size(); i++) {
						Date dt = Pub.transform_StringToDate(timerList.get(i),"yyyy-MM-dd HH:mm:ss.SSSZ");
						if (runDate.getTime() > dt.getTime()){
							continue;
						}
                        Map<String, Object> data = new HashMap<String, Object>();
                        data.put("aging_status", "未处理");
                        data.put("occur_time", 0);
                        data.put("name", name);

                        data.put("type", subType);
                        data.put("startMoniter", "yes");

                        Map<String, Object> fields = new HashMap<String, Object>();
                        fields.put("data_time", timerList.get(i));


                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dt);
						if(regular == 2){
							calendar.add(Calendar.MINUTE, Integer.parseInt(shuld_time[i]));
						}else{
							calendar.add(Calendar.MINUTE, Integer.parseInt(shuld_time[0]));
						}
                        data.put("should_time", Pub.transform_DateToString(calendar.getTime(),"yyyy-MM-dd HH:mm:ss.SSSZ"));

						if(regular == 2){
							calendar.add(Calendar.MINUTE, Integer.parseInt(last_time[i]));
						}else{
							calendar.add(Calendar.MINUTE, Integer.parseInt(last_time[0]));
						}
						data.put("last_time", Pub.transform_DateToString(calendar.getTime(),"yyyy-MM-dd HH:mm:ss.SSSZ"));

                        fields.put("file_name", path);
                        fields.put("ip_addr", IP);
                        fields.put("module", "DS");

						//添加文件大小范围和文件名
						fields.put("file_size_define",map.get("size_define").toString());
						String nameDefine = map.get("name_define").toString();
						String fileName = changeFileName(nameDefine,dt);
						fields.put("file_name",path+fileName);


                        data.put("fields", fields);
                        outData.add(data);
                    }
				} catch (Exception e) {
					e.printStackTrace();
					logger.warn(e.getMessage());
				}
			}

			Map<String, Object> backData = dataSourceEsInterface.insertDataSource_DI(JSON.toJSONString(outData));
			logger.info("DS -> SEVP_NMC_RFFC_SCON_EME_ACHN_LNO_P9 ->录入数据记录：" + backData+"----生成:"+outData.size()+"条");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	/**
	 * 生成T639 DI数据 只适用于T639
	 * 
	 * @param DIMapObj
	 */
	public void createT639DI(String type, Map<String, Map> DIMapObj, int numDay) {
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
				String module = key.contains("T639") ? "分发" : "加工";
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
//					// 判断是否预生成过，没有的话生成
//					if (isExist_DI_Data(indexKey, type, key)) {
//						continue;
//					}
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
					fields.put("end_time", Pub.transform_DateToString(date, "yyyy-MM-dd HH:mm:ss.SSSZ"));

					//添加文件大小范围和文件名
					fields.put("file_size_define",map.get("size_define").toString());
					String nameDefine = map.get("name_define").toString();
					String fileName = changeFileName(nameDefine,dt);
					fields.put("file_name",path+fileName);

					dataBean.setFields(fields);

					dt.setMinutes(startDate.getMinutes()-30);
					dataBean.setLast_time(Pub.transform_DateToString(dt, "yyyy-MM-dd HH:mm:ss.SSSZ"));

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


	public void initAlertMould(){
		List<Object> list = dataInfoRepository.findAlertModule();
		for (Object obj : list){
			JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(obj));
			if (jsonArray.size() == 2){
				Pub.alertModuleMap.put(jsonArray.get(0).toString(),jsonArray.get(1).toString());
			}
		}

	}

	/**
	 * 预生成文件名 对比对象：北京时间
	 * @param nameDefine
	 * @param dt
	 * @return
	 */
	public String changeFileName(String nameDefine,Date dt ){
		String fileName = "";
		try {
			if (StringUtils.isNotEmpty(nameDefine)){

                String timeFormat = nameDefine.substring(nameDefine.indexOf("{")+1,nameDefine.indexOf("}"));
                String timeZoneFormat = "0";
                if (nameDefine.indexOf("[") > -1 && nameDefine.indexOf("]") > -1){
                    timeZoneFormat = nameDefine.substring(nameDefine.indexOf("[")+1,nameDefine.indexOf("]"));
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(dt);
                cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(timeZoneFormat));

                fileName = nameDefine.replace("{"+timeFormat+"}",Pub.transform_DateToString(cal.getTime(),timeFormat)).replace("["+timeZoneFormat+"]","");
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}
}
