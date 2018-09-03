package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.bean.EsBean;
import com.cn.hitec.feign.client.EsService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MsgConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MsgConsumer.class);
    @Autowired
    EsService esService;

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

        List<String> list_mqpf_220  = new ArrayList<>();
        long startTime_mqpf220 = System.currentTimeMillis();
        long useaTime_mqpf220 = 0;

        while (true) {
            try {

                ConsumerRecords<String, String> records = consumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {

                    String msg = record.value();

                    List<String> msgs = processing(msg);

                    if(msgs != null && msgs.size() > 0) {
                        if ("MQPF_AC".equals(topic)){
                            try {
                                JSONObject jsonObject = JSONObject.parseObject(msgs.get(0));
                                if (jsonObject.containsKey("name") && "雷达基数据".equals(jsonObject.getString("name"))){
                                    list_mqpf_220.addAll(msgs);
                                }else {
                                    list.addAll(msgs);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            list.addAll(msgs);
                        }
                    }

                }
                useaTime = System.currentTimeMillis() - startTime;
                //当list数据量，大于2000 ， 或者存储时间超过5秒 ， 调用入ES接口一次
                if (list.size() > 1000 || (list.size() > 0 && useaTime > 5000)) {
                    esBean.setData(list);
                    String responst = esService.add(esBean);
                    logger.info(type+"数据入库信息1："+responst);

                    startTime = System.currentTimeMillis();
                    list.clear();
                }
                useaTime_mqpf220 = System.currentTimeMillis() - startTime_mqpf220;
                if (list_mqpf_220.size() > 1000 || (list_mqpf_220.size() > 0 && useaTime_mqpf220 > 5000)) {
                    EsBean esBean_mqpf220 = new EsBean();
                    esBean_mqpf220.setType(type);
                    esBean_mqpf220.setData(list_mqpf_220);
                    String responst = esService.update_mqpf220(esBean_mqpf220);
                    logger.info(type+"数据入库信息2："+responst);

                    startTime_mqpf220 = System.currentTimeMillis();
                    list_mqpf_220.clear();
                }

            }catch (Exception e){
                logger.error("!!!!!!error");
                logger.debug("",e);
                e.printStackTrace();
            }finally {
                consumer.commitSync();
            }
        }

    }

    public List<String> processing (String msg) throws ParseException {

        return null;
    }


    public void getHistory() throws ParseException {
        String startTime = "2018-08-22 10:00:00";//起始时间点
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long stime = sdf.parse(startTime).getTime();
//存放主题分区信息
        List<PartitionInfo> partitionSet = consumer.partitionsFor(topic);
        Map<TopicPartition, Long> offsetMapAll  = new HashMap<TopicPartition, Long>();
        for (PartitionInfo partitionInfo : partitionSet) {
            //存放主题信息和时间戳
            Map<TopicPartition, Long> startmap = new HashMap<TopicPartition, Long>();
            TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
            startmap.put(topicPartition, stime);
            //给consumer指定分区
            consumer.assign(Arrays.asList(topicPartition));
            //获得该分区的起始偏移量
            Map<TopicPartition, OffsetAndTimestamp> offsetMap = consumer.offsetsForTimes(startmap);
            if(offsetMap.get(topicPartition)==null) {
                continue;
            }
            offsetMapAll.put(topicPartition, offsetMap.get(topicPartition).offset());
        }
        if(offsetMapAll.size()>0) {
            consumer.assign(offsetMapAll.keySet());
            for(TopicPartition tp : offsetMapAll.keySet()){
                consumer.seek(tp, offsetMapAll.get(tp));//设置分区的起始偏移量
            }
            int cnt = 0;
            //轮询读取消息
            while (true) {

                ConsumerRecords<String, String> records = consumer.poll(1000);

                for (ConsumerRecord<String, String> record : records){
//                  TimeUnit.SECONDS.sleep(1);
                    System.out.printf("offset = %d, key = %s, partition = %s%n", record.offset(), record.key(), record.partition());
                    logger.error( record.offset()+"\t"+ record.key()+"\t"+record.partition());
                    cnt ++;
                }
                System.out.println("总数:" + cnt);
            }
        }
    }

}