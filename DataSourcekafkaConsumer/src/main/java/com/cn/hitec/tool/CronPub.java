package com.cn.hitec.tool;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CronPub {

    public static void test(){
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);

        calendar.add(Calendar.DAY_OF_MONTH , +1);
        calendar.set(Calendar.HOUR_OF_DAY , 0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND , 0 );

        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = calendar.getTime();

        List<String> timeList = getTimeBycron_String("0 0 * * * ? *","",startDate,endDate);
        for (String str : timeList){
            System.out.println(str);
        }
    }

    /**
     * 根据cron表达式， 生成 指定时间内的  时间列表---------时次对应的时间
     * @param cron
     * @param startDate
     * @param endDate
     * @return   String
     */
    public static List<String> getTimeBycron_String(String cron, String simpleDataFormatString , Date startDate , Date endDate) {
        List<String> timeList = new ArrayList<>();
        try {
            if(StringUtils.isEmpty(cron)){
                return null;
            }
            if(StringUtils.isEmpty(simpleDataFormatString)){
                simpleDataFormatString = "yyyy-MM-dd HH:mm:ss";
            }
            CronExpression exp = new CronExpression(cron);
            SimpleDateFormat df = new SimpleDateFormat(simpleDataFormatString);
            // 循环得到接下来n此的触发时间点，供验证
            while (startDate.getTime() < endDate.getTime()) {
                if(exp.isSatisfiedBy(startDate)){
                    timeList.add(df.format(startDate));
                }
                startDate = exp.getNextValidTimeAfter(startDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            timeList = null;
        }
        return timeList;
    }

    /**
     * 根据cron表达式， 生成 指定时间内的  时间列表---------时次对应的时间
     * @param cron
     * @param startDate
     * @param endDate
     * @return  Date
     */
    public static List<Date> getTimeBycron_Date(String cron , Date startDate , Date endDate) {
        List<Date> timeList = new ArrayList<>();
        try {
            if(StringUtils.isEmpty(cron)){
                return null;
            }
            CronExpression exp = new CronExpression(cron);
            // 循环得到接下来n此的触发时间点，供验证
            while (startDate.getTime() < endDate.getTime()) {
                if(exp.isSatisfiedBy(startDate)){
                    timeList.add(startDate);
                }
                startDate = exp.getNextValidTimeAfter(startDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            timeList = null;
        }
        return timeList;
    }


    /**
     * 根据cron表达式， 生成 接下来几天的 时间列表---------时次对应的时间
     * @param cron
     * @param days
     * @return
     */
    public static List<Date> getTimeByCron_Date_NextFewDays(String cron , int days){
        List<Date> timeList = new ArrayList<>();
        try {
            if(org.springframework.util.StringUtils.isEmpty(cron)){
                return null;
            }
            Date dt = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dt);
            calendar.add(Calendar.DAY_OF_MONTH,days+1);
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);

            timeList = getTimeBycron_Date(cron,dt,calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            timeList = null;
        }
        return timeList;
    }

    /**
     * 根据cron表达式， 生成 接下来几天的 时间列表---------时次对应的时间
     * @param cron
     * @param days
     * @return
     */
    public static List<String> getTimeByCron_String_NextFewDays(String cron ,String format, int days){
        List<String> timeList = new ArrayList<>();
        try {
            if(org.springframework.util.StringUtils.isEmpty(cron)){
                return null;
            }
            Date dt = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dt);
            calendar.add(Calendar.DAY_OF_MONTH,days+1);
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);

            timeList = getTimeBycron_String(cron,format,dt,calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            timeList = null;
        }
        return timeList;
    }


    /**
     * 根据cron表达式， 生成下一个匹配的时间
     * @param cron
     * @param startDate
     * @return   String
     */
    public static String getOneTimeBycron_String(String cron, String simpleDataFormatString , Date startDate) {
        String strTime = null;
        try {
            if(StringUtils.isEmpty(cron)){
                return null;
            }
            if(startDate == null){
                return null;
            }
            if(StringUtils.isEmpty(simpleDataFormatString)){
                simpleDataFormatString = "yyyy-MM-dd HH:mm:ss";
            }
            CronExpression exp = new CronExpression(cron);
//            SimpleDateFormat df = new SimpleDateFormat(simpleDataFormatString);
            // 循环得到接下来n此的触发时间点，供验证
            while (true) {
                System.out.println(exp.getTimeBefore(startDate));
                    break;
//                if(exp.isSatisfiedBy(startDate)){
//                    strTime = df.format(startDate);
//                    break;
//                }
//                startDate = exp.getNextValidTimeAfter(startDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            strTime = null;
        } 
        return strTime;
    }


    /**
     * 根据cron表达式， 生成上一个匹配的时间
     * @param cron
     * @param date
     * @return   String
     */
    public static String getLastTimeBycron_String(String cron, String simpleDataFormatString , Date date ) {
        String strTime = null;
        try {
            if(StringUtils.isEmpty(cron)){
                return null;
            }
            if(date == null){
                return null;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            calendar.set(Calendar.HOUR_OF_DAY , 0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND , 0 );

            Date newDate = calendar.getTime();
            if(StringUtils.isEmpty(simpleDataFormatString)){
                simpleDataFormatString = "yyyy-MM-dd HH:mm:ss";
            }
            CronExpression exp = new CronExpression(cron);
            SimpleDateFormat df = new SimpleDateFormat(simpleDataFormatString);
            // 循环得到接下来n此的触发时间点，供验证
            while (true) {
                if(exp.isSatisfiedBy(newDate)){

                    if(newDate.getTime() >= date.getTime()){
                        break;
                    }
                    strTime = df.format(newDate);
                }
                newDate = exp.getNextValidTimeAfter(newDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            strTime = null;
        }
        return strTime;
    }


    public static List<String> regToStr(String strFileName, String cron){

        Pattern p = Pattern.compile("\\{(.+?)(\\[[\\+\\-]?\\d\\])?\\}");
        Pattern p2 = Pattern.compile("(\\[.+?\\])");
        Matcher m = p.matcher(strFileName);
        List<String> strList = new ArrayList<>();

        while(m.find()){
            String timeFormat = m.group(1);
            int timezI = 0;
            if(m.group(2)!=null){
                String timez = m.group(2).replaceAll("(\\[)|(\\])","");
                timezI = Integer.parseInt(timez);
                strFileName = strFileName.replace(m.group(2),"");
            }

            SimpleDateFormat df = new SimpleDateFormat(timeFormat);
            List<Date> strTimeList = CronPub.getTimeByCron_Date_NextFewDays(cron,0);
            for (int i = 0 ; i < strTimeList.size(); i++){
                Date dt = strTimeList.get(i);
                String tempStr = strFileName;
                dt.setHours(dt.getHours()+timezI);

                tempStr = tempStr.replace("{" + timeFormat + "}",df.format(dt));
                strList.add(tempStr);
            }
            break;
        }
        List<String> results = new ArrayList<>();
        for (String str : strList ){
            m = p2.matcher(str);
            Map<String,List<String>> paramsMap = new HashMap<>();
            List<String> matchers = new ArrayList<>();
            while(m.find()){
                List<String> params = new ArrayList<>();
                String param = m.group().replaceAll("(\\[)|(\\])","");
                if(param.contains("-")){
                    String[] arr = param.split("-");
                    int begin = Integer.parseInt(arr[0]);
                    int end = Integer.parseInt(arr[1]);
                    for(int i = begin;i<=end;i++){
                        params.add(i+"");
                    }
                }
                else if(param.contains(",")){
                    String[] arr = param.split(",");
                    params.addAll(Arrays.asList(arr));
                }

                if(params.size() != 0){
                    paramsMap.put(m.group(),params);
                    matchers.add(m.group());
                }
            }
            int index = 0;
            if(matchers.size() > 0){
                replaceReg(str,paramsMap,index,matchers,results);
            }
            else{
                results.add(str);
            }
        }

        return results;
    }

    private static List<String> replaceReg(String replaceStr,Map<String,List<String>> paramsMap,int index,List<String> matchers,List<String> results){

        String matcher = matchers.get(index);
        index++;
        for(String s:paramsMap.get(matcher)){
            String str = replaceStr.replace(matcher,s);
            if(index<matchers.size()){
                replaceReg(str,paramsMap,index,matchers,results);
            }
            else{
                results.add(str);
            }
        }
        return results;
    }


    public static void main(String[] args){
        String word = "Hello";
        aa(word);
        System.out.println(word);

//        System.out.println(getLastTimeBycron_String("0 0 * * * ? *","yyyy-MM-dd HH:mm:ss",new Date()));
//        System.out.println(11);
//
//        String strTest = "AW_aa_hh_hdr";
//        if (strTest.indexOf("AW") > -1){
//            System.out.println(1);
//        }else if (strTest.indexOf("aa") > -1 || strTest.indexOf("hh") > -1){
//            System.out.println(2);
//        }

    }
    public static void aa(String word){
        word = word + "word";
    }
}
