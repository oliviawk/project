package com.cn.hitec.util;

import com.cn.hitec.bean.EsQueryBean_Exsit;
import com.cn.hitec.feign.client.EsQueryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Pub {

    public static Map<String,Object> alertMap = new HashMap<>();

    public static Map<String,Object> DIMap_collect = new HashMap<>();
    public static Map<String,Object> DIMap_machining = new HashMap<>();
    public static Map<String,Object> DIMap_distribute = new HashMap<>();

    public static Map<String,Object> DIMap_t639 = new HashMap<>();

    public static final String KEY_RESULT = "result";
    public static final String KEY_RESULTDATA = "resultData";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_SPEND = "spend";

    public static final String VAL_SUCCESS = "success";
    public static final String VAL_ERROR = "error";
    public static final String VAL_FAIL = "fail";


    public static final String Index_Head = "data_";
    public static final String Index_Food_Simpledataformat = "yyyyMMdd";

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

    public static Date transform_StringToDate(String strDate , String simpleDataFormat) throws  Exception{
        if(StringUtils.isEmpty(strDate)){
            return null;
        }
        SimpleDateFormat sdf = null;
        if(StringUtils.isEmpty(simpleDataFormat)){
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }else {
            sdf = new SimpleDateFormat(simpleDataFormat);
        }

        return sdf.parse(strDate);
    }

    /**
     * 秒 转换 为  String 格式的字符串
     * @param date
     * @param simpleDataFormat
     * @return
     * @throws Exception
     */
    public static String transform_longDataToString(long date , String simpleDataFormat) throws Exception{
        if(date <= 0){
            return "";
        }
        SimpleDateFormat sdf = null;
        if(StringUtils.isEmpty(simpleDataFormat)){
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }else {
            sdf = new SimpleDateFormat(simpleDataFormat);
        }
        Date newDate = new Date(date*1000);
        return sdf.format(newDate);
    }


//    public static Map<String,Integer> alert_time_map = new HashMap<>();
//    static {
//        alert_time_map.put("雷达",30);
//        alert_time_map.put("云图",60);
//        alert_time_map.put("ReadFY2NC",60);
//        alert_time_map.put("炎热是猪",5);
//    }


}
