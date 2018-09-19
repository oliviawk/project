package com.cn.hitec.service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: 描述信息
 * @author: fukl
 * @data: 2018年08月28日 下午3:51
 */
public class TTT {

    public static void main(String[] args){
        List<String> list = new ArrayList<>();
        String s1 = "ACHN.QREF000.{yyyyMMdd.HHmmss[-8]}.latlon";
        list.add(s1);
        String s2 = "SEVP_NSMC_WXGN_FY2G_E99_ACHN_LNO_P9_{yyyyMMddHHmmssSSS}.[HDF,png]";
        list.add(s2);
        String s3 = "FY4A-_AGRI--_N_DISK_1047E_L1-_FDI-_MULT_NOM_{yyyyMMddHH[-8]}1500_{yyyyMMddHH[-8]}2959_[1-4]000M_V0001.HDF";
        list.add(s3);

        for(String s:list){
            System.out.println(s+"生成的字符串有：");
            List<String> names = regToStr(s);
            for(String str:names){
                System.out.println(str);
            }
        }
    }


    static List<String> regToStr(String str){
        Pattern p = Pattern.compile("\\{(.+?)(\\[[\\+\\-]?\\d\\])?\\}");
        Pattern p2 = Pattern.compile("(\\[.+?\\])");
        Matcher m = p.matcher(str);
        while(m.find()){
            Date date = new Date();
            if(m.group(2)!=null){
                String timez = m.group(2).replaceAll("(\\[)|(\\])","");
                date.setHours(date.getHours()+Integer.parseInt(timez));
                str = str.replace(m.group(2),"");
            }

            SimpleDateFormat df = new SimpleDateFormat(m.group(1));
            str = str.replace("{" + m.group(1) + "}",df.format(date));
        }
        m = p2.matcher(str);
        List<String> results = new ArrayList<>();
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

        return results;
    }

    static List<String> replaceReg(String replaceStr,Map<String,List<String>> paramsMap,int index,List<String> matchers,List<String> results){

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
