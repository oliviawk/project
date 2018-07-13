package com.cn.hitec.service;/**
 * Created by libin on 2018/7/3.
 */

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.DataInfo;
import com.cn.hitec.bean.DataSourceSetting;
import com.cn.hitec.feign.client.DataSourceEsInterface;
import com.cn.hitec.repository.DataInfoRepository;
import com.cn.hitec.repository.DataSourceSettingRepository;
import com.cn.hitec.tool.CronPub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName DataSourceSendMakeProjectTable
 * @Description TODO
 * @Author Li Cong
 * @Date 2018/7/3 15:14
 * @vERSION 1.0
 **/
@Service
public class DataSourceSendMakeProjectTable  {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceSendMakeProjectTable.class);
    @Autowired
    DataSourceSettingRepository dataSourceSettingRepository;
    @Autowired
    DataSourceEsInterface dataSourceEsInterface;

    @Autowired
    DataInfoRepository dataInfoRepository;

    public List<DataSourceSetting> insertBaseFilter = new ArrayList<DataSourceSetting>();

    public void updateInsertBaseFilter(){
//    	List<DataSourceSetting> dataSourceSettings = dataSourceSettingRepository.findAll();
//    	System.out.println("查询数据库更新对比map:"+ JSON.toJSONString(dataSourceSettings));
//    	for (int i = 0; i < dataSourceSettings.size(); i++) {
//    		DataSourceSetting dataSourceSetting = dataSourceSettings.get(i);
//    		String key = dataSourceSetting.getIpAddr() +":"+ dataSourceSetting.getSendUser()
//    				+":"+ dataSourceSetting.getFileName();
//    		List<DataSourceSetting> value = insertBaseFilter.get(key);
//    		if (value == null){
//    			value = new ArrayList<DataSourceSetting>();
//    			value.add(dataSourceSetting);
//    		}else{
//    			value.add(dataSourceSetting);
//    		}
//    		insertBaseFilter.put(key, value);
//		}
        insertBaseFilter = dataSourceSettingRepository.findAll();
    }

    public List<Object> makeProjectTable(Date date){
        List<Object> outData = new ArrayList<Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<DataInfo> dataInfoList = dataInfoRepository.findDatasByParentId(30030001);

        for (int i = 0; i < dataInfoList.size(); i++) {
            DataInfo dataInfo = dataInfoList.get(i);
            String cron = dataInfo.getMonitor_times();

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

            String shouldTime = dataInfo.getShould_time();
            String[] shouldTimeArr = shouldTime.split(",");

            for (int j = 0; j < timerList.size(); j++) {
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("aging_status", "未处理");
                data.put("occur_time", 0);

                data.put("name", "国内精细化城镇预报");
                data.put("type", dataInfo.getName());
                data.put("startMoniter", "yes");

                Map<String, Object> fields = new HashMap<String, Object>();
                fields.put("data_time", timerList.get(j));

                Date timerDate = new Date();
                try {
                    timerDate = sdf.parse(timerList.get(j));
                } catch (ParseException e) {
                    logger.error("解析时次为时间格式出错"+ e.toString());
                    logger.error("解析时次为时间格式出错，时次为"+ timerList.get(j));
                    continue;
                }
                cal.setTime(timerDate);
                int shouldTimeAdd = 0;
                try {
                    shouldTimeAdd = Integer.parseInt(shouldTimeArr[j]);
                } catch (NumberFormatException e) {
                    logger.error("转换String为int出错"+ e.toString());
                    logger.error("解析String为int出错，String为"+ shouldTimeArr[j]);
                    continue;
                }
                cal.add(Calendar.MINUTE, shouldTimeAdd);
                data.put("should_time", sdf.format(cal.getTime()));

                data.put("last_time", timerList.get(j));
                fields.put("file_name", "/forecast/");
                fields.put("ip_addr", dataInfo.getIp());
                fields.put("module", "DS");

                data.put("fields", fields);
                outData.add(data);
            }
        }

        Map<String, Object> backData = dataSourceEsInterface.insertDataSource_DI(JSON.toJSONString(outData));
        System.out.println(JSON.toJSONString(outData));
        System.out.println(backData);
        return outData;
    }
}
