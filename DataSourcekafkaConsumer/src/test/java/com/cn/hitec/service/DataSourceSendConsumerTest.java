package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.DataSourceSetting;
import com.cn.hitec.bean.EsBean;
import com.cn.hitec.bean.User_Catalog;
import com.cn.hitec.feign.client.DataSourceEsInterface;
import com.cn.hitec.repository.DataSourceSettingRepository;
import com.cn.hitec.repository.User_Catalog_Repository;
import com.cn.hitec.tool.CronPub;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * @description: 文件匹配测试
 * @author: fukl
 * @data: 2018年07月19日 上午9:43
 */
@ActiveProfiles({"prod"})
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataSourceSendConsumerTest {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceSendConsumerTest.class);
    @Autowired
    User_Catalog_Repository user_catalog_repository;
    @Autowired
    DataSourceSettingRepository dataSourceSettingRepository;
    @Autowired
    DataSourceEsInterface dataSourceEsInterface;

    @Test
    public void processing() {

        List<String> msgs = new ArrayList<String>();

        String strLog = "Tue Aug 28 17:42:24 2018 1 10.10.31.98 613298 /disk2/upload/product/SEVP_NMC_SNWL_SFER_EME_ACHN_L88_P9_20180828120002400.jpg.tmp b _ i r upload ftp 0 * c 10.0.122.155";
        Map map = processing(strLog);
        System.out.println(map);

        if (map == null){
            System.out.println("kong");
            return ;
        }
        if ("dataSource".equals(map.get("type"))){
            msgs.add(JSON.toJSONString(map.get("data")));
            logger.info(JSON.toJSONString(msgs));
        }

        //当list数据量，大于100 ， 或者存储时间超过5秒 ， 调用入ES接口一次
        if (msgs.size() > 0) {
            EsBean esBean = new EsBean();
            esBean.setType("DATASOURCE");
            esBean.setIndex("");
            esBean.setData(msgs);

//            Map<String, Object> insertDataSource = dataSourceEsInterface.update(esBean);
//            logger.info("入库数据返回结果："+ JSON.toJSONString(insertDataSource));
            msgs.clear();
        }

    }


    /**
     * 解析数据拼成入库所需格式
     * 数据源为：Sun Feb 25 03:32:06 2018 1 10.1.72.45 8070 /bin/Z_RADR_I_Z9857_20180224192248_P_DOR_CD_R_10_230_5.857.bin.tmp b _ i r nmic_provider ftp 0 * c
     * 共141位
     */
    public Map<String, Object> processing (String msg) {

        List<DataSourceSetting> insertBaseFilter = dataSourceSettingRepository.findAll();

        Map<String, Object> outData = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> field = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

//        msg = "Thur Jun 21 20:02:01 2018 1 10.10.31.98 1000 /forecast/SEVP_NMC_RFFC_SCON_EME_ACHN_LNO_P9_20180621210016812.TXT.tmp b _ i r nmc_provider ftp 0 * c 10.30.16.111";

        String[] msgs = msg.split(" ");

        //按空格划分, 会有多余空格项, 要去除
        List<String> localMsgs=Arrays.asList(msgs);//将数组转换为list集合
        if(localMsgs.contains("")){//加入集合中包含这个元素
            //这个时候我们直接移除会报错,所以我们要转换为Arraylist
            List<String> changeMsgs=new ArrayList<String>(localMsgs);//转换为ArrayLsit调用相关的remove方法
            changeMsgs.remove("");
            for (int i = 0; i < changeMsgs.size(); i++) {
                msgs[i] = changeMsgs.get(i);
            }
        }

        // 不是这个用户发的不需要
        String user = msgs[13];


        String file_name_log = msgs[8];
        file_name_log=file_name_log.replace(".tmp","");
        String filenamelogtwo=null;

        String [] filenamelog_array=file_name_log.split("/");
        if (filenamelog_array!=null&&filenamelog_array.length>=1){
            filenamelogtwo=filenamelog_array[filenamelog_array.length-1];
        }

        String file_sizeStr = msgs[7];
        String event_status = msgs[15];
        String ipAddr = msgs[18];

        if(file_name_log.indexOf("/radar-base/bz2") > -1){	//说明是MQPF的雷达日志
//			Sun Feb 25 03:32:31 2018 1 10.1.72.76 647951 /radar-base/bz2/Z_RADR_I_Z9519_20180224192600_O_DOR_SA_CAP.bin.bz2.tmp b _ i r nmic_provider ftp 0 * c
            try {
                String sourceIp = msgs[6];	//消息来源
                if(!("10.1.72.77".equals(sourceIp) || "10.1.72.76".equals(sourceIp) || "10.1.72.75".equals(sourceIp) || "10.1.72.74".equals(sourceIp))){
                    return null;
                }

                String occurTimeStr = "";
                for (int j = 0; j < 5; j++) {
                    if (j < 4){
                        occurTimeStr += msgs[j] + " ";
                    }
                    if (j == 4){
                        occurTimeStr += msgs[j];
                    }
                }

                Date date = new Date(occurTimeStr);
                long occur_time = date.getTime();
                data.put("occur_time", occur_time);

                String[] filePaths = file_name_log.split("/");

                String[] fileNames = filePaths[3].split("_");

                sdf.applyPattern("yyyyMMddHHmmss");
                Date dataTime = sdf.parse(fileNames[4]);
                sdf.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                String data_time = sdf.format(dataTime);

                long file_size_long = Long.parseLong(file_sizeStr);

                // 采集数据中不包含的数据，后期从配置库中获取
//				data.put("should_time", 0);
//				data.put("last_time", 0);
                data.put("name", "雷达基数据");
                data.put("type", fileNames[0]+"_"+fileNames[1]+"_"+fileNames[2]+"_"+fileNames[3]);

                field.put("file_name", file_name_log);
                field.put("file_size", file_size_long);
                field.put("data_time", data_time);
                field.put("event_status", event_status);
                field.put("start_time",sdf.format(date));
                field.put("end_time",sdf.format(date));
                field.put("ip_addr", ipAddr);
                field.put("module", "采集");

                data.put("fields", field);

                outData.put("type", "MQPF_DataSource");
                outData.put("data", data);
            } catch (ParseException e) {
                logger.error("解析 雷达基数据 日志并组装入库数据出错:"+e.getMessage());
                logger.warn(msg);
//				e.printStackTrace();
                return null;
            }
            return outData;
        }else if (file_name_log.indexOf("T639-GMFS-HNEHE") > -1){
            try {

                String[] filePaths = file_name_log.split("/");

                String occurTimeStr = "";
                for (int j = 0; j < 5; j++) {
                    if (j < 4){
                        occurTimeStr += msgs[j] + " ";
                    }
                    if (j == 4){
                        occurTimeStr += msgs[j];
                    }
                }
                Date date = new Date(occurTimeStr);
                long occur_time = date.getTime();
                data.put("occur_time", occur_time);

                sdf.applyPattern("yyyyMMddHH");
                String strTime = filePaths[1];

                Date dataTime_H = sdf.parse(strTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dataTime_H);

                String strHour = filePaths[2].replace(".grib2","").split("-")[4];
                strHour = strHour.substring(0,3);
                calendar.add(Calendar.HOUR_OF_DAY,Integer.parseInt(strHour));

                sdf.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                String data_time = sdf.format(calendar.getTime());

                long file_size_long = Long.parseLong(file_sizeStr);

                // 采集数据中不包含的数据，后期从配置库中获取
                data.put("name", "T639采集");
                data.put("type", "Z_NAFP_C_BABJ");

                field.put("file_name", file_name_log);
                field.put("file_size", file_size_long);
                field.put("data_time", data_time);
                field.put("event_status", event_status);
                field.put("ip_addr", "10.30.16.220");
                field.put("module", "采集");
                data.put("fields", field);


                outData.put("type", "FZJC_DataSource");
                outData.put("data", data);

            } catch (ParseException e) {
                logger.error("解析 FZJC日志 并组装入库数据出错 ." + e.getMessage() );
                logger.warn(msg);
                return null;
            }
            return outData;
        }else if(file_name_log.indexOf("?") > -1  || file_name_log.indexOf("RADA") != -1 || file_name_log.indexOf("RADR") != -1){
            return null;
        }

        String regEx = "[^0-9]";//匹配指定范围内的数字
        //Pattern是一个正则表达式经编译后的表现模式
        Pattern p = Pattern.compile(regEx);
        // 一个Matcher对象是一个状态机器，它依据Pattern对象做为匹配模式对字符串展开匹配检查。
        Matcher m = p.matcher(file_name_log);
        //将输入的字符串中非数字部分用空格取代并存入一个字符串
        String newFileName = m.replaceAll(" ").trim();
        //以空格为分割符在讲数字存入一个字符串数组中
        String[] newFileNameArray = newFileName.split(" ");
        String timeStr = new String();
        for (int i = 0; i < newFileNameArray.length; i++) {
            String group = newFileNameArray[i];
            if (timeStr == null || timeStr.length() < group.length()){
                timeStr = group;
            }
        }

        String dataSourceType = file_name_log.replace(timeStr, "-");
        //通过和配置库比较，查看入数据库还是模板库
        DataSourceSetting dataSourceSetting = null;
        if (insertBaseFilter.size() == 0){
            Map<String, Object> possibleNeedData = new HashMap<String, Object>();
            possibleNeedData.put("_id", dataSourceType);
            possibleNeedData.put("fileName", file_name_log);
            possibleNeedData.put("sendUser", user);
            possibleNeedData.put("ipAddr", ipAddr);

            outData.put("type", "noDataSource");
            outData.put("data", possibleNeedData);

            return outData;
        }
        for (int i = 0; i < insertBaseFilter.size(); i++) {
            dataSourceSetting = insertBaseFilter.get(i);
            String fileName = dataSourceSetting.getFileName();
            String filePath=dataSourceSetting.getDirectory();
            Pattern pattern = Pattern.compile(fileName);
            Matcher matcher = pattern.matcher(filenamelogtwo);
            // 查找字符串中是否有匹配正则表达式的字符/字符串
            boolean rs = matcher.matches();
            if (rs){
                break;
            }
            if (i == insertBaseFilter.size() - 1){
                Map<String, Object> possibleNeedData = new HashMap<String, Object>();
                possibleNeedData.put("_id", dataSourceType);
                possibleNeedData.put("fileName", file_name_log);
                possibleNeedData.put("sendUser", user);
                possibleNeedData.put("ipAddr", ipAddr);

                outData.put("type", "noDataSource");
                outData.put("data", possibleNeedData);

                return outData;
            }
        }

        //成功匹配到，入库
        try {
            String occurTimeStr = "";
            for (int j = 0; j < 5; j++) {
                if (j < 4){
                    occurTimeStr += msgs[j] + " ";
                }
                if (j == 4){
                    occurTimeStr += msgs[j];
                }
            }
            Date date = new Date(occurTimeStr);
            long occur_time = date.getTime();
//			String occur_time = sdf.format(date);
            data.put("occur_time", occur_time);

            String timeFormat = dataSourceSetting.getTimeFormat();
            String dataTimeStr = timeStr.substring(0, timeFormat.length());
            sdf.applyPattern(timeFormat);
            Date dataTime = sdf.parse(dataTimeStr);
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
            String data_time = sdf.format(dataTime);

            long file_size_long = Long.parseLong(file_sizeStr);

            // 采集数据中不包含的数据，后期从配置库中获取
            data.put("should_time", 0);
            data.put("last_time", 0);
            data.put("name", dataSourceSetting.getName());
            data.put("type", dataSourceSetting.getName());

            field.put("file_name", file_name_log);
            field.put("file_size", file_size_long);
            field.put("data_time", data_time);
            field.put("event_status", event_status);
            field.put("data_type", dataSourceSetting.getDataType());
            field.put("moniter_timer", dataSourceSetting.getMoniterTimer());
            field.put("department_name", dataSourceSetting.getDepartmentName());
            field.put("phone", dataSourceSetting.getPhone());
            field.put("ip_addr", ipAddr);
            field.put("use_department", dataSourceSetting.getUseDepartment());
            field.put("system_name", "");
            field.put("module", "DS");

            data.put("fields", field);

            outData.put("type", "dataSource");
            outData.put("data", data);

            logger.info("成功匹配入库：{}",JSON.toJSONString(outData));

        } catch (ParseException e) {
            logger.error("解析日志并组装入库数据出错:"+e.getMessage());
            logger.warn(msg);
//			e.printStackTrace();
        }
        return outData;
    }
}