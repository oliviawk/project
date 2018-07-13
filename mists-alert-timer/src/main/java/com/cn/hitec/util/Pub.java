package com.cn.hitec.util;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.bean.AlertBeanNew;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;

public class Pub {

	public static Map<String, Object> DI_ConfigMap = Collections.synchronizedMap(new HashMap());

//	public static Map<String, Object> moduleMap = new HashMap<>();
//	public static Map<String, Object> moduleMapGet = new HashMap<>();
//	public static Map<String, String> dataNameMap = new HashMap<>();

	public static Map<String, Map> DIMap = Collections.synchronizedMap(new HashMap());
//	public static Map<String, Map> DIMap_machining = Collections.synchronizedMap(new HashMap());
//	public static Map<String, Map> DIMap_distribute = Collections.synchronizedMap(new HashMap());
	public static Map<String, Map> DIMap_DS = Collections.synchronizedMap(new HashMap());

	public static Map<String, Map> DIMap_t639 = Collections.synchronizedMap(new HashMap());
	/*告警上下游关联使用*/
	public static Map<String, String> alertModuleMap = Collections.synchronizedMap(new HashMap());

	public static final String KEY_RESULT = "result";
	public static final String KEY_RESULTDATA = "resultData";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_SPEND = "spend";

	public static final String VAL_SUCCESS = "success";
	public static final String VAL_ERROR = "error";
	public static final String VAL_FAIL = "fail";

	public static String Index_Head = null;
	public static String Index_Food_Simpledataformat = "yyyyMMdd";

//	static {
//		moduleMap.put("采集","A");
//		moduleMap.put("加工","B");
//		moduleMap.put("分发","C");
//		moduleMap.put("DS","DS");
//
//		moduleMapGet.put("A","采集");
//		moduleMapGet.put("B","加工");
//		moduleMapGet.put("C","分发");
//		moduleMapGet.put("DS","DS");
//
//	}

	public static String transform_DateToString(Date date, String simpleDataFormat) throws Exception {
		if (date == null) {
			return "";
		}
		SimpleDateFormat sdf = null;
		if (StringUtils.isEmpty(simpleDataFormat)) {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} else {
			sdf = new SimpleDateFormat(simpleDataFormat);
		}
		return sdf.format(date);
	}

	public static Date transform_StringToDate(String strDate, String simpleDataFormat) throws Exception {
		if (StringUtils.isEmpty(strDate)) {
			return null;
		}
		SimpleDateFormat sdf = null;
		if (StringUtils.isEmpty(simpleDataFormat)) {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} else {
			sdf = new SimpleDateFormat(simpleDataFormat);
		}

		return sdf.parse(strDate);
	}

	/**
	 * 秒 转换 为 String 格式的字符串
	 * 
	 * @param date
	 * @param simpleDataFormat
	 * @return
	 * @throws Exception
	 */
	public static String transform_longDataToString(long date, String simpleDataFormat) throws Exception {
		if (date <= 0) {
			return "";
		}
		SimpleDateFormat sdf = null;
		if (StringUtils.isEmpty(simpleDataFormat)) {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} else {
			sdf = new SimpleDateFormat(simpleDataFormat);
		}
		Date newDate = new Date(date * 1000);
		return sdf.format(newDate);
	}

	// public static Map<String,Integer> alert_time_map = new HashMap<>();
	// static {
	// alert_time_map.put("雷达",30);
	// alert_time_map.put("云图",60);
	// alert_time_map.put("ReadFY2NC",60);
	// alert_time_map.put("炎热是猪",5);
	// }


	/**
	 * 得到 今天和昨天的index
	 * @param date
	 * @param lg
	 * @return
	 */
	public static String[] getIndices ( Date date , int lg){
		if(lg < 0){
			return null;
		}
		String[] dates = new String[lg+1];
		//生成日历插件， 计算出 第二天的开始时间和结束时间
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			for (int i = 0; i < lg ; i ++){
				calendar.add(Calendar.DAY_OF_MONTH , -1);
				Date tempDate = calendar.getTime();
				dates[i] = Index_Head + transform_DateToString(tempDate,Index_Food_Simpledataformat);
			}

			dates[lg] = Index_Head + transform_DateToString(date,Index_Food_Simpledataformat);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dates;
	}

	/**
	 * 获取md5
	 * @param s
	 * @return
	 */
	public static String MD5(String s) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(s.getBytes("utf-8"));

			final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
			StringBuilder ret = new StringBuilder(bytes.length * 2);
			for (int i=0; i<bytes.length; i++) {
				ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
				ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
			}
			return ret.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
			return "";
//            throw new RuntimeException(e);
		}
	}


	/**
	 * 转换微信、短信告警信息格式
	 * @return
	 */
	public static String transformTitle(String str , AlertBeanNew alertBean) throws  Exception{
		if(StringUtils.isEmpty(str)){
			return str;
		}
		String[] s = alertBean.getGroupId().split("_");
		if(s.length != 3){
			new Exception("groupid type is error");
		}
		str = str.replace("[yyyy-MM-dd HH:mm:ss]",alertBean.getOccur_time() == null ? "时间为空":alertBean.getOccur_time());
		str = str.replace("[yyyy-MM-dd HH:mm]",alertBean.getOccur_time() == null ? "时间为空":alertBean.getOccur_time());
		str = str.replace("[数据源]","OP".equals(s[0])? "业务数据":"基础资源");
		str = str.replace("[资料名]",alertBean.getSubName() == null ? "资料名为空":alertBean.getSubName());
		str = str.replace("[资料时次]",alertBean.getData_time() == null ? "资料时次为空":alertBean.getData_time());
		str = str.replace("[IP]",alertBean.getIpAddr() == null ? "IP为空":alertBean.getIpAddr());
		str = str.replace("[业务名]",s[1]);
		str = str.replace("[环节]",s[2]);
		str = str.replace("[路径]",alertBean.getFileName()== null ? "路径为空":alertBean.getFileName());
		str = str.replace("[影响的业务]","(影响的业务方法目前还没有实现)");
		str = str.replace("[处理方案]","(目前还没有实现处理方案)");
		str = str.replace("[错误信息]",alertBean.getDesc() == null ? "错误信息为空":alertBean.getDesc());
		str = str.replace("[错误详情]",alertBean.getDesc() == null ? "错误详情为空":alertBean.getDesc());
		return str;
	}


	public static void main(String[] args){
//		int a =  "OP_FZJC_C".lastIndexOf("_"+Pub.moduleMap.get("分发").toString());
//		String str = "OP_FZJC_C";
//		str = str.substring(0,str.lastIndexOf("_"+Pub.moduleMap.get("分发").toString())) + "_" +Pub.moduleMap.get("采集").toString();
//		System.out.println(str);
//		Date date = new Date();
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd.HHmmss");
//		String strDate = simpleDateFormat.format(date);
//		System.out.println(strDate);

//		try {
//			String nameDefine = "sdfefe_sssfe_{yyyyMMdd.HHmmss}[+8]_0000";
//
//			String timeFormat = nameDefine.substring(nameDefine.indexOf("{")+1,nameDefine.indexOf("}"));
//
//			System.out.println(timeFormat);
//			String timeZoneFormat = "0";
//			if (nameDefine.indexOf("[") > -1 && nameDefine.indexOf("]") > -1){
//				timeZoneFormat = nameDefine.substring(nameDefine.indexOf("[")+1,nameDefine.indexOf("]"));
//			}
//
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(new Date());
//			cal.add(Calendar.HOUR_OF_DAY, -Integer.parseInt(timeZoneFormat));
//			String fileName = nameDefine.replace("{"+timeFormat+"}",Pub.transform_DateToString(cal.getTime(),timeFormat)).replace("["+timeZoneFormat+"]","");
//			System.out.println(fileName);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//		Map ma = new HashMap();
//		ma.put("aaa","aaaavalue");
//		ma.put("bb","bbbbvalue");
//		Map files = new HashMap();
//		files.put("111","1111");
//
//		ma.put("files",files);
//
//		System.out.println(JSON.toJSONString(ma));

		boolean bool1 = false;
		boolean bool2 = false;
		if(bool1 && ( bool1 == bool2)){
			System.out.println(true);
		}else{
			System.out.println(false);
		}
	}
}
