package com.cn.hitec.tools;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.AlertBeanNew;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class Pub {
    public static Map<String,Object> alertDIMap = new HashMap<>();

    public static final String KEY_RESULT = "result";
    public static final String KEY_RESULTDATA = "resultData";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_SPEND = "spend";

    public static final String VAL_SUCCESS = "success";
    public static final String VAL_ERROR = "error";
    public static final String VAL_FAIL = "fail";


    public static  String Index_Head = "";
    public static  String Index_Food_Simpledataformat = "yyyyMMdd";


    public static Map<String,Object> alertMap_collect = new HashMap<>();
    public static Map<String,Object> alertMap_machining = new HashMap<>();
    public static Map<String,Object> alertMap_distribute = new HashMap<>();
    public static Map<String,Object> alert_time_map = Collections.synchronizedMap(new HashMap());
    public static Map<String,Object> DI_ConfigMap = Collections.synchronizedMap(new HashMap());

    public static List<String> indexExitsList = new LinkedList<>();

//    public static Map<String, Object> moduleMap = new HashMap<>();
//    public static Map<String, Object> moduleMapGet = new HashMap<>();

    /*下游环节报错时，溯源上游使用*/
    public static Map<String, String> alertModuleMap = Collections.synchronizedMap(new HashMap());
//    static {
//        moduleMap.put("采集","A");
//        moduleMap.put("加工","B");
//        moduleMap.put("分发","C");
//        moduleMap.put("DS","DS");
//
//        moduleMapGet.put("A","采集");
//        moduleMapGet.put("B","加工");
//        moduleMapGet.put("C","分发");
//        moduleMapGet.put("DS","DS");
//
//    }

    public static String transform_DateToString(Date date , String simpleDataFormat) throws Exception{
        if(date == null){
            return "";
        }
        SimpleDateFormat sdf = null;
        if(StringUtils.isEmpty(simpleDataFormat)){
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }else {
            sdf = new SimpleDateFormat(simpleDataFormat);
        }
        return sdf.format(date);
    }

    public static Date transform_StringToDate(String strDate , String simpleDataFormat) throws Exception{
        if(StringUtils.isEmpty(strDate) || StringUtils.isEmpty(simpleDataFormat)){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(simpleDataFormat);

        return sdf.parse(strDate);
    }


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

            for (int i = 1; i <= lg ; i ++){
                calendar.add(Calendar.DAY_OF_MONTH , -1);
                Date tempDate = calendar.getTime();
                dates[i-1] = Index_Head + transform_DateToString(tempDate,Index_Food_Simpledataformat);
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
        if(org.apache.commons.lang.StringUtils.isEmpty(str)){
            return str;
        }

        String[] s = alertBean.getGroupId().split("_");
        if(s.length != 3){
            new Exception("groupid type is error");
        }
        String strTime = alertBean.getData_time();
        try {
            strTime = transform_DateToString(transform_StringToDate(alertBean.getData_time(),"yyyy-MM-dd HH:mm:ss.SSSZ"),"yyyy-MM-dd HH:mm");
        } catch (Exception e) {
            System.err.println("时间格式转换错误！"+e.getMessage());

        }


        if (alertBean.getAlertType().equals(AlertType.NOTE.getValue())){
            str = ""+alertBean.getOccur_time()+","+alertBean.getSubName()
                    +" , "+strTime+" 时次数据正常到达 ";
            return str;
        }

        str = str.replace("[yyyy-MM-dd HH:mm:ss]",alertBean.getOccur_time() == null ? "[时间为空]":alertBean.getOccur_time());
        str = str.replace("[yyyy-MM-dd HH:mm]",alertBean.getOccur_time() == null ? "[时间为空]":alertBean.getOccur_time());
        str = str.replace("[数据源]","OP".equals(s[0])? "业务数据":"基础资源");
        str = str.replace("[资料名]",alertBean.getSubName() == null ? "[资料名为空]":alertBean.getSubName());
        str = str.replace("[资料时次]",strTime == null ? "资料时次为空":strTime);
        str = str.replace("[IP]",alertBean.getIpAddr() == null ? "IP为空":alertBean.getIpAddr());
        str = str.replace("[路径]",alertBean.getFileName()== null ? "[路径为空]":alertBean.getFileName());
        str = str.replace("[提示信息]",alertBean.getDesc() == null ? "[提示信息为空]":alertBean.getDesc());
        str = str.replace("[业务名]",s[1]);
        str = str.replace("[环节]",s[2]);
        str = str.replace("[影响的业务]","(影响的业务方法目前还没有实现)");
        str = str.replace("[处理方案]","(目前还没有实现处理方案)");
        str = str.replace("[错误详情]",alertBean.getErrorMessage() == null ? "[错误详情为空]":alertBean.getErrorMessage());

        return str;
    }


    public static String transform_time(int time) {
        if (time <= 0) {
            return "";
        }
        time = time / 1000;
        int min = time / 60;
        int secend = time % 60;

        return min + "分" + secend + "秒";
    }

}
