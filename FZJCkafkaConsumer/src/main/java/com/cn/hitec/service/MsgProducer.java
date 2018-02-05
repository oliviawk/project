package com.cn.hitec.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public enum MsgProducer {
    INSTANCE;
    private Producer instance;
    MsgProducer() {
        Properties props = new Properties();

        // 此处配置的是kafka的broker地址:端口列表
        props.put("bootstrap.servers", "10.30.17.173:9092,10.30.17.174:9092,10.30.17.175:9092");

        //配置value的序列化类
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        //配置key的序列化类
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        /**
         * 0表示不等待结果返回<br/>
         * 1表示等待至少有一个服务器返回数据接收标识<br/>
         * -1表示必须接收到所有的服务器返回标识，及同步写入<br/>
         * */
        props.put("request.required.acks", "0");
        /**
         * 内部发送数据是异步还是同步
         * sync：同步, 默认
         * async：异步
         */
        props.put("producer.type", "async");
        // 异步提交的时候(async)，并发提交的记录数
        props.put("batch.num.messages", "200");

        instance = new KafkaProducer<String, String>(props);
    }
    public void sendMsg(String topic,String msg) {
        instance.send(new ProducerRecord(topic, msg ));
        System.out.println(msg);
    }
}

