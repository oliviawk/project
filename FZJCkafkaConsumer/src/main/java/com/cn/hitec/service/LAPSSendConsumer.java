package com.cn.hitec.service;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LAPSSendConsumer extends MsgConsumer {
	private static final Logger logger = LoggerFactory.getLogger(LAPSSendConsumer.class);
    private static String topic = "SEND";
	private static String group;
	private static String type = "LAPS";

	static{
		ResourceBundle bundle = ResourceBundle.getBundle("application");
		group = bundle.getString("LAPS.group.id");
	}

    @Value("${LAPS.send.target.ips}")
    private String ips;
    @Value("${LAPS.datatype}")
    private String datatypes;

    public LAPSSendConsumer() {
		super(topic,group,type);
    }

	@Override
    public List<String> processing(String msg){
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
							obj.put("type", arr[2] + "_" + arr[3]);
//							obj.put("name", arr[3]);

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

}