package com.cn.hitec.tools;

import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Pub {
    public static Map<String,String> machingMap = new HashMap<>();
    static {
        machingMap.put("ReadFY2NCVIS","卫星云图");
        machingMap.put("ReadFY2NCIR1","卫星云图");
        machingMap.put("ReadFY2NC","卫星云图");
        machingMap.put("LatLonQREFEnd","雷达png图");
        machingMap.put("dizhizaihai","灾害落区图");
        machingMap.put("shanhong","灾害落区图");
        machingMap.put("forestfire","灾害落区图");
        machingMap.put("micapstojson072","天气公报");
        machingMap.put("micapstojson072","天气公报");
        machingMap.put("micapstojson072","天气公报");


        machingMap.put("satellite","FY-2G/E");
    }

    public static Map<String,Object> alertMap_collect = new HashMap<>();
    public static Map<String,Object> alertMap_machining = new HashMap<>();
    public static Map<String,Object> alertMap_distribute = new HashMap<>();


    public static final String Index_Head = "log_";
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

    public static Date transform_StringToDate(String strDate , String simpleDataFormat) throws Exception{
        if(StringUtils.isEmpty(strDate) || StringUtils.isEmpty(simpleDataFormat)){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(simpleDataFormat);

        return sdf.parse(strDate);
    }

    /**
     * 得到index集合
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
}
