package com.cn.hitec.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.util.Pub;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(ConfigServiceTest.class);

	@Autowired
	ConfigService configService;

	@Autowired
	ConfigService service;

	@Test
	public void createAlertItem() throws Exception {
		// 初始化配置文件 不能多次执行
		logger.info("init config start");

		// int n = configService.initAlertConfigData_collect();
		// logger.info("init collect alert_config end , data number :" + n);
		// Thread.sleep(1000);
		// int n2 = configService.initAlertConfigData_machining();
		// logger.info("init machining alert_config end , data number :" + n2);
		// Thread.sleep(1000);
		// int n3 = configService.initAlertConfigData_distribute();
		// logger.info("init distribute alert_config end , data number :" + n3);
		// Thread.sleep(1000);

		// 初始化 alertMap
		configService.initAlertMap();

		logger.info(JSON.toJSONString(Pub.DIMap_collect));
		logger.info(JSON.toJSONString(Pub.DIMap_machining));
		logger.info(JSON.toJSONString(Pub.DIMap_distribute));
	}

	/**
	 * 创建数据测试
	 */
	@Test
	public void initData() {
		// 初始化 alertMap
		configService.initAlertMap();
		logger.info("alertMap_collect.size:" + Pub.DIMap_collect.size() + "");
		logger.info("alertMap_machining.size:" + Pub.DIMap_machining.size() + "");
		logger.info("alertMap_distribute.size:" + Pub.DIMap_distribute.size() + "");
		// configService.createAlertDI("FZJC","采集",Pub.DIMap_collect);
		// configService.createAlertDI("FZJC","加工",Pub.DIMap_machining);
		// configService.createAlertDI("FZJC","分发",Pub.DIMap_distribute);

		logger.info("DIMap_t639.size:" + Pub.DIMap_t639.size() + "");
		configService.createT639DI("FZJC", Pub.DIMap_t639, 5);

		logger.info("------");
	}

	@Autowired
	AgingStatusService agingService;
	@Autowired
	EsWriteService esWriteService;

	@Test
	public void updata_agings() {
		try {
			// 生成日历插件， 计算出 第二天的开始时间和结束时间
			// Calendar calendar = Calendar.getInstance();
			// Date date = new Date();
			// calendar.setTime(date);
			// calendar.add(Calendar.DAY_OF_MONTH , 1);
			//
			// Date endDate = calendar.getTime();
			// agingService.collect_task(date);

			System.out.println("----");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void initT639() {
		// String json = "{\"DI_name\":\"风流场\",\"time_interval\":\"0 0 2/3 * * ?
		// *\",\"should_time\":0,\"last_time\":0,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"contacts_information\":[],\"IP\":\"10.30.16.223\",\"path\":\"Z:\\\\NoGeography\\\\forecast\\\\t639\",\"file_name\":\"T639_GMFS_WIND_2017102508.json\",\"transfer_type\":\"\"}";
		// Map<String,Object> map = JSON.parseObject(json);
		// Map<String,Object> a = new HashMap<>();
		// a.put("风流场",map);
		// configService.createT639DI("FZJC",a,5);
		System.out.println("");
	}
}