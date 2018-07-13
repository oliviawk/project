package com.cn.hitec.service;/**
 * Created by libin on 2018/7/2.
 */

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.DataInfo;
import com.cn.hitec.bean.DataSourceSetting;
import com.cn.hitec.feign.client.DataSourceEsInterface;
import com.cn.hitec.repository.DataInfoRepository;
import com.cn.hitec.tool.CronPub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName DataSourceSendTsix
 * @Description TODO
 * @Author Li Cong
 * @Date 2018/7/2 10:36
 * @vERSION 1.0
 **/
@Service
public class DataSourceSendTsix extends  MsgConsumer {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceSendTsix.class);
    private static String topic = "XFERLOG_T639";
    public DataSourceSendTsix() {
        super(topic, "1234", null);
    }
    /**
     * 解析数据拼成入库所需格式
     * 数据源为：Sun Feb 25 03:32:06 2018 1 10.1.72.45 8070 /bin/Z_RADR_I_Z9857_20180224192248_P_DOR_CD_R_10_230_5.857.bin.tmp b _ i r nmic_provider ftp 0 * c
     * 共141位
     */
    @SuppressWarnings("deprecation")
    public Map<String, Object> processing (String msg) {

        Map<String, Object> outData = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, Object> field = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
//        msg = "Tue Jul  3 00:15:55 2018 1 10.20.49.131 53313806 /2018070212/Z_NAFP_C_BABJ_20180702120000_P_CNPC-T639-GMFS-HNEHE-00600.grib2 b _ i r cwfs ftp 0 * c 10.14.83.63";
        String[] msgs = msg.split(" ");
        //按空格划分, 会有多余空格项, 要去除
        List<String> localMsgs=Arrays.asList(msgs);//将数组转换为list集合
        if(localMsgs.contains("")){//加入集合中包含这个元素
            //这个时候我们直接移除会报错,所以我们要转换为Arraylist
            //list.remove("张三");
            List<String> changeMsgs=new ArrayList<String>(localMsgs);//转换为ArrayLsit调用相关的remove方法
            changeMsgs.remove("");
            for (int i = 0; i < changeMsgs.size(); i++) {
                msgs[i] = changeMsgs.get(i);
            }
        }
        //   Tsix=/2018070212/Z_NAFP_C_BABJ_20180702120000_P_CNPC-T639-GMFS-HNEHE-00600.grib2
        String Tsix=msgs[8];
        if(Tsix.indexOf("T639")==-1){
            return null;
        }
        // 不是这个用户发的不需要
        String user = msgs[13];
        if ("upload".equals(user)){
            return null;
        }
        String UserCatalog_username=user_catalog_repository.findAll_User_catalog(user).getUser_name();
        if (UserCatalog_username==null||UserCatalog_username.equals("")){
            return null;
        }
        String file_name = msgs[8];
        file_name=file_name.replace(".tmp","");

        if (file_name.indexOf("?") != -1 || file_name.indexOf("RADA") != -1
                || file_name.indexOf("radar") != -1 || file_name.indexOf("RADR") != -1){	//去除雷达文件
            return null;
        }
        logger.info("筛选后："+msg);
//        msg = "Tue Jul  3 00:15:55 2018 1 10.20.49.131 53313806 /2018070212/Z_NAFP_C_BABJ_20180702120000_P_CNPC-T639-GMFS-HNEHE-00600.grib2 b _ i r cwfs ftp 0 * c 10.14.83.63";
        String file_sizeStr = msgs[7];
        String event_status = msgs[15];
        String ipAddr = msgs[18];
        String regEx = "[^0-9]";//匹配指定范围内的数字
        //Pattern是一个正则表达式经编译后的表现模式
        Pattern p = Pattern.compile(regEx);
        // 一个Matcher对象是一个状态机器，它依据Pattern对象做为匹配模式对字符串展开匹配检查。
        Matcher m = p.matcher(file_name);
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
        String dataSourceType = file_name.replace(timeStr, "-");
        //通过和配置库比较，查看入数据库还是模板库
        DataSourceSetting dataSourceSetting = null;
        if (insertBaseFilter.size() == 0){
            Map<String, Object> possibleNeedData = new HashMap<String, Object>();
            possibleNeedData.put("_id", dataSourceType);
            possibleNeedData.put("fileName", file_name);
            possibleNeedData.put("sendUser", user);
            possibleNeedData.put("ipAddr", ipAddr);
            outData.put("type", "noDataSource");
            outData.put("data", possibleNeedData);
            logger.info("放回Tsix1  nodata");
            return outData;
        }
        for (int i = 0; i < insertBaseFilter.size(); i++) {
            dataSourceSetting = insertBaseFilter.get(i);

            String fileName = dataSourceSetting.getFileName();
            String filePath=dataSourceSetting.getDirectory();
            filePath="/"+filePath.replace(UserCatalog_username,"");
            fileName=filePath+fileName;
            Pattern pattern = Pattern.compile(fileName);
            Matcher matcher = pattern.matcher(file_name);
            // 查找字符串中是否有匹配正则表达式的字符/字符串
            boolean rs = matcher.matches();
            if (rs){
                break;
            }
            if (i == insertBaseFilter.size() - 1){
                Map<String, Object> possibleNeedData = new HashMap<String, Object>();
                possibleNeedData.put("_id", dataSourceType);
                possibleNeedData.put("fileName", file_name);
                possibleNeedData.put("sendUser", user);
                possibleNeedData.put("ipAddr", ipAddr);
                outData.put("type", "noDataSource");
                outData.put("data", possibleNeedData);
                logger.info("放回Tsix2  nodata");
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

            field.put("file_name", file_name);
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
        } catch (ParseException e) {
            logger.error("解析日志并组装入库数据出错");
            e.printStackTrace();
        }

        return outData;
    }
}
