package com.cn.hitec.task;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.service.AgingStatusService;
import com.cn.hitec.service.ConfigService;
import com.cn.hitec.util.Pub;

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
@Order(value = 2)
public class ConfigTask {
	private static final Logger logger = LoggerFactory.getLogger(ConfigTask.class);

	@Autowired
	ConfigService configService;
	@Autowired
	AgingStatusService agingService;
	@Autowired
	EsWriteService esWriteService;

	@Scheduled(cron = "0 0 20 * * ?")
	public void initCompleteTask() {
		logger.info("---------------------------------开始执行定时任务，生成第二天的数据--------------------------------");
		logger.info("alertMap_collect.size:" + Pub.DIMap_collect.size() + "");
		logger.info("alertMap_machining.size:" + Pub.DIMap_machining.size() + "");
		logger.info("alertMap_distribute.size:" + Pub.DIMap_distribute.size() + "");
		configService.createAlertDI("采集", Pub.DIMap_collect);
		configService.createAlertDI("加工", Pub.DIMap_machining);
		configService.createAlertDI("分发", Pub.DIMap_distribute);

		logger.info("DIMap_t639.size:" + Pub.DIMap_t639.size() + "");
		configService.createT639DI("FZJC", Pub.DIMap_t639, 5);

		// 这里要加个判断， 查询是否生成了第二天的数据，如果没有， 告警并尝试重新生成
	}

	@Scheduled(cron = "30 0/3 * * * ?")
	public void updAgingStatus() {
		try {
			Date nowDate = new Date();
			agingService.collect_task(nowDate); // 目前不适应 T639 数据
			agingService.task_T639();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
