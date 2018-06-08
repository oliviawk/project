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
public class FZJCWorkingConsumer extends MsgConsumer {
	private static final Logger logger = LoggerFactory.getLogger(FZJCWorkingConsumer.class);
    private static String topic = "FZJC";
	private static String type = "FZJC";

    public FZJCWorkingConsumer(@Value("${FZJC.group.id}")String group) {
		super(topic,group,type);
    }

	@Override
	public List<String> processing (String msg){
    	JSONObject obj = JSONObject.fromObject(msg);
		
		if(topic.equals(obj.getString("type"))){
			
			JSONObject newJson = new JSONObject();
			String type = obj.getString("sub_type");
			newJson.put("type", type);
			
			newJson.put("name", "");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
			Date end = null;
			try {
				end = df.parse(obj.getString("end_time"));
				
				if("ReadFY2NC".equals(type)){
					Date d = df.parse(obj.getString("data_time"));
					d.setHours(d.getHours()-1);
					d.setMinutes(0);
					d.setSeconds(0);
					obj.put("data_time", df.format(d));
				}

				if("炎热指数".equals(type)){
					String time = obj.getString("data_time");
					obj.put("data_time", time.substring(0,time.indexOf(":")) + ":00:00.000+0800");
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newJson.put("occur_time", end.getTime());
			newJson.put("receive_time", new Date().getTime());
			
			obj.remove("type");
			obj.remove("sub_type");
	    	newJson.put("fields", obj);

	    	List<String> list = new ArrayList();
	    	list.add(newJson.toString());
	    	return list;
		}
		else{
			return null;
		}
    	
    }
}