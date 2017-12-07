package com.cn.hitec.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import net.sf.json.JSONObject;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.hitec.bean.EsBean;
import com.cn.hitec.feign.client.EsService;

@Service
public class LAPS_WSConsumer {
	private static final Logger logger = LoggerFactory.getLogger(LAPS_WSConsumer.class);
    @Autowired
    EsService esService;


    private final KafkaConsumer<String, String> consumer;
    private List<String> list  = new ArrayList<>();
    private String TOPIC = "WS";
    public LAPS_WSConsumer() {
    	
    	//*******************bootstrap.servers方式******************//
    	Properties props = new Properties();
		// 设置brokerServer(kafka)ip地址
		props.put("bootstrap.servers",
				"10.30.17.173:9092,10.30.17.174:9092,10.30.17.175:9092");
		// 设置consumer group name
		ResourceBundle bundle = ResourceBundle.getBundle("application");
		String group = bundle.getString("LAPS.group.id");
		props.put("group.id", group);

		props.put("enable.auto.commit", "false");

		// 设置使用最开始的offset偏移量为该group.id的最早。如果不设置，则会是latest即该topic最新一个消息的offset
		// 如果采用latest，消费者只能得道其启动后，生产者生产的消息
		props.put("auto.offset.reset", "earliest");
		//
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer",
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer",
				"org.apache.kafka.common.serialization.StringDeserializer");

		consumer = new KafkaConsumer<String, String>(props);
		
    }

    public void consume() {
    	
    	consumer.subscribe(Arrays.asList(TOPIC));
    	EsBean esBean = new EsBean();
        esBean.setType("LAPS");
        long startTime = System.currentTimeMillis();
        long useaTime = 0;
		while (true) {
			try {
				ConsumerRecords<String, String> records = consumer.poll(100);
				for (ConsumerRecord<String, String> record : records) {
					
					String msg = record.value();

					try{
						msg = processing(msg);
					}catch (Exception e){
		            	logger.error("!!!!!!error[msg:" + msg + "]");
		            	logger.debug("",e);
		                e.printStackTrace();
		            }
	                if(msg != null){
						logger.info(msg);
	                	list.add(msg);

		                useaTime = System.currentTimeMillis() - startTime;
		                //当list数据量，大于100 ， 或者存储时间超过5秒 ， 调用入ES接口一次
		                if (list.size() > 100 || (list.size() > 0 &&  useaTime > 5000)){
		                	esBean.setData(list);
		                    String responst = esService.add(esBean);
		                    System.out.println(responst);
		                    startTime = System.currentTimeMillis();
		                    list.clear();
		                }
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
    
    public String processing (String msg){
    	JSONObject obj = JSONObject.fromObject(msg);
		
		if(obj.getString("sub_type").toLowerCase().startsWith("laps")){
			
			JSONObject newJson = new JSONObject();
			newJson.put("type", obj.getString("sub_type"));
			
			
			newJson.put("name", "");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
			Date end = null;
			try {
				end = df.parse(obj.getString("end_time"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newJson.put("occur_time", end.getTime());
			newJson.put("receive_time", new Date().getTime());
			
			obj.remove("type");
			obj.remove("sub_type");
	    	newJson.put("fields", obj);
	    	
	    	return newJson.toString();
		}
		else{
			return null;
		}
    	
    }

    public static void main(String[] args) {
        new LAPS_WSConsumer().consume();
    }
}