package com.cn.hitec.tools;

import com.alibaba.fastjson.JSONObject;
import org.assertj.core.internal.Dates;
import org.assertj.core.internal.Strings;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class Pub {

    public static  String Index_Head = "";
    public static  String Index_Food_Simpledataformat = "yyyyMMdd";

    public static Map<String,Map>  dataMachiningMap = new HashMap<>();

    static{
        Map<String,Object> map_ocf = new HashMap<>();
        map_ocf.put("serverName","精细化预报(OCF)");
        map_ocf.put("basic","风、温、湿、水、云、天");
        map_ocf.put("resolution","1公里,15天时长/逐1小时");
        map_ocf.put("url","/OCF/");
        dataMachiningMap.put("OCF",map_ocf);

        Map<String,Object> map_rgf = new HashMap<>();
        map_rgf.put("serverName","快速地面融合(RGF)");
        map_rgf.put("basic","风、温、湿、压、水、能");
        map_rgf.put("resolution","1公里,30分钟时长/逐10分钟");
        map_rgf.put("url","/RGF/");
        dataMachiningMap.put("RGF",map_rgf);

        Map<String,Object> map_mqpf = new HashMap<>();
        map_mqpf.put("serverName","分钟降水预报(MQPF)");
        map_mqpf.put("basic","风、水");
        map_mqpf.put("resolution","1公里,2小时时长/逐5小时");
        map_mqpf.put("url","/MQPF/");
        dataMachiningMap.put("MQPF",map_mqpf);

        Map<String,Object> map_laps = new HashMap<>();
        map_laps.put("serverName","格点实况(LAPS)");
        map_laps.put("basic","风、温、湿、水");
        map_laps.put("resolution","1公里,0小时时长/逐1小时");
        map_laps.put("url","/laps/lct");
        dataMachiningMap.put("LAPS",map_laps);

        Map<String,Object> map_fzjc = new HashMap<>();
        map_fzjc.put("serverName","气象信息决策支持系统(FZJC)");
        map_fzjc.put("basic","温、风、水、云");
        map_fzjc.put("resolution","一公里/实时");
        map_fzjc.put("url","/fzjc/lct");
        dataMachiningMap.put("FZJC",map_fzjc);
    }

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

        Collections.sort(dateList, new Comparator<Date>(){
            /*
             * int compare(Object ob1, Object ob2) 返回一个基本类型的整型，
             * 返回负数表示：p1 小于p2，
             * 返回0 表示：p1和p2相等，
             * 返回正数表示：p1大于p2
             */
            public int compare(Date ob1, Date ob2) {
                //按照时间顺序
                if (ob1.getTime() > ob2.getTime()){
                    return -1;
                }else if(ob1.getTime() == ob2.getTime()){
                    return 0;
                }else{
                    return 1;
                }
            }
        });

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


    /**
     * map对象copy
     * @param paramsMap     被copy对象
     * @param resultMap     返回对象
     */
    public static void mapCopy(Map paramsMap , Map resultMap){
        if(resultMap == null){
            resultMap = new HashMap();
        }
        if(paramsMap == null){
            return ;
        }
        Iterator iterator = paramsMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            Object key = entry.getKey();
            resultMap.put(key,paramsMap.get(key));
        }
    }
}
