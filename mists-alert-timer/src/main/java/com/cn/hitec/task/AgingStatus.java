package com.cn.hitec.task;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.repository.jpa.DataInfoRepository;
import com.cn.hitec.service.AgingStatusService;
import com.cn.hitec.service.ConfigService;
import com.cn.hitec.util.Pub;
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
public class AgingStatus {
	private static final Logger logger = LoggerFactory.getLogger(AgingStatus.class);

	@Autowired
	AgingStatusService agingStatus;
    @Autowired
    ConfigService configService;

	/**
	 * 修改超时数据
	 */
	@Scheduled(cron = "10 * * * * ?")
	public void updTimeoutData() {
		try {
            configService.initAlertMould();
			agingStatus.collect_task();
			agingStatus.collectDataSource_task();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
