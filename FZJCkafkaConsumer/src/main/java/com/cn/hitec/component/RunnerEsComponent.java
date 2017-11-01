package com.cn.hitec.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.cn.hitec.service.FZJCSendConsumer;
import com.cn.hitec.service.KafConsumer;
import com.cn.hitec.service.LAPS_WSConsumer;

/**
 * @ClassName:
 * @Description: 启动kafkaConsumer
 * @author: fukl
 * @data: 2017年05月10日 下午1:14
 */
@Component
@Order(value = 2)
public class RunnerEsComponent implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(RunnerEsComponent.class);

    @Autowired
    KafConsumer kafkaConsumer;
    @Autowired
    LAPS_WSConsumer lapsConsumer;
    @Autowired
    FZJCSendConsumer fzjcSendConsumer;
    @Override
    public void run(String... strings) throws Exception {
        Thread t1 = new Thread(){

			@Override
			public void run() {
				kafkaConsumer.consume();
			}
        	
        };
        
//        Thread t2 = new Thread(){
//
//			@Override
//			public void run() {
//				lapsConsumer.consume();
//			}
        	
//        };
        
        Thread t3 = new Thread(){

			@Override
			public void run() {
				fzjcSendConsumer.consume();
			}
        	
        };
        t1.start();
//        t2.start();
        t3.start();
    	
//    	kafkaConsumer.consume();
//        lapsConsumer.consume();
    }

}
