package com.cn.hitec.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LAPS_WSConsumer extends MsgConsumer {
	private static final Logger logger = LoggerFactory.getLogger(LAPS_WSConsumer.class);
	private static String topic = "WS";
	private static String type = "LAPS";

    public LAPS_WSConsumer(@Value("${LAPS.group.id}")String group) {
		super(topic,group,type);
	}

	@Override
    public List<String> processing (String msg){
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

			String data_time = obj.getString("end_time");
			obj.put("data_time", data_time.substring(0,data_time.indexOf(":")) + ":00:00.000+0800");

			newJson.put("occur_time", end.getTime());
			newJson.put("receive_time", new Date().getTime());
			
			obj.remove("type");
			obj.remove("sub_type");
	    	newJson.put("fields", obj);
			logger.info(newJson.toString());
			List<String> list = new ArrayList();
			list.add(newJson.toString());
			return list;
		}
		else{
			return null;
		}
    	
    }

}