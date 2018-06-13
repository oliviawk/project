package com.cn.hitec.service;

import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.bean.AlertBeanNew;
import com.cn.hitec.domain.Users;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.repository.jpa.DataInfoRepository;
import com.cn.hitec.repository.jpa.UsersRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.EsWriteBean;
import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.util.Pub;

/**
 * @Description: 这里是描述信息
 * @author: fukl
 * @data: 2017年10月01日 15:13
 */
@Service
public class SendAlertMessage {
	private static final Logger logger = LoggerFactory.getLogger(SendAlertMessage.class);
	@Autowired
	EsWriteService esWriteService;
	@Autowired
	EsQueryService esQueryService;
	@Autowired
	KafkaProducer kafkaProducer;
	@Autowired
	UsersRepository usersRepository;

	@Autowired
	DataInfoRepository dataInfoRepository;


	public void sendAlert(String index, String type, Map<String, Object> map) {

		try {
			Map<String,Object> strategyMap = new HashMap<>();
			Map<String, Object> fields = (Map<String, Object>) map.get("fields");

			String str_type = map.get("_type").toString();
			if(!"DATASOURCE".equals(str_type)){
				// 去掉资料时次里的时区
				fields.put("data_time",
						Pub.transform_DateToString(
								Pub.transform_StringToDate(fields.get("data_time").toString(), "yyyy-MM-dd HH:mm:ss.SSSZ"),
								"yyyy-MM-dd HH:mm:ss"));

			}

			String module = fields.get("module").toString();
			String ipAddr = fields.containsKey("ip_addr") ? fields.get("ip_addr").toString() : "-";

			String strKey = str_type+","+map.get("type").toString()+","+module+","+ipAddr;

			if(Pub.DI_ConfigMap.containsKey(strKey)){
				strategyMap = (Map<String, Object>) Pub.DI_ConfigMap.get(strKey);
				if(strategyMap == null ){
					System.err.println("创建告警信息失败");
					return ;
				}
				String alertTitle = map.get("type") + "--" + fields.get("module") + "--" + fields.get("data_time")
						+ " 时次产品 ，超时未到达";

				AlertBeanNew alertBean = new AlertBeanNew();

				alertBean.setType("SYSTEM.ALARM.EI");
				alertBean.setName(""+str_type+"业务告警");
				alertBean.setMessage(str_type+"业务告警");
				alertBean.setGroupId("OP_"+str_type +"_"+ module);
				alertBean.setOccur_time(Pub.transform_DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
				alertBean.setAlertType("01");
				alertBean.setEventType("OP_"+str_type +"_"+ module+"-1-01-01");
				alertBean.setLevel(fields.containsKey("event_status") ? fields.get("event_status").toString() : "1");
				alertBean.setCause("-");
				alertBean.setModule(module);
				alertBean.setDataName(map.get("type").toString());
				alertBean.setSubName(map.get("name").toString());
				alertBean.setData_time(fields.get("data_time").toString());
				alertBean.setIpAddr(ipAddr);
				if("DATASOURCE".equals(str_type)){
					alertBean.setShould_time(map.containsKey("should_time") ? map.get("should_time").toString():"0");
					alertBean.setLast_time(map.containsKey("last_time") ? map.get("last_time").toString():"0");
				}else{
					alertBean.setShould_time(map.containsKey("should_time") ?
							Pub.transform_DateToString(
									Pub.transform_StringToDate(map.get("should_time").toString(), "yyyy-MM-dd HH:mm:ss.SSSZ"),
									"yyyy-MM-dd HH:mm:ss")
							: "0");
					alertBean.setLast_time(map.containsKey("last_time") ?
							Pub.transform_DateToString(
									Pub.transform_StringToDate(map.get("last_time").toString(), "yyyy-MM-dd HH:mm:ss.SSSZ"),
									"yyyy-MM-dd HH:mm:ss")
							: "0");
				}

				alertBean.setReceive_time("0");
				alertBean.setEventTitle(alertTitle);
				alertBean.setDesc("超时未到达");
				alertBean.setErrorMessage(fields.containsKey("event_info") ? fields.get("event_info").toString() : "-");
				alertBean.setPath(fields.containsKey("path") ? fields.get("path").toString() : "-");
				alertBean.setFileName(fields.containsKey("file_name") ? fields.get("file_name").toString() : "-");

				// 初始化告警实体类
				EsWriteBean esWriteBean = new EsWriteBean();
				esWriteBean.setIndex(index);
				esWriteBean.setType(type);
				String module_key = alertBean.getGroupId()+","+alertBean.getDataName()+","+alertBean.getIpAddr();
				String str_id = Pub.MD5(module_key+","+alertBean.getData_time());
				esWriteBean.setId(str_id);
				List<String> params = new ArrayList<>();
				params.add(JSON.toJSONString(alertBean));
				esWriteBean.setData(params);
				esWriteService.add(esWriteBean); // 将告警信息写入ES

				//判断是否发送微信、短信告警
				boolean isAlert = true;
				/*JSONObject jsonObject = new JSONObject();
				jsonObject.put("index",index);
				jsonObject.put("type",type);
                String moduleKey = module_key;
                String moduleKeyParent = "";
                while (true){
                    moduleKeyParent = Pub.alertModuleMap.containsKey(moduleKey) ? Pub.alertModuleMap.get(moduleKey):moduleKey;

                    if (moduleKey.equals(moduleKeyParent)){
                        break;
                    }
                    jsonObject.put("id",Pub.MD5(moduleKeyParent+","+alertBean.getData_time()));
                    String id_cj = esQueryService.getDocumentById(jsonObject.toJSONString());
                    if (!StringUtils.isEmpty(id_cj)){
                        isAlert_parent = true;
                        logger.info("-------> 存在上级告警");
                        logger.info("过滤掉的告警信息："+JSON.toJSONString(alertBean));
                        break;
                    }

                    moduleKey = moduleKeyParent;
                    moduleKeyParent = "";
                }*/


                /*====================modified by czt 2018.6.11=======================*/
				//先比较当前的告警时间范围和最大告警数
                List<Object> curmodules = dataInfoRepository.findAlertRules(str_type,module,map.get("type").toString(),ipAddr);
				if(curmodules.size() > 0){
					JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(curmodules));

					dataInfoRepository.addAlertCnt(jsonArray.getLongValue(0));

					if(jsonArray.getInteger(2) != null ){
						if(jsonArray.getInteger(3) > jsonArray.getInteger(2)){
							isAlert = false;
						}
					}

					if(isAlert){
						String[] range = jsonArray.getString(1).split("-");
						String[] times = range[0].split("-");
						SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
						String now = df.format(new Date());
						if(times[0].compareTo(times[1]) >= 0){
							if(now.compareTo(times[0]) >= 0 || now.compareTo(times[0]) <= 0){
								isAlert = true;
							}
							else{
								isAlert = false;
							}
						}
						else{
							if(now.compareTo(times[0]) >= 0 && now.compareTo(times[0]) <= 0){
								isAlert = true;
							}
							else{
								isAlert = false;
							}
						}
					}
				}
				if(isAlert){
					List<Object> pres = dataInfoRepository.findPreModules(module_key);
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("index",index);
					jsonObject.put("type",type);
					for(Object pre : pres){
						jsonObject.put("id",Pub.MD5(pre.toString()+","+alertBean.getData_time()));
						String id_cj = esQueryService.getDocumentById(jsonObject.toJSONString());
						if (!StringUtils.isEmpty(id_cj)){
							isAlert = false;
							logger.info("-------> 存在上级告警");
							logger.info("过滤掉的告警信息："+JSON.toJSONString(alertBean));
							break;
						}
					}
				}


				if(isAlert){
					String weChartContent = strategyMap.get("wechart_content").toString();
					String smsContent = strategyMap.get("wechart_content").toString();
					String wechart_send_enable = strategyMap.get("wechart_send_enable").toString();
					String sms_send_enable = strategyMap.get("sms_send_enable").toString();
					//转换微信格式告警信息
					weChartContent = Pub.transformTitle(weChartContent,alertBean);

						// 推送到前端
//					kafkaProducer.sendMessage("ALERT", null, JSON.toJSONString(alertBean));

					if("1".equals(wechart_send_enable)){
						//查询发送的用户
						initWeChart(esWriteBean,strategyMap,weChartContent);
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initWeChart(EsWriteBean esWriteBean,Map<String,Object> strategyMap,String weChartContent){
//		//查询发送的用户
//		String[] strIds = strategyMap.get("send_users").toString().split(",");
//		long[] longIds = new long[strIds.length];
//		for (int i = 0 ; i < strIds.length ; i ++){
//			longIds[i] = Long.parseLong(strIds[i]);
//		}
//		List<Users> usersList = usersRepository.findAllByIds(longIds);
		String strParentId = strategyMap.get("send_users").toString();
		long parentId = Long.parseLong(strParentId);
		List<Users> usersList = usersRepository.findAllByPid(parentId);
		String strUsers = "";
		for (Users use : usersList){
			if ("".equals(strUsers)){
				strUsers += use.getWechart();
			}else {
				strUsers += "|"+use.getWechart();
			}

		}

		esWriteBean.setType("sendWeichart");
		Map<String,Object> weichartMap = new HashMap<>();
		weichartMap.put("sendUser", StringUtils.isEmpty(strUsers) ? "@all":strUsers);
//		weichartMap.put("sendUser","QQ670779441|FuTieQiang");
		weichartMap.put("alertTitle",weChartContent);
		weichartMap.put("isSend","false");
		weichartMap.put("send_time",0);
		weichartMap.put("create_time",System.currentTimeMillis());

		List<String> paramWeichart = new ArrayList<>();
		paramWeichart.add(JSON.toJSONString(weichartMap));
		esWriteBean.setData(paramWeichart);
		esWriteService.add(esWriteBean); // 存入微信待发送消息
	}
}
