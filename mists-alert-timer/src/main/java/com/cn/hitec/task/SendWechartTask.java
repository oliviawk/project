package com.cn.hitec.task;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.service.SendWechartMessage;
import com.cn.hitec.util.HttpPub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @description: TODO(这里用一句话描述这个类的作用)
 * @author james
 * @since 2017年7月21日 下午3:28:16
 * @version
 *
 */
@Component
@Order(value = 4)
public class SendWechartTask {
	private static final Logger logger = LoggerFactory.getLogger(SendWechartTask.class);

	@Autowired
	SendWechartMessage sendWechartMessage;

	/**
     * 发送微信、短信
	 */
	@Scheduled(cron = "0/30 * * * * ?")
	public void updTimeoutData() {
		try {
			sendWechartMessage.sendWechart();
			sendWechartMessage.sendSMS();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
