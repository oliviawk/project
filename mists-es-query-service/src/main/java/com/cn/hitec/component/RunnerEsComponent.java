package com.cn.hitec.component;

import com.cn.hitec.repository.ESRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @ClassName:
 * @Description: 启动ES客户端和模板
 * @author: fukl
 * @data: 2017年05月10日 下午1:14
 */
@Component
//@Order(value = 1)
public class RunnerEsComponent  implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(RunnerEsComponent.class);
    @Autowired
    ESRepository esRepository;

    @Override
    public void run(String... strings) throws Exception {
        //初始化ES连接信息
        logger.info("start init es");
        esRepository.buildClient();
        esRepository.bulidBulkProcessor();

    }

}
