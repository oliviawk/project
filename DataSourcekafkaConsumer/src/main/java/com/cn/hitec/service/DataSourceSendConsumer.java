package com.cn.hitec.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cn.hitec.bean.User_Catalog;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.DataInfo;
import com.cn.hitec.bean.DataSourceSetting;
import com.cn.hitec.feign.client.DataSourceEsInterface;
import com.cn.hitec.repository.DataInfoRepository;
import com.cn.hitec.tool.CronPub;

@Service
public class DataSourceSendConsumer extends MsgConsumer{

	private static final Logger logger = LoggerFactory.getLogger(DataSourceSendConsumer.class);
    private static String topic = "XFER_LOG";
	public DataSourceSendConsumer(@Value("${groupid.xferlog}") String groupid) {
		super(topic, groupid, null);
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

        if ("upload".equals(user)){
        	return null;
        }
        String file_name_log = msgs[8];
		file_name_log=file_name_log.replace(".tmp","");

		String file_sizeStr = "";
		String event_status = "";
		String ipAddr = "";

        if(file_name_log.indexOf("/radar-base/bz2") > -1){	//说明是MQPF的雷达日志
//			Sun Feb 25 03:32:31 2018 1 10.1.72.76 647951 /radar-base/bz2/Z_RADR_I_Z9519_20180224192600_O_DOR_SA_CAP.bin.bz2.tmp b _ i r nmic_provider ftp 0 * c
			try {
				file_sizeStr = msgs[7];
				event_status = msgs[15];
				ipAddr = msgs[18];
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
				if (filePaths.length != 4){
					return null;
				}
				String[] fileNames = filePaths[3].split("_");
				if (fileNames.length != 9){
					return null;
				}
				sdf.applyPattern("yyyyMMddHHmmss");
				Date dataTime = sdf.parse(fileNames[4]);
				sdf.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
				String data_time = sdf.format(dataTime);

				long file_size_long = Long.parseLong(file_sizeStr);

				// 采集数据中不包含的数据，后期从配置库中获取
//				data.put("should_time", 0);
//				data.put("last_time", 0);
				data.put("name", "分钟降水-雷达基数据");
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
				logger.error("解析 雷达基数据 日志并组装入库数据出错");
				e.printStackTrace();
				return null;
			}
			return outData;
		}else if(file_name_log.indexOf("?") > -1  || file_name_log.indexOf("RADA") != -1 || file_name_log.indexOf("RADR") != -1){
			return null;
		}

		User_Catalog user_catalog=user_catalog_repository.findAll_User_catalog(user);
		if (user_catalog == null || StringUtils.isEmpty(user_catalog.getUser_catalog_content())){
			return  null;
		}
		String UserCatalog_username=user_catalog.getUser_catalog_content();

//        logger.info(msg);

		file_sizeStr = msgs[7];
		event_status = msgs[15];
		ipAddr = msgs[18];

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
//			logger.info("-- fileName:{} , filePath:{} , usercatalogName:{}",fileName,filePath,UserCatalog_username);
			filePath="/"+filePath.replace(UserCatalog_username,"");
			fileName=filePath+fileName;
			Pattern pattern = Pattern.compile(fileName);
    	    Matcher matcher = pattern.matcher(file_name_log);
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
			logger.error("解析日志并组装入库数据出错");
			e.printStackTrace();
		}
		return outData;
    }

}
