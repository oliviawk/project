package com.cn.hitec.task;

import com.cn.hitec.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
@Order(value = 3)
public class AlertModule {
	private static final Logger logger = LoggerFactory.getLogger(AlertModule.class);

	@Autowired
	ConfigService configService;

	@Scheduled(cron = "0 0/2 * * * ?")
	public void updTimeoutData() {
		configService.initAlertMould();
	}


}
