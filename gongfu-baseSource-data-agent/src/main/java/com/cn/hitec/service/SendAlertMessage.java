package com.cn.hitec.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.EsWriteBean;
import com.cn.hitec.feign.client.EsWriteService;

/**
 * @Description: 这里是描述信息
 * @author: fukl
 * @data: 2017年10月01日 15:13
 */
@Service
public class SendAlertMessage {
	@Autowired
	EsWriteService esWriteService;
	@Autowired
	KafkaProducer kafkaProducer;

	@Value("${profile.environment}")
	private String env;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public void sendAlert(String alertTitle, String sendUser) {

		List<String> listWriteBean = new ArrayList<>();
		try {
			Long timeNow = System.currentTimeMillis();
			// String timeNowString =
			// String.valueOf(System.currentTimeMillis());
			// 初始化告警实体类
			EsWriteBean esWriteBean = new EsWriteBean();
			// 微信告警信息
			Map<String, Object> params = new HashMap<>();
			params.put("alertTitle", alertTitle);
			params.put("create_time", timeNow);
			// 入库时是false,发送成功后变为true
			params.put("isSend", "false");
			params.put("sendUser", sendUser);
			params.put("send_time", timeNow);

			listWriteBean.add(JSON.toJSONString(params));

			// esWriteBean.setIndex("data_" +
			// sdf.format(Long.parseLong(timeNow)));
			esWriteBean.setIndex("data_" + sdf.format(timeNow));
			esWriteBean.setType("sendWeichart");
			esWriteBean.setParams(params);
			esWriteBean.setData(listWriteBean);
			if ("local".equals(env) || "dev".equals(env)) {
				esWriteService.add(esWriteBean);
				// System.out.println("跳过了告警");
			} else {
				// System.out.println("没有跳过告警");
				// 推送到前端
				// kafkaProducer.sendMessage("ALERT", null,
				// JSON.toJSONString(alertBean));
				// 发送告警消息 到微信
				esWriteService.add(esWriteBean); // 将告警信息写入ES,注意,会直接进行微信告警
			}

			/*
			 * Map<String, Object> fields = (Map<String, Object>)
			 * map.get("fields"); // 去掉资料时次里的时区 fields.put("data_time",
			 * Pub.transform_DateToString(
			 * Pub.transform_StringToDate(fields.get("data_time").toString(),
			 * "yyyy-MM-dd HH:mm:ss.SSSZ"), "yyyy-MM-dd HH:mm:ss"));
			 * 
			 * String alertTitle2 = map.get("type") + "--" +
			 * fields.get("module") + "--" + fields.get("data_time") +
			 * " 时次产品 ，超时未到达"; AlertBean alertBean = new AlertBean();
			 * alertBean.setType("OP_FZJC_TIMER"); alertBean.setAlertType("超时");
			 * alertBean.setLevel(fields.containsKey("event_status") ?
			 * fields.get("event_status").toString() : "");
			 * alertBean.setTitle(alertTitle);
			 * alertBean.setTime(Pub.transform_DateToString(new Date(),
			 * "yyyy-MM-dd HH:mm:ss"));
			 * alertBean.setIp(fields.containsKey("ip_addr") ?
			 * fields.get("ip_addr").toString() : "");
			 * alertBean.setDesc(fields.containsKey("event_info") ?
			 * fields.get("event_info").toString() : "");
			 * alertBean.setCause("");
			 * alertBean.setData_name(map.get("type").toString());
			 * alertBean.setData_time(fields.get("data_time").toString());
			 * alertBean.setModule(fields.get("module").toString());
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
