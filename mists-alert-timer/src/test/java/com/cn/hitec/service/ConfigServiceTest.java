package com.cn.hitec.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.repository.jpa.DataInfoRepository;
import com.cn.hitec.util.HttpPub;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.util.Pub;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({ "dev" })
public class ConfigServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(ConfigServiceTest.class);

	@Autowired
	ConfigService configService;

	@Autowired
	ConfigService service;

	@Autowired
	DataInfoRepository dataInfoRepository;

	@Autowired
	HttpPub httpPub;

	@Autowired
	EsQueryService esQueryService;

	// @Test
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

		logger.info(JSON.toJSONString(Pub.DIMap));
//		logger.info(JSON.toJSONString(Pub.DIMap_machining));
//		logger.info(JSON.toJSONString(Pub.DIMap_distribute));
	}

	/**
	 * 创建数据测试
	 */
	@Test
	public void initData() throws Exception {

		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		calendar.setTime(date);
//		calendar.add(Calendar.HOUR_OF_DAY, -1);

		// 初始化 alertMap
		configService.createAlertDI(Pub.DIMap,0,calendar.getTime());
//		configService.createAlertDI("加工", Pub.DIMap_machining,0,calendar.getTime());
//		configService.createAlertDI("分发", Pub.DIMap_distribute,0,calendar.getTime());

		configService.makeProjectTable(new Date(),0,Pub.DIMap_DS,calendar.getTime());

		logger.info("---------------------------------开始执行定时任务，生成后5天的数据--------------------------------");
		configService.createT639DI("FZJC",Pub.DIMap_t639,5);

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

	@Test
	public void testWX(){
		httpPub.httpPost("@all","test");
	}

	@Test
	public void getAlertModule(){
		List<Object> currentAlert = dataInfoRepository.findAlertRules("LAPS","前处理","LSX1","10.30.16.242");
		JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(currentAlert));
		if(jsonArray.size() > 0){
			jsonArray = (JSONArray)jsonArray.get(0);
		}

		boolean alert = true;
		if(jsonArray.size() > 0 && jsonArray.getString(0) != null){
			System.out.println(jsonArray.get(0));



				String[] times = jsonArray.getString(1).split("-");
				SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
				String now = df.format(new Date());

				System.out.println(now);
				System.out.println(times[0]);
				System.out.println(times[1]);
				if(times[0].compareTo(times[1]) >= 0){
					if(now.compareTo(times[0]) >= 0 || now.compareTo(times[1]) <= 0){
						alert = true;
					}
					else{
						alert = false;
					}
				}
				else{
					if(now.compareTo(times[0]) >= 0 && now.compareTo(times[1]) <= 0){
						alert = true;
					}
					else{
						alert = false;
					}

			}

		}
		System.out.println(alert);

		System.out.println(JSON.toJSONString(currentAlert));
//		System.out.println(((Object[]) jsonArray.get(0))[0]);
//		System.out.println(((JSONArray)jsonArray.get(0)).getLongValue(0));

		/*List<Object> preAlerts = dataInfoRepository.findPreModules("OP_LAPS_前处理,LSX,10.30.16.242");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("index","data_20180615");
		jsonObject.put("type","LAPS");
		for(Object o : preAlerts){
			jsonObject.put("id",Pub.MD5(o.toString()+",2018-06-15 17:00:00.000+0800"));
			String id_cj = esQueryService.getDocumentById(jsonObject.toJSONString());
			System.out.println(id_cj);
		}*/


	}
}