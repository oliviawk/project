package com.cn.hitec.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.cn.hitec.service.GongFuService;
import com.cn.hitec.service.KfkConsumer;
import com.cn.hitec.tools.Pub;

/**
 * @ClassName: RunnerEsComponent
 * @Description: 启动ES客户端和模板
 * @author: fukl
 * @data: 2017年05月10日 下午1:14
 */
@Component
// @Order(value = 1)
public class RunnerEsComponent implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(RunnerEsComponent.class);
	@Autowired
	KfkConsumer kfkConsumer;
	@Autowired
	GongFuService gongFuService;

	@Override
	public void run(String... strings) throws Exception {
		try {
			Pub.cookie_rill = Pub.login();
			kfkConsumer.consume();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
