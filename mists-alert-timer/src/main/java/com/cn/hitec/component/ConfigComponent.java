package com.cn.hitec.component;

import com.cn.hitec.bean.EsWriteBean;
import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.service.AgingStatusService;
import com.cn.hitec.service.ConfigService;
import com.cn.hitec.util.Pub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName:
 * @Description: 初始化告警配置信息---不要多次执行
 * @author: fukl
 * @data: 2017年05月10日 下午1:14
 */
@Component
@Order(value = 1)
public class ConfigComponent implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(ConfigComponent.class);

    @Autowired
    ConfigService configService;

    @Override
    public void run(String... strings) throws Exception {
        try {

            //初始化 alertMap
            configService.initAlertMap();

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

}
