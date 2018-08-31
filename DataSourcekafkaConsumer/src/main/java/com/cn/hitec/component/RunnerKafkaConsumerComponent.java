package com.cn.hitec.component;

import com.cn.hitec.service.DataSourceSendConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//import com.cn.hitec.service.DataSourceSendConsumer;

/**
* @Description 项目启动时启动订阅kafka程序
* @author HuYiWu
* @date 2018年4月25日 下午4:23:36
 */
@Component
@Order(value=1)
public class RunnerKafkaConsumerComponent implements CommandLineRunner{

	@Autowired
	DataSourceSendConsumer dataSourceSendConsumer;

	@Override
	public void run(String... arg0) throws Exception {
		//启动kafka订阅程序
        System.out.println("kafka订阅程序启动.........");

        Thread datasourceSendThread = new Thread(){
        	@Override
			public void run(){
				dataSourceSendConsumer.consume();
			}
		};
		datasourceSendThread.start();

	}

}
