package com.cn.hitec.component;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.service.ConfigService;
import com.cn.hitec.util.Pub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
    @Value("${es.indexHeader}")
    private String indexHeader ;

    @Override
    public void run(String... strings) throws Exception {
        try {
            //初始化 alertMap
            configService.initAlertMap();
            if (Pub.DI_ConfigMap.size() < 1){
                logger.error("请添加数据的告警策略！！");
            }
//            System.out.println(JSON.toJSONString(Pub.DI_ConfigMap));
            Pub.Index_Head = indexHeader;
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

}
