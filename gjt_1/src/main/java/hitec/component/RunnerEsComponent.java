package hitec.component;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import hitec.repository.ESRepository;
import hitec.service.ESClientAdminService;

/**
 * @ClassName:
 * @Description: 启动ES客户端和模板
 * @author: fukl
 * @data: 2017年05月10日 下午1:14
 */
@Component
@Order(value = 1)
public class RunnerEsComponent  implements CommandLineRunner {
    @Autowired
    ESRepository esRepository;
    @Autowired
    ESClientAdminService clientAdminService;

    @Override
    public void run(String... strings) throws Exception {
        //初始化ES连接信息
        System.out.println("start init es");
        esRepository.buildClient();
        esRepository.bulidBulkProcessor();
        esRepository.buildTemplate();

        Thread.sleep(1000);

        clientAdminService.getClusterHealth();
    }

}
