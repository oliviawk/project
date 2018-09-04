package com.cn.hitec.component;

import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.service.ESClientAdminService;
import com.cn.hitec.tools.Pub;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @ClassName:
 * @Description: 启动ES客户端和模板
 * @author: fukl
 * @data: 2017年05月10日 下午1:14
 */
@Component
@Slf4j
//@Order(value = 1)
public class RunnerEsComponent  implements CommandLineRunner {
//    private static final Logger logger = LoggerFactory.getLogger(RunnerEsComponent.class);
    @Autowired
    ESRepository esRepository;
    @Autowired
    ESClientAdminService esClientAdminService;

    @Value("${es.indexHeader}")
    private String indexHeader;

    @Override
    public void run(String... strings) throws Exception {

        Pub.Index_Head = indexHeader;

        //初始化ES连接信息
        log.info("start init es");
        esRepository.buildClient();
        esRepository.bulidBulkProcessor();

        esClientAdminService.getClusterHealth();

    }

}
