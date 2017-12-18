package com.cn.hitec.component;

import com.cn.hitec.service.ConfigService;
import com.cn.hitec.service.FZJCService;
import com.cn.hitec.service.KfkConsumer;
import com.cn.hitec.tools.Pub;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName:
 * @Description: 初始化告警配置信息---不要多次执行
 * @author: fukl
 * @data: 2017年05月10日 下午1:14
 */
@Slf4j
@Component
@Order(value = 1)
public class ConfigComponent implements CommandLineRunner {

    @Autowired
    ConfigService configService;
    @Autowired
    KfkConsumer kfkConsumer;

    @Override
    public void run(String... strings) throws Exception {
        try {

            log.info("加载CI信息");
            //初始化 alertMap
            List<Map> listMap_collect = configService.getConfigAlert("collect");
            for (Map map : listMap_collect){
                String DI_name = map.get("DI_name").toString();
                Pub.alertMap_collect.put(DI_name,map);
            }
            List<Map> listMap_machining = configService.getConfigAlert("machining");
            for (Map map : listMap_machining){
                String DI_name = map.get("DI_name").toString();
                Pub.alertMap_machining.put(DI_name,map);
            }
            List<Map> listMap_distribute = configService.getConfigAlert("distribute");
            for (Map map : listMap_distribute){
                String DI_name = map.get("DI_name").toString();
                Pub.alertMap_distribute.put(DI_name,map);
            }
            log.info("创建kafka连接，启动websocket");
            kfkConsumer.consume();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
