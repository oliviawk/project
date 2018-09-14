package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.DataSourceSetting;
import com.cn.hitec.bean.EsBean;
import com.cn.hitec.feign.client.DataSourceEsInterface;
import com.cn.hitec.repository.DataSourceSettingRepository;

import com.cn.hitec.repository.User_Catalog_Repository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

public class MsgConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MsgConsumer.class);

    @Autowired
    DataSourceEsInterface dataSourceEsInterface;
    @Autowired
    DataSourceSettingRepository dataSourceSettingRepository;
    @Autowired
    User_Catalog_Repository user_catalog_repository;
    @Value("${es.indexHeader}")
	public String indexHeader;

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

    public List<DataSourceSetting> insertBaseFilter = new ArrayList<DataSourceSetting>();
    
    private final KafkaConsumer<String, String> consumer;

    private String topic;
    private String type;
    public MsgConsumer(String topic, String group, String type) {
        this.type = type;
        this.topic = topic;
        logger.info("groupId:{}",group);
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

        consumer = new KafkaConsumer<String, String>(props);

    }

	public void consume() {


        consumer.subscribe(Arrays.asList(topic));

        long startTime1 = System.currentTimeMillis();
        long startTime2 = System.currentTimeMillis();
        long startTime3 = System.currentTimeMillis();
        long startTime_fzjc = System.currentTimeMillis();
        long useaTime1 = 0;
        long useaTime2 = 0;
        long useaTime3 = 0;
        long useaTime_fzjc = 0;
        List<String> msgs = new ArrayList<String>();
        List<Object> possibleNeedDataList = new ArrayList<Object>();
        List<String> mqpfList = new ArrayList<String>();
        List<String> FZJCList = new ArrayList<String>();
        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(1000);
                if (records.isEmpty()){
                    continue;
                }
                insertBaseFilter = dataSourceSettingRepository.findAll();
                for (ConsumerRecord<String, String> record : records) {

                    String msg = record.value();
                    Map<String, Object> data = processing(msg);
                    if (data == null){
                        continue;
                    }
                    if ("dataSource".equals(data.get("type"))){
                        Map<String,Object> mapdata=(Map<String, Object>) data.get("data");
                        Map<String,Object> fieldmap=(Map<String, Object>) mapdata.get("fields");
                        String filename=(String)fieldmap.get("file_name");
                        logger.info("文件名："+filename);
                        mapdata.put("type",filename);
                        msgs.add(JSON.toJSONString(mapdata));
                    }else if ("noDataSource".equals(data.get("type"))){
                        possibleNeedDataList.add(data.get("data"));
                    }else if ("MQPF_DataSource".equals(data.get("type"))){
                        mqpfList.add(JSON.toJSONString(data.get("data")));
                    }else if("FZJC_DataSource".equals(data.get("type"))){
                        FZJCList.add(JSON.toJSONString(data.get("data")));
                    }
                }
                useaTime1 = System.currentTimeMillis() - startTime1;
                useaTime2 = System.currentTimeMillis() - startTime2;
                useaTime3 = System.currentTimeMillis() - startTime3;
                useaTime_fzjc = System.currentTimeMillis() - startTime_fzjc;
                //当list数据量，大于100 ， 或者存储时间超过5秒 ， 调用入ES接口一次
                if (msgs.size() > 3000 || (msgs.size() > 0 && useaTime1 > 5000)) {
                    EsBean esBean = new EsBean();
                    esBean.setType("DATASOURCE");
                    esBean.setIndex("");
                    esBean.setData(msgs);
//                    logger.info("入库数据："+ JSON.toJSONString(msgs));
                    Map<String, Object> insertDataSource = dataSourceEsInterface.update(esBean);
//                	System.out.println(JSON.toJSONString(insertDataSource));
                    logger.info("入库数据返回结果："+ JSON.toJSONString(insertDataSource));
                    msgs.clear();
                    startTime1 = System.currentTimeMillis();
                }
                if (possibleNeedDataList.size() > 3000 || (possibleNeedDataList.size() > 0 && useaTime2 > 5000) ){
                    Map<String, Object> possibleNeedData = new HashMap<String, Object>();
                 //  logger.info("可能需要的数据"+JSON.toJSONString(possibleNeedData));
                    possibleNeedData.put("_index", indexHeader +"possible_needed_data");
                    possibleNeedData.put("_type", "POSSIBLE_NEEDED_DATA");
                    possibleNeedData.put("_data", possibleNeedDataList);

//                    logger.info("可能需要数据入库："+JSON.toJSONString(possibleNeedDataList));
                    Map<String, Object> insertDataSource = dataSourceEsInterface.insertList(JSON.toJSONString(possibleNeedData));
//            		System.out.println(insertDataSource);
                    logger.info("可能需要数据返回结果："+ JSON.toJSONString(insertDataSource));
                    possibleNeedDataList.clear();
                    startTime2 = System.currentTimeMillis();
                }
                if(mqpfList.size() > 3000 || (mqpfList.size() > 0 && useaTime3 > 5000)){
                    EsBean esBean_mqpf = new EsBean();
                    esBean_mqpf.setType("MQPF");
                    esBean_mqpf.setIndex("");
                    esBean_mqpf.setData(mqpfList);
                    //执行入库
                    //
                    Map<String, Object> insertDataSource = dataSourceEsInterface.insertMQPFData(esBean_mqpf);
//                	System.out.println(JSON.toJSONString(insertDataSource));
                    logger.info("入库MQPF数据返回结果："+ JSON.toJSONString(insertDataSource));
                    mqpfList.clear();
                    startTime3 = System.currentTimeMillis();
                }

                if (FZJCList.size() > 3000 || (FZJCList.size() > 0 && useaTime_fzjc > 5000)){
                    EsBean esBean_fzjc = new EsBean();
                    esBean_fzjc.setType("FZJC");
                    esBean_fzjc.setIndex("");
                    //开始入库

                    FZJCList.clear();
                    startTime_fzjc = System.currentTimeMillis();
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

    public Map<String, Object> processing (String msg) {
        return null;
    }
    
//    /**
//    * @Description 查询配置库给全局变量赋值 TODO <pre>
//    * @author HuYiWu <pre>
//    * @date 2018年4月11日 下午3:07:04 <pre>
//     */
//    public void updateInsertBaseFilter(){
////    	List<DataSourceSetting> dataSourceSettings = dataSourceSettingRepository.findAll();
////    	System.out.println("查询数据库更新对比map:"+ JSON.toJSONString(dataSourceSettings));
////    	for (int i = 0; i < dataSourceSettings.size(); i++) {
////    		DataSourceSetting dataSourceSetting = dataSourceSettings.get(i);
////    		String key = dataSourceSetting.getIpAddr() +":"+ dataSourceSetting.getSendUser()
////    				+":"+ dataSourceSetting.getFileName();
////    		List<DataSourceSetting> value = insertBaseFilter.get(key);
////    		if (value == null){
////    			value = new ArrayList<DataSourceSetting>();
////    			value.add(dataSourceSetting);
////    		}else{
////    			value.add(dataSourceSetting);
////    		}
////    		insertBaseFilter.put(key, value);
////		}
//    	insertBaseFilter = dataSourceSettingRepository.findAll();
//    }

}