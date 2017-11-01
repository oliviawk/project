package com.cn.hitec.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消费完一个分区后手动提交偏移量
 * @author 陈正廷
 *
 */
public class ManualCommitPartion {
    private static Logger logger = LoggerFactory.getLogger(ManualCommitPartion.class);
    public ManualCommitPartion() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Properties props = new Properties();
        //设置brokerServer(kafka)ip地址
        props.put("bootstrap.servers", "10.30.17.173:9092,10.30.17.174:9092,10.30.17.175:9092");
        //设置consumer group name
        props.put("group.id","999");

        props.put("enable.auto.commit", "false");

        //设置使用最开始的offset偏移量为该group.id的最早。如果不设置，则会是latest即该topic最新一个消息的offset
        //如果采用latest，消费者只能得道其启动后，生产者生产的消息
        props.put("auto.offset.reset", "earliest");
        //
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String ,String> consumer = new KafkaConsumer<String ,String>(props);
        consumer.subscribe(Arrays.asList("FZJC"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
            for (TopicPartition partition : records.partitions()) {
                List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
                for (ConsumerRecord<String, String> record : partitionRecords) {
                    logger.info("now consumer the message it's offset is :"+record.offset() + " and the value is :" + record.value());
                }
                long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
//                logger.info("now commit the partition[ "+partition.partition()+"] offset");
                consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
            }
        }
    }

}
