package com.cn.hitec.service;

import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RGF_test2 {


    public static void main(String[] args){
        test();
    }


    public static void test(){
        String msg = "10.30.16.220※log_tran_h8_nc_20180722.log※!!!Info:proc tran ftp://10.14.83.137/h8_nc/ at 02:20:18......※10.30.16.220※log_tran_h8_nc_20180722.log※!!!Info:proc tran ftp://10.14.83.137/h8_nc/ at 02:20:34......※HS_H08_20180721_1800_B01_FLDK_R10_S0110.nc transfer completed with status 0. Total time:16.303320 sec. File size:69944612 bytes. File mtime:02:20:14.※!!!Info:proc tran ftp://10.0.74.170///home/docker/TDS/data/h8_nc/ at 02:20:35......※HS_H08_20180721_1800_B01_FLDK_R10_S0110.nc transfer completed with status 0. Total time:1.788716 sec. File size:69944612 bytes. File mtime:02:20:14.※!!!Info:proc tran ftp://10.0.74.170///home/docker/TDS/data/h8_nc/ at 02:20:37......※HS_H08_20180721_1800_B01_FLDK_R10_S0110.nc transfer completed with status 0. Total time:1.808796 sec. File size:69944612 bytes. File mtime:02:20:14.※HS_H08_20180721_1800_B01_FLDK_R10_S0110.nc transfer completed with status 0. Total time:0.770028 sec. File size:69944612 bytes. File mtime:02:20:14.※###############################  process end at 02:20:38  ###############################※";
        System.out.println(processing(msg));
    }

    public static List<String> processing(String msg){

        String ips = "(10.30.16.220|10.0.74.170)";
        String datatypes = "h8_nc,eleh";

        List<String> toEsJsons = new ArrayList<>();

        Pattern ipspattern = Pattern.compile(ips);
        Matcher matcher = ipspattern.matcher(msg);
        if(!matcher.find()){
            return toEsJsons;
        }

        try{

            String[] lines = msg.split("\\※|\\?");
            String date = "";
            String beginTime = "";
            String endTime = "";
            String ip = "";
            String target_ip = "";
            String type = "";
            boolean add = false;

            Pattern ippattern = Pattern.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})$");
            Pattern typepattern = Pattern.compile("log_tran_(.+)_(\\d{8})");
            Pattern timepattern1 = Pattern
                    .compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).+(\\d{2}:\\d{2}:\\d{2})");
            Pattern timepattern2 = Pattern
                    .compile("(\\d{2}:\\d{2}:\\d{2})");
            Pattern contextpattern = Pattern
                    .compile("(.+) transfer completed with status (\\d+)\\. Total time:([0-9|\\.]+) sec\\. File size:(\\d+) bytes\\. File mtime:([0-9|:]+)");

            SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMddHH:mm:ss");
            SimpleDateFormat df2 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss.SSSZ");
            List<JSONObject> list = new ArrayList<JSONObject>();
            long receive_time = new Date().getTime();
            for (int i = 0; i < lines.length; i++) {
                if("".equals(ip)){
                    matcher = ippattern.matcher(lines[i]);
                    if (matcher.find()) {
                        ip = matcher.group(1);
                    }
                    continue;
                }



                matcher = typepattern.matcher(lines[i]);
                if (matcher.find()) {
                    type = matcher.group(1);
                    date = matcher.group(2);

                    if(!datatypes.contains(type)){
                        return toEsJsons;
                    }
                } else {
                    matcher = timepattern1.matcher(lines[i]);
                    if (matcher.find()) {
                        target_ip = matcher.group(1);
                        if(ips.contains(target_ip)){
                            add = true;
                            beginTime = endTime = matcher.group(2);

                            Date end = df1.parse(date + endTime);

                            if(list.size() > 0){
                                for (JSONObject obj : list) {

                                    obj.getJSONObject("fields").element("end_time", df2.format(end));
                                    obj.put("occur_time", end.getTime());

                                    toEsJsons.add(obj.toString());

                                    System.out.println(obj.toString());
                                }

                                list.clear();

                            }
                        }
                        else{
                            add = false;
                        }

                    } else {
                        matcher = contextpattern.matcher(lines[i]);
                        if (matcher.find() && add) {

                            JSONObject obj = new JSONObject();
                            JSONObject subobj = new JSONObject();
                            obj.put("receive_time", receive_time);

                            subobj.put("start_time", df2.format(df1.parse(date + beginTime)));

                            subobj.put("ip_addr", ip);
                            subobj.put("ip_addr_target", target_ip);

                            subobj.put("module", "分发");

//                            h8_nc,eleh

                            if(type.equals("h8_nc")){
                                String[] arr = matcher.group(1).split("_");
                                //HS_H08_20180728_1540_B01_FLDK_R10_S0110.nc

                                obj.put("type", "h8_nc");
                                obj.put("name", "h8_nc");
                                String time  = arr[2]+arr[3];

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);
                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d));
                            }
                            else if(type.equals("eleh")){
                                //MSP1_PMSC_ELEH_ME_L88_CHN_201807291540_00020-00000.nc

                                String[] arr = matcher.group(1).split("_");

                                String time = arr[6];
                                int addtime = Integer.parseInt(arr[7].replace("-00000.nc","")) ;

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date oldDt = df.parse(time);

                                Calendar cal = Calendar.getInstance();
                                cal.setTime(oldDt);
                                cal.add(Calendar.MINUTE, addtime);

                                obj.put("type", "eleh");
                                obj.put("name", "eleh");

                                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(cal.getTime()));
                            }
                            else{
                                obj.put("type", type);
                            }

                            subobj.put("file_name", matcher.group(1));
                            subobj.put("event_status", matcher.group(2));
                            subobj.put("totalTime", matcher.group(3));
                            subobj.put("file_size", matcher.group(4));
                            subobj.put("mtime", matcher.group(5));
                            if (!matcher.group(2).equals("0")) {
                                i++;
                                subobj.put("event_info", lines[i]);
                            }

                            obj.put("fields", subobj);

                            list.add(obj);
                        }
                        else if(lines[i].contains("process end")){
                            matcher = timepattern2.matcher(lines[i]);
                            if(matcher.find()){
                                endTime = matcher.group(1);

                                Date end = df1.parse(date + endTime);

                                if(list.size() > 0){
                                    for (JSONObject obj : list) {

                                        obj.getJSONObject("fields").element("end_time", df2.format(end));
                                        obj.put("occur_time", end.getTime());

                                        toEsJsons.add(obj.toString());

                                        System.out.println(obj.toString());
                                    }

                                    list.clear();

                                }
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            System.out.println("!!!!!!error");
            System.out.println(e);
            System.out.println(msg);
            e.printStackTrace();
        }

        return toEsJsons;
    }

}

