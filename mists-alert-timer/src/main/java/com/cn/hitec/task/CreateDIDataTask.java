package com.cn.hitec.task;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.EsWriteBean;
import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.service.ConfigService;
import com.cn.hitec.util.Pub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

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
public class CreateDIDataTask {
	private static final Logger logger = LoggerFactory.getLogger(CreateDIDataTask.class);

	@Autowired
	ConfigService configService;
	@Autowired
	EsWriteService esWriteService;

	@Scheduled(cron = "10 45 23 * * ?")
	public void initCompleteTask() {
		boolean isError = false;
		int errorNum = 0;
		do {
			logger.info("---------------------------------开始执行定时任务，生成第二天的数据--------------------------------");
			try {
				configService.initAlertMap();

				configService.initSendMessage();

				System.out.println("DIMap.size:"+ Pub.DIMap.size()+",--:"+ JSON.toJSONString(Pub.DIMap));
				System.out.println("DIMap_t639.size:"+ Pub.DIMap_t639.size()+",--:"+ JSON.toJSONString(Pub.DIMap_t639));

				configService.createAlertDI( Pub.DIMap,1,new Date());

				configService.makeProjectTable(new Date(),1,Pub.DIMap_DS,new Date());

				logger.info("---------------------------------开始执行定时任务，生成后5天的数据--------------------------------");
				configService.createT639DI("FZJC",Pub.DIMap_t639,5);

			} catch (Exception e) {
				e.printStackTrace();
				isError = true;
				errorNum ++;
				try {
					//如果报错，过一分钟后再执行一次
					Thread.sleep(6000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			if(errorNum > 10){

				try {
					isError = false;
					EsWriteBean esWriteBean = new EsWriteBean();
					esWriteBean.setIndex(Pub.Index_Head+Pub.transform_DateToString(new Date(),Pub.Index_Food_Simpledataformat));
					esWriteBean.setType("sendWeichart");
					Map<String,Object> weichartMap = new HashMap<>();
					weichartMap.put("sendUser", "@all");
					weichartMap.put("alertTitle","创建第二天节目表连续失败10次，接下来将会影响第二天的业务，请手动生成节目表！！");
					weichartMap.put("isSend","false");
					weichartMap.put("send_time",0);
					weichartMap.put("create_time",System.currentTimeMillis());

					List<String> paramWeichart = new ArrayList<>();
					paramWeichart.add(JSON.toJSONString(weichartMap));
					esWriteBean.setData(paramWeichart);
					esWriteService.add(esWriteBean); // 存入微信待发送消息
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}while (isError);
	}


}
