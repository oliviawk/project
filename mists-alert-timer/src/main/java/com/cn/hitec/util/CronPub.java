package com.cn.hitec.util;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
            System.out.println(cron);
            e.printStackTrace();
            timeList = null;
        } finally {
            return timeList;
        }
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
        } finally {
            return timeList;
        }
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
            SimpleDateFormat df = new SimpleDateFormat(simpleDataFormatString);
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
        } finally {
            return strTime;
        }
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
        } finally {
            return strTime;
        }
    }

//
//    public static void main(String[] args){
//
//        System.out.println(getLastTimeBycron_String("0 0 * * * ? *","yyyy-MM-dd HH:mm:ss",new Date()));
//        System.out.println(11);
//    }
}
