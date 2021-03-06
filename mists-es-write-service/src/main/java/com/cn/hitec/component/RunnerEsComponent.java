package com.cn.hitec.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.service.ConfigService;
import com.cn.hitec.service.ESClientAdminService;
import com.cn.hitec.tools.Pub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName:
 * @Description: 启动ES客户端和模板
 * @author: fukl
 * @data: 2017年05月10日 下午1:14
 */
@Slf4j
@Component
@Order(value = 1)
public class RunnerEsComponent  implements CommandLineRunner {
    @Autowired
    ESRepository esRepository;
    @Autowired
    ConfigService configService;
    @Autowired
    ESClientAdminService clientAdminService;

    @Value("${es.indexHeader}")
    private String indexHeader;

    @Override
    public void run(String... strings) throws Exception {
        //初始化ES连接信息
        log.info("start init es");
        esRepository.buildClient();
        esRepository.bulidBulkProcessor();
        esRepository.buildTemplate();

        Thread.sleep(1000);

        clientAdminService.getClusterHealth(esRepository);
        configService.initAlertMap();

        Pub.Index_Head = indexHeader;

        if(Pub.DI_ConfigMap.size() < 1){
            log.error("初始化数据异常");
        }

        configService.initAlertMould();
    }

}
