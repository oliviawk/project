//package com.cn.hitec.service;
//
//import com.cn.hitec.bean.EsBean;
//import com.cn.hitec.feign.client.EsService;
//import kafka.consumer.ConsumerConfig;
//import kafka.consumer.ConsumerIterator;
//import kafka.consumer.KafkaStream;
//import kafka.javaapi.consumer.ConsumerConnector;
//import kafka.serializer.StringDecoder;
//import kafka.utils.VerifiableProperties;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class KafkaConsumerTest {
//
//    @Autowired
//    EsService esService;
//
////    @Value("${kafka.zookeeper.connect}")
////    public String zookeeper_connect;
////    @Value("${kafka.group.id}")
////    public String group_id;
////    @Value("${kafka.topic}")
////    public String TOPIC;
////    @Value("${kafka.zookeeper.session.timeout.ms}")
////    public String zookeeper_session_timeout_ms;
////    @Value("${kafka.zookeeper.sync.time.ms}")
////    public String zookeeper_sync_time_ms;
////    @Value("${kafka.auto.commit.interal.ms}")
////    public String auto_commit_interal_ms;
////    @Value("${kafka.auto.offset.rest}")
////    public String auto_offset_rest;
////    @Value("${kafka.serializer.class}")
////    public String serializer_class;
//
//    private final ConsumerConnector consumer;
//    private List<String> list  = new ArrayList<>();
//    private String TOPIC = "FZJC";
//    public KafkaConsumerTest() {
//        Properties props = new Properties();
//        //zookeeper 配置
////        props.put("zookeeper.connect", zookeeper_connect);
////
////        //group 代表一个消费组
////        props.put("group.id", group_id);
////
////        //zk连接超时
////        props.put("zookeeper.session.timeout.ms", zookeeper_session_timeout_ms);
////        //指定多久消费者更新offset到zookeeper中。注意offset更新时基于time而不是每次获得的消息。一旦在更新zookeeper发生异常并重启，将可能拿到已拿到过的消息
////        props.put("zookeeper.sync.time.ms", zookeeper_sync_time_ms);
////        //自动更新时间。默认60 * 1000
////        props.put("auto.commit.interval.ms", auto_commit_interal_ms);
////
////        //如果zookeeper没有offset值或offset值超出范围。那么就给个初始的offset。有smallest、largest、
////        //anything可选，分别表示给当前最小的offset、当前最大的offset、抛异常。默认largest
////        props.put("auto.offset.reset", auto_offset_rest);
////        //序列化类
////        props.put("serializer.class", serializer_class);
//
//        //zookeeper 配置
//        props.put("zookeeper.connect", "10.30.17.173:2181,10.30.17.174:2181,10.30.17.175:2181");
//
//        //group 代表一个消费组
//        props.put("group.id", "0");
//
//        //zk连接超时
//        props.put("zookeeper.session.timeout.ms", "4000");
//        //指定多久消费者更新offset到zookeeper中。注意offset更新时基于time而不是每次获得的消息。一旦在更新zookeeper发生异常并重启，将可能拿到已拿到过的消息
//        props.put("zookeeper.sync.time.ms", "200");
//        //自动更新时间。默认60 * 1000
//        props.put("auto.commit.interval.ms", "1000");
//
//        //如果zookeeper没有offset值或offset值超出范围。那么就给个初始的offset。有smallest、largest、
//        //anything可选，分别表示给当前最小的offset、当前最大的offset、抛异常。默认largest
//        props.put("auto.offset.reset", "smallest");
//        //序列化类
//        props.put("serializer.class", "kafka.serializer.StringEncoder");
//
//        ConsumerConfig config = new ConsumerConfig(props);
//
//        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);
//    }
//
//}