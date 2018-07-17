//package com.cn.hitec.service;
//
//import com.alibaba.fastjson.JSON;
//import com.cn.hitec.bean.DataSourceSetting;
//import com.cn.hitec.bean.EsBean;
//import com.cn.hitec.feign.client.DataSourceEsInterface;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Service
//public class FZJC_T639_Consumer {
//    private static final Logger logger = LoggerFactory.getLogger(FZJC_T639_Consumer.class);
//
//    @Autowired
//    DataSourceEsInterface dataSourceEsInterface;
//    @Value("${es.indexHeader}")
//	public String indexHeader;
//
//
//    private final KafkaConsumer<String, String> consumer;
//  // private List<Map<String, Object>> msgs = new ArrayList<Map<String,Object>>();
//    private List<String> msgs = new ArrayList<String>();
//    private final String topic = "XFERLOG_T639";
//    private final String type ="";
//    private final String group = "xferlog_t639_1";
//
//    public FZJC_T639_Consumer() {
//        //*******************bootstrap.servers方式******************//
//        Properties props = new Properties();
//        // 设置brokerServer(kafka)ip地址
//        props.put("bootstrap.servers",
//                "10.30.17.173:9092,10.30.17.174:9092,10.30.17.175:9092");
//        props.put("group.id", group);
//
//        props.put("enable.auto.commit", "false");
//
//        // 设置使用最开始的offset偏移量为该group.id的最早。如果不设置，则会是latest即该topic最新一个消息的offset
//        // 如果采用latest，消费者只能得道其启动后，生产者生产的消息
//        props.put("auto.offset.reset", "latest");
//        //
//        props.put("session.timeout.ms", "30000");
//        props.put("key.deserializer",
//                "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("value.deserializer",
//                "org.apache.kafka.common.serialization.StringDeserializer");
//
//        consumer = new KafkaConsumer<String, String>(props);
//
//    }
//
//	public void consume() {
//
//        consumer.subscribe(Arrays.asList(topic));
//
//        EsBean esBean_t639 = new EsBean();
//        esBean_t639.setType("MQPF");
//        esBean_t639.setIndex("");
//
//        long startTime1 = System.currentTimeMillis();
//        long useaTime1 = 0;
//        while (true) {
//            try {
//                ConsumerRecords<String, String> records = consumer.poll(1000);
//
//                for (ConsumerRecord<String, String> record : records) {
//
//                    String msg = record.value();
//                    Map<String, Object> data = processing(msg);
//                    if (data == null){
//                        continue;
//                    }
//                    msgs.add(JSON.toJSONString(data);
//                }
//                useaTime1 = System.currentTimeMillis() - startTime1;
//
//                //当list数据量，大于100 ， 或者存储时间超过5秒 ， 调用入ES接口一次
//                if (msgs.size() > 3000 || (msgs.size() > 0 && useaTime1 > 5000)) {
//                    esBean_t639.setData(msgs);
//
//                    logger.info("FZJC_T639入库数据："+ JSON.toJSONString(msgs));
////                    Map<String, Object> insertDataSource = dataSourceEsInterface.update(esBean);
//////                	System.out.println(JSON.toJSONString(insertDataSource));
////                    logger.info("入库数据返回结果："+ JSON.toJSONString(insertDataSource));
//                    msgs.clear();
//                    startTime1 = System.currentTimeMillis();
//                }
//                consumer.commitSync();
//
//            }catch (Exception e){
//                logger.error("!!!!!!error");
//                logger.debug("",e);
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    /**
//     * 解析数据拼成入库所需格式
//     * 数据源为：Sun Feb 25 03:32:06 2018 1 10.1.72.45 8070 /bin/Z_RADR_I_Z9857_20180224192248_P_DOR_CD_R_10_230_5.857.bin.tmp b _ i r nmic_provider ftp 0 * c
//     * 共141位
//     */
//    public Map<String, Object> processing (String msg) {
//        Map<String, Object> data = new HashMap<String, Object>();
//        Map<String, Object> field = new HashMap<String, Object>();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
////        msg = "Tue Jul  3 00:15:55 2018 1 10.20.49.131 53313806 /2018070212/Z_NAFP_C_BABJ_20180702120000_P_CNPC-T639-GMFS-HNEHE-00600.grib2 b _ i r cwfs ftp 0 * c 10.14.83.63";
//        String[] msgs = msg.split(" ");
//        //按空格划分, 会有多余空格项, 要去除
//        List<String> localMsgs=Arrays.asList(msgs);//将数组转换为list集合
//        if(localMsgs.contains("")){//加入集合中包含这个元素
//            //这个时候我们直接移除会报错,所以我们要转换为Arraylist
//            //list.remove("张三");
//            List<String> changeMsgs=new ArrayList<String>(localMsgs);//转换为ArrayLsit调用相关的remove方法
//            changeMsgs.remove("");
//            for (int i = 0; i < changeMsgs.size(); i++) {
//                msgs[i] = changeMsgs.get(i);
//            }
//        }
//        // 不是这个用户发的不需要
//        String user = msgs[13];
//        if ("upload".equals(user)){
//            return null;
//        }
//        String file_name = msgs[8];
//        if (file_name.indexOf("T639-GMFS-HNEHE") == -1){
//            return null;
//        }
//        logger.info("筛选后："+msg);
////        msg = "Tue Jul  3 00:15:55 2018 1 10.20.49.131 53313806 /2018070212/Z_NAFP_C_BABJ_20180702120000_P_CNPC-T639-GMFS-HNEHE-00600.grib2 b _ i r cwfs ftp 0 * c 10.14.83.63";
//        String file_sizeStr = msgs[7];
//        String event_status = msgs[15];
//        String ipAddr = msgs[18];
//
//        //成功匹配到，入库
//        try {
//            String occurTimeStr = "";
//            for (int j = 0; j < 5; j++) {
//                if (j < 4){
//                    occurTimeStr += msgs[j] + " ";
//                }
//                if (j == 4){
//                    occurTimeStr += msgs[j];
//                }
//            }
//            Date date = new Date(occurTimeStr);
//            long occur_time = date.getTime();
////			String occur_time = sdf.format(date);
//            data.put("occur_time", occur_time);
//
////            String timeFormat = dataSourceSetting.getTimeFormat();
////            String dataTimeStr = timeStr.substring(0, timeFormat.length());
////            sdf.applyPattern(timeFormat);
////            Date dataTime = sdf.parse(dataTimeStr);
////            sdf.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
////            String data_time = sdf.format(dataTime);
//
//            long file_size_long = Long.parseLong(file_sizeStr);
//
//            // 采集数据中不包含的数据，后期从配置库中获取
//            data.put("should_time", 0);
//            data.put("last_time", 0);
//            data.put("name", "T639采集");
//            data.put("type", "T639");
//
//            field.put("file_name", file_name);
//            field.put("file_size", file_size_long);
//            field.put("data_time", data_time);
//            field.put("event_status", event_status);
//            field.put("ip_addr", ipAddr);
//            field.put("module", "采集");
//            data.put("fields", field);
//        } catch (ParseException e) {
//            logger.error("解析日志并组装入库数据出错");
//            e.printStackTrace();
//        }
//
//        return data;
//    }
//
//}