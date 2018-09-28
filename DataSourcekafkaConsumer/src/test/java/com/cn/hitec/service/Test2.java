package com.cn.hitec.service;

import com.cn.hitec.tool.CronPub;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: 描述信息
 * @author: fukl
 * @data: 2018年09月07日 下午3:17
 */
public class Test2 {

    public static void main(String[] args){

        String strLog = "^(.+) (\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) \\d{} /disk2/upload/product/SEVP_NMC_SNWL_SFER_EME_ACHN_L88_P9_20180828120002400.jpg.tmp b _ i r upload ftp 0 * c 10.0.122.155$";

        Pattern pattern_main = Pattern.compile("");

        Pattern pattern1 = Pattern.compile("^FY4A-_AGRI--_N_DISK_1047E_L1-_(GEO|FDI)-_MULT_NOM_(\\d{10})0000_(\\d{10}1459)_(500|1000|2000|4000)M_V0001.HDF$");

        List<String> list = new ArrayList<>();
        String fileName = "FY4A-_AGRI--_N_DISK_1047E_L1-_GEO-_MULT_NOM_20180514020000_20180514021459_4000M_V0001.HDF";
        String fileName2 = "FY4A-_AGRI--_N_DISK_1047E_L1-_FDI-_MULT_NOM_20180514020000_20180514021459_4000M_V0001.HDF";

        list.add(fileName);
        list.add(fileName2);

        for (String str : list){
            Matcher m = pattern1.matcher(str);
            System.out.println("----->"+str);
            if (m.find()){
                System.out.println(true);
            }else {

                System.out.println(false);
            }
        }


    }


    private static List<String> regToStr(String strFileName, String cron){

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
}
