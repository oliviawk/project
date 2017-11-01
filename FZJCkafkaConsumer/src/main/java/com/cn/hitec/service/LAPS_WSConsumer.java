package com.cn.hitec.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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
    private String GROUP = "0";
    public LAPS_WSConsumer() {
        /*Properties props = new Properties();
        //zookeeper 配置
        props.put("bootstrap.servers", "10.30.17.173:9092,10.30.17.174:9092,10.30.17.175:9092");

        //group 代表一个消费组
        props.put("group.id", "0");

        //zk连接超时
        props.put("zookeeper.session.timeout.ms", "4000");
        //指定多久消费者更新offset到zookeeper中。注意offset更新时基于time而不是每次获得的消息。一旦在更新zookeeper发生异常并重启，将可能拿到已拿到过的消息
        props.put("zookeeper.sync.time.ms", "200");
        //自动更新时间。默认60 * 1000
        props.put("auto.commit.interval.ms", "1000");

        //如果zookeeper没有offset值或offset值超出范围。那么就给个初始的offset。有smallest、largest、
        //anything可选，分别表示给当前最小的offset、当前最大的offset、抛异常。默认largest
        props.put("auto.offset.reset", "smallest");
        //序列化类
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        
        ConsumerConfig config = new ConsumerConfig(props);

        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);
        
        *
        */
    	
    	//*******************bootstrap.servers方式******************//
    	Properties props = new Properties();
		// 设置brokerServer(kafka)ip地址
		props.put("bootstrap.servers",
				"10.30.17.173:9092,10.30.17.174:9092,10.30.17.175:9092");
		// 设置consumer group name
		props.put("group.id", GROUP);

		props.put("enable.auto.commit", "false");

		// 设置使用最开始的offset偏移量为该group.id的最早。如果不设置，则会是latest即该topic最新一个消息的offset
		// 如果采用latest，消费者只能得道其启动后，生产者生产的消息
//		props.put("auto.offset.reset", "latest");
		//
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer",
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer",
				"org.apache.kafka.common.serialization.StringDeserializer");

		consumer = new KafkaConsumer<String, String>(props);
		
    }

    public void consume() {

        /*Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(TOPIC, new Integer(1));

        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

        Map<String, List<KafkaStream<String, String>>> consumerMap =
                consumer.createMessageStreams(topicCountMap,keyDecoder,valueDecoder);
        KafkaStream<String, String> stream = consumerMap.get(TOPIC).get(0);
        ConsumerIterator<String, String> it = stream.iterator();
        long startTime = System.currentTimeMillis();
        long useaTime = 0;
//
        EsBean esBean = new EsBean();
        esBean.setType(TOPIC);
        while (it.hasNext()){
        	String msg = it.next().message();
            try {
                msg = processing(msg);
                System.out.println(msg);
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
            } catch (Exception e){
            	logger.error("!!!!!!error[msg:" + msg + "]");
            	logger.debug("",e);
                e.printStackTrace();
            }
        }*/
    	
    	
    	consumer.subscribe(Arrays.asList(TOPIC));
    	EsBean esBean = new EsBean();
        esBean.setType("LAPS");
        long startTime = System.currentTimeMillis();
        long useaTime = 0;
        int i = 0;
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
					i++;
					System.out.println(msg);
	                if(msg != null){
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