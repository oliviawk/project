package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.EsBean;
import com.cn.hitec.feign.client.DataSourceEsInterface;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class MsgConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MsgConsumer.class);

    @Autowired
    DataSourceEsInterface dataSourceEsInterface;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private final KafkaConsumer<String, String> consumer;
    private List<String> list  = new ArrayList<>();
    private String topic;
    private String type;
    public MsgConsumer(String topic, String group, String type) {
        this.type = type;
        this.topic = topic;
        //*******************bootstrap.servers方式******************//
        Properties props = new Properties();
        // 设置brokerServer(kafka)ip地址
        props.put("bootstrap.servers",
                "10.30.17.173:9092,10.30.17.174:9092,10.30.17.175:9092");
        props.put("group.id", group);

        props.put("enable.auto.commit", "false");

        // 设置使用最开始的offset偏移量为该group.id的最早。如果不设置，则会是latest即该topic最新一个消息的offset
        // 如果采用latest，消费者只能得道其启动后，生产者生产的消息
        props.put("auto.offset.reset", "latest");
        //
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<String, String>(props);

    }

    public void consume() {

        consumer.subscribe(Arrays.asList(topic));
        EsBean esBean = new EsBean();
        esBean.setType(type);
        long startTime = System.currentTimeMillis();
        long useaTime = 0;
        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(1000);
                List<Map<String, Object>> msgs = new ArrayList<Map<String,Object>>();
                for (ConsumerRecord<String, String> record : records) {

                    String msg = record.value();
                    System.out.println(msg);
                    Map<String, Object> data = processing(msg);
//                    msgs.add(data);
//                    if (msgs != null){
//                    	System.out.println(msgs.toString());
//                    	Map<String, Object> backData = dataSourceEsInterface.insertDataSource(JSON.toJSONString(msgs));
//                    	System.out.println(backData.toString());
//                    }
//                    if(msgs != null && msgs.size() > 0) {
//                        list.addAll(msgs);
//                    }
                }
                
//                useaTime = System.currentTimeMillis() - startTime;
//                //当list数据量，大于100 ， 或者存储时间超过5秒 ， 调用入ES接口一次
//                if (list.size() > 5000 || (list.size() > 0 && useaTime > 5000)) {
//                    esBean.setData(list);
//                    String responst = esService.add(esBean);
//                    System.out.println(responst+"--耗时："+useaTime);
//                    startTime = System.currentTimeMillis();
//                    list.clear();
//                }
                consumer.commitSync();

            }catch (Exception e){
                logger.error("!!!!!!error");
                logger.debug("",e);
                e.printStackTrace();
            }
        }

    }

    public Map<String, Object> processing (String msg) {
        return null;
    }

}