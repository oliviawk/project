package com.cn.hitec.task;

import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.service.AgingStatusService;
import com.cn.hitec.service.ConfigService;
import com.cn.hitec.util.Pub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;


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
        logger.info("alertMap_collect.size:"+Pub.DIMap_collect.size()+"");
        logger.info("alertMap_machining.size:"+Pub.DIMap_machining.size()+"");
        logger.info("alertMap_distribute.size:"+Pub.DIMap_distribute.size()+"");
        configService.createAlertDI("FZJC","采集",Pub.DIMap_collect);
        configService.createAlertDI("FZJC","加工",Pub.DIMap_machining);
        configService.createAlertDI("FZJC","分发",Pub.DIMap_distribute);

        configService.createT639DI("FZJC",Pub.DIMap_t639,5);

        //这里要加个判断， 查询是否生成了第二天的数据，如果没有， 告警并尝试重新生成
	}


    @Scheduled(cron = "30 0/3 * * * ?")
    public void updAgingStatus() {
        try {
            Date nowDate = new Date();
            agingService.collect_task(nowDate);
//            String index = "log_" + Pub.transform_DateToString(nowDate,"yyyyMMdd");
//
//            EsWriteBean esWriteBean = new EsWriteBean();
//            esWriteBean.setIndex(index);
//            esWriteBean.setType("FZJC");
//            Map<String,Object> params = new HashMap<>();
//            for (String strKey : map.keySet()){
//                esWriteBean.setId(strKey);
//                params.put("aging_status",map.get(strKey));
//                esWriteBean.setParams(params);
//                esWriteService.update_field(esWriteBean);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
