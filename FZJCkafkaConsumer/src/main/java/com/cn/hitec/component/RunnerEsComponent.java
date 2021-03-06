package com.cn.hitec.component;

import com.cn.hitec.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
    OCF_Consumer ocf_consumer;
    @Autowired
    MQPF_AC_Consumer mqpf_ac_consumer;
    @Autowired
    RGF_Consumer rgf_consumer;
    @Autowired
    FZJCWorkingConsumer kafkaConsumer;
    @Autowired
    LAPS_WSConsumer lapsConsumer;
    @Autowired
    FZJCSendConsumer fzjcSendConsumer;
    @Autowired
    LAPSSendConsumer lapsSendConsumer;
    @Autowired
    LAPSCollectConsumer lapsCollectConsumer;
    @Autowired
    LAPSCollectConsumerEX LAPSCollectConsumerEX;

    @Override
    public void run(String... strings) throws Exception {
        Thread ocfthread = new Thread(){
            @Override
            public void run(){
                ocf_consumer.consume();
            }
        };
        ocfthread.start();
        Thread mqthread = new Thread(){
            @Override
            public void run(){
                mqpf_ac_consumer.consume();
            }
        };
        mqthread.start();

        Thread rgfThread = new Thread(){
            @Override
            public void run(){
                rgf_consumer.consume();
            }
        };
        rgfThread.start();

        Thread fzjc = new Thread(){

			@Override
			public void run() {
				kafkaConsumer.consume();
			}

        };

        Thread laps = new Thread(){

			@Override
			public void run() {
				lapsConsumer.consume();
			}

        };

        Thread fzjcSend = new Thread(){

			@Override
			public void run() {
				fzjcSendConsumer.consume();
			}

        };

        Thread lapsSend = new Thread(){

            @Override
            public void run() {
                lapsSendConsumer.consume();
            }

        };
        Thread lapsColl = new Thread(){

            @Override
            public void run() {
                lapsCollectConsumer.consume();
            }

        };

        fzjc.start();
        laps.start();
        fzjcSend.start();
        lapsSend.start();
        lapsColl.start();

        Thread xjf = new Thread(){

            @Override
            public void run() {
                LAPSCollectConsumerEX.consume();
            }

        };
        xjf.start();
    }

}
