package com.cn.hitec.tools;

import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Pub {
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
}
