package com.cn.hitec.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.AlertBean;
import com.cn.hitec.bean.EsWriteBean;
import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.util.HttpPub;
import com.cn.hitec.util.Pub;

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

	public void sendAlert(String index, String type, Map<String, Object> map) {

		try {
			Map<String, Object> fields = (Map<String, Object>) map.get("fields");
			// 去掉资料时次里的时区
			fields.put("data_time",
					Pub.transform_DateToString(
							Pub.transform_StringToDate(fields.get("data_time").toString(), "yyyy-MM-dd HH:mm:ss.SSSZ"),
							"yyyy-MM-dd HH:mm:ss"));

			String alertTitle = map.get("type") + "--" + fields.get("module") + "--" + fields.get("data_time")
					+ " 时次产品 ，超时未到达";
			AlertBean alertBean = new AlertBean();
			alertBean.setType("OP_FZJC_TIMER");
			alertBean.setAlertType("超时");
			alertBean.setLevel(fields.containsKey("event_status") ? fields.get("event_status").toString() : "");
			alertBean.setTitle(alertTitle);
			alertBean.setTime(Pub.transform_DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
			alertBean.setIp(fields.containsKey("ip_addr") ? fields.get("ip_addr").toString() : "");
			alertBean.setDesc(fields.containsKey("event_info") ? fields.get("event_info").toString() : "");
			alertBean.setCause("");
			alertBean.setData_name(map.get("type").toString());
			alertBean.setData_time(fields.get("data_time").toString());
			alertBean.setModule(fields.get("module").toString());
			// 初始化告警实体类

			EsWriteBean esWriteBean = new EsWriteBean();
			esWriteBean.setIndex(index);
			esWriteBean.setType(type);
			List<String> params = new ArrayList<>();
			params.add(JSON.toJSONString(alertBean));
			esWriteBean.setData(params);
			esWriteService.add(esWriteBean); // 将告警信息写入ES
			// 推送到前端
			kafkaProducer.sendMessage("ALERT", null, JSON.toJSONString(alertBean));
			// 发送告警消息 到微信
			HttpPub.httpPost("@all", alertTitle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
