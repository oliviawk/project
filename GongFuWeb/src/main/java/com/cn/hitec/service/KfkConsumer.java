package com.cn.hitec.service;

import com.cn.hitec.controller.WebSocketController;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @description: 消费Kafka—— ALERT(topic)数据类,实时向页面传递告警信息
 * @author fukl
 * @since 2017年8月27日 下午2:59:06
 * @version
 *
 */
@Service
public class KfkConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KfkConsumer.class);


    private final KafkaConsumer<String, String> consumer;
    private List<String> list  = new ArrayList<>();
//    private String TOPIC = "websocket";
    private String TOPIC = "ALERT";
    private String GROUP = "0";
    public KfkConsumer() {

        //*******************bootstrap.servers方式******************//
        Properties props = new Properties();
        // 设置brokerServer(kafka)ip地址
        props.put("bootstrap.servers", "10.30.17.173:9092,10.30.17.174:9092,10.30.17.175:9092");
//        props.put("bootstrap.servers", "127.0.0.1:9092");
        // 设置consumer group name
        props.put("group.id", GROUP);

        props.put("enable.auto.commit", "false");

        // 设置使用最开始的offset偏移量为该group.id的最早。如果不设置，则会是latest即该topic最新一个消息的offset
        //earliest
        // 如果采用latest，消费者只能得道其启动后，生产者生产的消息
//		props.put("auto.offset.reset", "latest");
        //
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
        consumer = new KafkaConsumer<String, String>(props);

    }

    public void consume() {

        consumer.subscribe(Arrays.asList(TOPIC));

        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    String msg = record.value();
                    for (WebSocketController webSocket : WebSocketController.wbSockets){
                        webSocket.sendMessage(msg);
                    }
                }
                consumer.commitSync();
            }catch (Exception e){
                logger.error("!!!!!!error");
                logger.debug("",e);
                e.printStackTrace();
            }
        }

    }


//    public static void main(String[] args) {
//        new KfkConsumer().consume();
//    }
}