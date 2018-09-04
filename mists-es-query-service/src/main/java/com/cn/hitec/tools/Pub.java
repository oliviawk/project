package com.cn.hitec.tools;

import org.assertj.core.internal.Dates;
import org.assertj.core.internal.Strings;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class Pub {

    public static  String Index_Head = "";
    public static  String Index_Food_Simpledataformat = "yyyyMMdd";


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

    public static List<Date> getDateList(Date endDate , int timeGranularity, int n) throws Exception{
        List<Date> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);

        dateList.add(endDate);
        int d = DateUnit.getDateUnit(DateUnit.MM);
        for (int i = 1 ; i < n ; i++){
            calendar.add( d , timeGranularity);
            dateList.add(calendar.getTime());
        }

        return dateList;
    }

    public static List<String> getIndexList (List<Date> dateList){
        List<String> indexList = new ArrayList<>();
        SimpleDateFormat sd = new SimpleDateFormat(Pub.Index_Food_Simpledataformat);
        for (Date dt : dateList){
            String strDt = Pub.Index_Head+sd.format(dt);
            if (indexList.contains(strDt)){
                continue;
            }
            indexList.add(strDt);
        }
        return indexList;
    }

    public static List<String> getIndexList (Date... values){
        List<String> indexList = new ArrayList<>();
        SimpleDateFormat sd = new SimpleDateFormat(Pub.Index_Food_Simpledataformat);
        for (Date dt : values){
            String strDt = Pub.Index_Head+sd.format(dt);
            if (indexList.contains(strDt)){
                continue;
            }
            indexList.add(strDt);
        }
        return indexList;
    }


    public static RangeQueryBuilder RangeChoiceTest(String rangeType, RangeQueryBuilder builder , Object param){
        if("gt".equals(rangeType)) {
            builder.gt(param);
        }else if("gte".equals(rangeType)) {
            builder.gte(param);
        }else if("lt".equals(rangeType)) {
            builder.lt(param);
        }else if("lte".equals(rangeType)) {
            builder.lte(param);
        }

        return builder;
    }
}
