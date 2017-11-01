package com.cn.hitec.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 手动批量提交偏移量
 * @author 陈正廷
 *
 */
public class ManualOffsetConsumer {
	
    private static Logger logger = LoggerFactory.getLogger(ManualOffsetConsumer.class);
    
    public ManualOffsetConsumer() {
    }

    public static void main(String[] args) {
    	String TOPIC = "FZJC";
        String GROUP = "czt";
        
        Properties props = new Properties();
        //设置brokerServer(kafka)ip地址
        props.put("bootstrap.servers", "10.30.17.173:9092,10.30.17.174:9092,10.30.17.175:9092");
        //设置consumer group name
        props.put("group.id",GROUP);

        props.put("enable.auto.commit", "false");

        //设置使用最开始的offset偏移量为该group.id的最早。如果不设置，则会是latest即该topic最新一个消息的offset
        //如果采用latest，消费者只能得道其启动后，生产者生产的消息
        props.put("auto.offset.reset", "earliest");
        //
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String ,String> consumer = new KafkaConsumer<String ,String>(props);
        consumer.subscribe(Arrays.asList(TOPIC));
        final int minBatchSize = 5;  //批量提交数量
         List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
         while (true) {
             ConsumerRecords<String, String> records = consumer.poll(100);
             for (ConsumerRecord<String, String> record : records) {
                 System.out.println(record.offset() + "***" + record.key() + "***" + record.value());
            	 
//            	 logger.info("consumer message values is "+record.value()+" and the offset is "+ record.offset());
                 buffer.add(record);
             }
             if (buffer.size() >= minBatchSize) {
            	 System.out.println("*******now commit offset***********");
//                 logger.info("*******now commit offset***********");
                 consumer.commitSync();
                 buffer.clear();
             }
         }
    }

}

