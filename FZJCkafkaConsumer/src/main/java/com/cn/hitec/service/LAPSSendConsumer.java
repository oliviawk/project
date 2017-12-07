package com.cn.hitec.service;

import com.cn.hitec.bean.EsBean;
import com.cn.hitec.feign.client.EsService;
import net.sf.json.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LAPSSendConsumer {
	private static final Logger logger = LoggerFactory.getLogger(LAPSSendConsumer.class);
    @Autowired
    EsService esService;


    private final KafkaConsumer<String, String> consumer;
    private List<String> list  = new ArrayList<String>();
    private String TOPIC = "SEND";

    @Value("${LAPS.send.target.ips}")
    private String ips;
    @Value("${LAPS.datatype}")
    private String datatypes;

    public LAPSSendConsumer() {

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
		//earliest
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
					List<String> msgs = convert(msg);

					if(msgs.size() > 0){
	                	list.addAll(msgs);

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

    public List<String> convert(String msg){
    	List<String> toEsJsons = new ArrayList<>();

    	Pattern ipspattern = Pattern.compile(ips);
    	Matcher matcher = ipspattern.matcher(msg);
    	if(!matcher.find()){
    		return toEsJsons;
    	}

    	try{

			String[] lines = msg.split("※");
			String date = "";
			String beginTime = "";
			String endTime = "";
			String ip = lines[0];
			String target_ip = "";
			String type = "";
			boolean add = false;

			Pattern typepattern = Pattern.compile("log_tran_(.+)_(\\d{8})");
			Pattern timepattern1 = Pattern
					.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).+(\\d{2}:\\d{2}:\\d{2})");
			Pattern timepattern2 = Pattern
					.compile("(\\d{2}:\\d{2}:\\d{2})");
			Pattern contextpattern = Pattern
					.compile("(.+) transfer completed with status (\\d+)\\. Total time:([0-9|\\.]+) sec\\. File size:(\\d+) bytes\\. File mtime:([0-9|:]+)");

			SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			SimpleDateFormat df2 = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss.SSSZ");
			List<JSONObject> list = new ArrayList<JSONObject>();
			long receive_time = new Date().getTime();
			for (int i = 1; i < lines.length; i++) {
				matcher = typepattern.matcher(lines[i]);
				if (matcher.find()) {
					type = matcher.group(1);
					date = matcher.group(2);

					if(!datatypes.contains(type)){
						return toEsJsons;
					}
				} else {
					matcher = timepattern1.matcher(lines[i]);
					if (matcher.find()) {
						target_ip = matcher.group(1);
						if(ips.contains(target_ip)){
							add = true;
							beginTime = endTime = matcher.group(2);

							Date end = df1.parse(date + endTime);

							if(list.size() > 0){
								for (JSONObject obj : list) {

									obj.getJSONObject("fields").element("end_time", df2.format(end));
									obj.put("occur_time", end.getTime());

									toEsJsons.add(obj.toString());
									logger.info(obj.toString());
								}

								list.clear();

							}
						}
						else{
							add = false;
						}

					} else {
						matcher = contextpattern.matcher(lines[i]);
						if (matcher.find() && add) {

							JSONObject obj = new JSONObject();
							JSONObject subobj = new JSONObject();
							obj.put("receive_time", receive_time);

							subobj.put("start_time", df2.format(df1.parse(date + beginTime)));

							subobj.put("ip_addr", ip);
							subobj.put("ip_addr_target", target_ip);

							subobj.put("module", "分发");

							//MSP3_PMSC_LAPS3KM_ME_L88_CHN_201712060000_00000-00000.GR2
							//MSP3_PMSC_LAPS3KMGEO_T_L88_CHN_201712041500_00000-00000.JPG
							String[] arr = matcher.group(1).split("_");
							obj.put("type", arr[2]);
							obj.put("name", arr[3]);

							SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
							Date d = df.parse(arr[6]);
							df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
							subobj.put("data_time", df.format(d));


							subobj.put("file_name", matcher.group(1));
							subobj.put("event_status", matcher.group(2));
							subobj.put("totalTime", matcher.group(3));
							subobj.put("file_size", matcher.group(4));
							subobj.put("mtime", matcher.group(5));
							if (!matcher.group(2).equals("0")) {
								i++;
								subobj.put("event_info", lines[i]);
							}

							obj.put("fields", subobj);

							list.add(obj);
						}
						else if(lines[i].contains("process end")){
							matcher = timepattern2.matcher(lines[i]);
							if(matcher.find()){
								endTime = matcher.group(1);

								Date end = df1.parse(date + endTime);

								if(list.size() > 0){
									for (JSONObject obj : list) {

										obj.getJSONObject("fields").element("end_time", df2.format(end));
										obj.put("occur_time", end.getTime());

										toEsJsons.add(obj.toString());
										logger.info(obj.toString());
									}

									list.clear();

								}
							}
						}
					}
				}
			}
    	}catch(Exception e){
    		logger.error("!!!!!!error");
        	logger.debug("",e);
        	System.out.println(msg);
            e.printStackTrace();
    	}

    	return toEsJsons;
    }


    public static void main(String[] args) {
        new LAPSSendConsumer().consume();
    }
}