package com.cn.hitec.service;

import com.cn.hitec.domain.EsBean;
import com.cn.hitec.feign.client.GongFuWrite;
import com.cn.hitec.tools.Pub;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class KfkConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KfkConsumer.class);

    @Autowired
    GongFuWrite esWrite;

    private final KafkaConsumer<String, String> consumer;
    private List<String> list  = new ArrayList<>();
//    private String TOPIC = "websocket";
    private String TOPIC = "ALIYUN";
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

        List<String> listJson = new ArrayList<>();
        long timeout = 3000;
        long time = System.currentTimeMillis();
        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    String msg = record.value();
                    listJson.add(msg);
                }
                //当数据量超过100条，或者时间超过3秒，上传一次数据
                if(listJson.size() > 100 || System.currentTimeMillis() - time > timeout){
                    String index = "data_"+ Pub.transform_DateToString(new Date(),"yyyyMMdd");
                    EsBean e = new EsBean(index, "FZJC", null, listJson);
                    if(listJson.size()> 0){
                        Map<String, Object> map = esWrite.add(e);
                        logger.info("map:"+map);
                    }
                    time = System.currentTimeMillis();
                    listJson.clear();
                }
                consumer.commitSync();
            }catch (Exception e){
                logger.error("!!!!!!error");
                logger.debug("",e);
                e.printStackTrace();
            }
        }

    }

}