package com.cn.hitec.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.feign.client.DataSourceEsInterface;
import com.cn.hitec.tool.CronPub;

@Service
public class DataSourceSendConsumer extends MsgConsumer{

	private static final Logger logger = LoggerFactory.getLogger(DataSourceSendConsumer.class);
    private static String topic = "XFER_LOG";
	
    @Autowired
    DataSourceEsInterface dataSourceEsInterface;
    
	public DataSourceSendConsumer() {
		super(topic, "1234", null);
	}
	
	
	/**
	 * 解析数据拼成入库所需格式
	 * 数据源为：Sun Feb 25 03:32:06 2018 1 10.1.72.45 8070 /bin/Z_RADR_I_Z9857_20180224192248_P_DOR_CD_R_10_230_5.857.bin.tmp b _ i r nmic_provider ftp 0 * c
	 * 共141位
	 */
	@SuppressWarnings("deprecation")
	public Map<String, Object> processing (String msg) {
		List<Map<String, Object>> outList = new ArrayList<Map<String,Object>>();
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> field = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        msg = "Sun Mar 30 22:00:19 2018 1 11.10.31.98 200 /forecast/SEVP_NMC_RFFC_SCON_EME_ACHN_LNO_P9_20180330220016812.TXT.tmp b _ i r nmc_provider ftp 0 * c";
        
        String[] msgs = msg.split(" ");
        
        // 不是这个用户发的不需要
        String user = msgs[13];
        if (!"nmc_provider".equals(user)){
			return null;
		}
        
        String file_name = msgs[8];
        if (file_name.indexOf("SEVP_NMC_RFFC_SCON_EME_ACHN_LNO_P9") == -1 ){
        	return null;
        }
        field.put("file_name", file_name);
        
		String occurTimeStr = "";
		for (int i = 0; i < msgs.length; i++) {
			if (i < 4){
				occurTimeStr += msgs[i] + " ";
			}
			if (i == 4){
				occurTimeStr += msgs[i];
			}
		}
		Date date = new Date(occurTimeStr);
		String occur_time = sdf.format(date);
		data.put("occur_time", occur_time);
		
		String ip_addr = msgs[6];
		field.put("ip_addr", ip_addr);
		
		String file_sizeStr = msgs[7];
		long file_size;
		try {
			file_size = Long.parseLong(file_sizeStr);
		} catch (NumberFormatException e1) {
			logger.error("解析文件转换大小为long报错"+ e1.toString());
			return null;
		}
		field.put("file_size", file_size);
		
		String[] fileNameSplit = file_name.split("_");
		String format = "yyyyMMddHHmm";
		String dataTimeStr = fileNameSplit[fileNameSplit.length -1].substring(0, format.length());
		sdf.applyPattern(format);
		Date dataTime = new Date();
		try {
			dataTime = sdf.parse(dataTimeStr);
		} catch (ParseException e) {
			logger.error("解析时次出错"+ e.toString());
			return null;
		}
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		String data_time = sdf.format(dataTime);
		field.put("data_time", data_time);
		
		String event_status = msgs[15];
		field.put("event_status", event_status);
		
		// 采集数据中不包含的数据，后期从配置库中获取
		data.put("should_time", 0);
		data.put("last_time", 0);
		data.put("name", "国内精细化城镇预报");
		data.put("type", "SEVP_NMC_RFFC_SCON_EME_ACHN_LNO_P9");
		
		field.put("data_type", "预报产品");
		field.put("department_name", "气象中心");
		field.put("phone", "5584");
		field.put("use_department", "全媒体气象产品室");
		field.put("system_name", "");
		field.put("module", "DS");
		
		data.put("fields", field);
		
		outList.add(data);
		Map<String, Object> insertDataSource = dataSourceEsInterface.insertDataSource(JSON.toJSONString(outList));
		System.out.println(insertDataSource);
		return data;
    }
	
	public List<Object> makeProjectTable(Date date){
		List<Object> outData = new ArrayList<Object>();
		String cron = "0 0 8,12,20 ? * *";
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date startDate = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date endDate = cal.getTime();
		
		List<String> timerList = CronPub.getTimeBycron_String(cron, "", startDate, endDate);
		
		for (int i = 0; i < timerList.size(); i++) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("aging_status", "未处理");
			data.put("occur_time", 0);
			
			data.put("name", "国内精细化城镇预报");
			data.put("type", "SEVP_NMC_RFFC_SCON_EME_ACHN_LNO_P9");
			data.put("startMoniter", "yes");
			
			Map<String, Object> fields = new HashMap<String, Object>();
			fields.put("data_time", timerList.get(i));
			data.put("should_time", timerList.get(i));
			data.put("last_time", timerList.get(i));
			fields.put("file_name", "/forecast/");
			fields.put("ip_addr", "10.10.31.98");
			fields.put("module", "DS");
			
			data.put("fields", fields);
			outData.add(data);
			
		}
		Map<String, Object> backData = dataSourceEsInterface.insertDataSource_DI(JSON.toJSONString(outData));
		System.out.println(backData);
		return outData;
	}

}
