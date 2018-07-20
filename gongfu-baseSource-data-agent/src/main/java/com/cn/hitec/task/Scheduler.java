package com.cn.hitec.task;

import com.cn.hitec.service.GongFuService;
import com.cn.hitec.tools.Pub;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {
    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    GongFuService gongFuService;

    @Scheduled(cron = "0 0/1 * * * ?") // 每分钟执行一次
    public void statusCheck() {

        logger.info("每分钟执行一次。开始……");
        if (StringUtils.isEmpty(Pub.cookie_rill)) {
            logger.error("获取锐捷cookie错误！请尽快核实。");
            return;
        }
        logger.info("锐捷cookie：" + Pub.cookie_rill);
        gongFuService.writeData();
        gongFuService.findDiskData();
        gongFuService.findNetdate();
        gongFuService.findEventData();
        logger.info("每分钟执行一次。结束。");
    }

    @Scheduled(cron = "0 0 1 * * ?") // 每分钟执行一次
    public void rillCookie() {
        logger.info("每天执行一次。开始……");
        Pub.cookie_rill = Pub.login();
        logger.info("每天执行一次。结束。");
    }
}