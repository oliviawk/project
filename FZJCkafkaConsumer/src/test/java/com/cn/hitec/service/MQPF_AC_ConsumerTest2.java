package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MQPF_AC_ConsumerTest2 {


    public static void main(String[] args){
//        MQPF_AC_ConsumerTest2.test_220();
        test();
    }


    public static List<String> processing(String msg){

        String ips = "(10.30.16.220|10.0.122.155|10.30.16.249)";
        String datatypes = "radarbasebin,mqpfPngref5m,mqpfNc5m";
        String collect = "10.30.16.249";
        String send = "10.0.122.155";

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
                            if(collect.contains(target_ip)){
                                subobj.put("module", "采集");
                            }
                            else if(send.contains(target_ip)){
                                subobj.put("module", "分发");
                            }

//                            radarbasebin,mqpfPngref5m,mqpfNc5m

                            if(type.equals("radarbasebin")){
                                String time = "";
                                String[] arr = matcher.group(1).split("_");
                                //Z_RADR_I_Z9240_20180727033525_O_DOR_SC_CAP.bin.bz2

                                obj.put("type", arr[0]+"_"+arr[1]+"_"+arr[2]+"_"+arr[3]);
                                obj.put("name", "雷达基数据");
                                time = arr[4];


                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                                Date d = df.parse(time);
                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d));
                            }
                            else if(type.equals("mqpfPngref5m")){
                                //QPFRef_201807031710.png
                                String[] arr = matcher.group(1).split("_");
                                obj.put("type", "MQPF_PNG5M");
                                obj.put("name", "MQPF_PNG5M");
                                String time = arr[1].replace(".png","");

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);
                                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d));
                            }
                            else if(type.equals("mqpfNc5m")) {
                                //mqpf_20180703_2320.nc
                                String[] arr = matcher.group(1).split("_");
                                obj.put("type", "MQPF_NC5M");
                                obj.put("name", "MQPF_NC5M");
                                String time = arr[1]+arr[2].replace(".nc","");

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);
                                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d));
                            }else{
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

    public static void test_220(){
        StringBuffer msg = new StringBuffer();
        msg.append("10.30.16.220※log_tran_radarbasebin_20180727.log※!!!Info:proc tran ftp://10.30.16.187//radar_base/ at 11:36:32......※Z_RADR_I_Z9970_20180727033450_O_DOR_CD_CAP.bin.bz2 transfer completed with status 0. Total time:0.015184 sec. File size:29489 bytes. File mtime:11:36:32.※Z_RADR_I_Z9370_20180727033000_O_DOR_SB_CAP.bin.bz2 transfer completed with status 0. Total time:0.010286 sec. File size:712898 bytes. File mtime:11:36:25.※Z_RADR_I_Z9970_20180727033031_O_DOR_CD_CAP.bin.bz2 transfer completed with status 0. Total time:0.002055 sec. File size:27340 bytes. File mtime:11:36:32.※Z_RADR_I_Z9818_20180727033000_O_DOR_SC_CAP.bin.bz2 transfer completed with status 0. Total time:0.013126 sec. File size:992024 bytes. File mtime:11:36:30.※Z_RADR_I_Z9598_20180727033000_O_DOR_SA_CAP.bin.bz2 transfer completed with status 0. Total time:0.009434 sec. File size:628670 bytes. File mtime:11:36:23.※Z_RADR_I_Z9457_20180727033200_O_DOR_CC_CAP.bin.bz2 transfer completed with status 0. Total time:0.016269 sec. File size:1119918 bytes. File mtime:11:36:20.※Z_RADR_I_Z9240_20180727033525_O_DOR_SC_CAP.bin.bz2 transfer completed with status 0. Total time:0.011337 sec. File size:645867 bytes. File mtime:11:36:22.※Z_RADR_I_Z9938_20180727033430_O_DOR_CD_CAP.bin.bz2 transfer completed with status 0. Total time:0.004141 sec. File size:94158 bytes. File mtime:11:36:22.※Z_RADR_I_Z9990_20180727033500_O_DOR_CC_CAP.bin.bz2 transfer completed with status 0. Total time:0.011636 sec. File size:694433 bytes. File mtime:11:36:27.※Z_RADR_I_Z9375_20180727033000_O_DOR_SA_CAP.bin.bz2 transfer completed with status 0. Total time:0.015731 sec. File size:1030751 bytes. File mtime:11:36:31.※Z_RADR_I_Z9937_20180727033000_O_DOR_CC_CAP.bin.bz2 transfer completed with status 0. Total time:0.016672 sec. File size:1260032 bytes. File mtime:11:36:18.※Z_RADR_I_Z9393_20180727033000_O_DOR_SB_CAP.bin.bz2 transfer completed with status 0. Total time:0.009948 sec. File size:661410 bytes. File mtime:11:36:29.※!!!Info:proc tran ftp://10.30.16.249//9854/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9872/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9827/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9527/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9396/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9776/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9456/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9516/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9350/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9722/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9731/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9556/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9458/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9818/ at 11:36:32......※Z_RADR_I_Z9818_20180727033000_O_DOR_SC_CAP.bin.bz2 transfer completed with status 0. Total time:0.054785 sec. File size:992024 bytes. File mtime:11:36:30.※!!!Info:proc tran ftp://10.30.16.249//9856/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9559/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9798/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9270/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9763/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9415/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9903/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9599/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9592/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9313/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9917/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9817/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9758/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9454/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9411/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9888/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9558/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9478/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9896/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9475/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9352/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9773/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9376/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9476/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9515/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9718/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9892/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9091/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9452/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9898/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9816/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9762/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9953/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9555/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9934/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9735/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9086/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9971/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9839/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9755/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9085/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9535/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9081/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9357/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9090/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9596/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9734/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9432/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9951/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9433/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9543/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9200/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9436/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9792/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9836/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9851/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9010/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9997/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9717/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9593/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9660/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9230/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9001/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9591/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9451/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9996/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9754/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9914/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9210/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9779/ at 11:36:32......※!!!Info:proc tran ftp://10.30.16.249//9240/ at 11:36:32......※Z_RADR_I_Z9240_20180727033525_O_DOR_SC_CAP.bin.bz2 transfer completed with status 0. Total time:0.045687 sec. File size:645867 bytes. File mtime:11:36:22.※!!!Info:proc tran ftp://10.30.16.249//9710/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9745/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9999/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9774/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9539/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9759/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9355/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9912/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9739/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9092/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9335/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9911/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9523/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9937/ at 11:36:33......※Z_RADR_I_Z9937_20180727033000_O_DOR_CC_CAP.bin.bz2 transfer completed with status 0. Total time:0.057071 sec. File size:1260032 bytes. File mtime:11:36:18.※!!!Info:proc tran ftp://10.30.16.249//9073/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9314/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9941/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9915/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9576/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9082/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9471/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9662/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9572/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9736/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9563/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9775/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9439/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9751/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9772/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9060/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9020/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9852/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9431/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9936/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9692/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9891/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9517/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9954/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9834/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9598/ at 11:36:33......※Z_RADR_I_Z9598_20180727033000_O_DOR_SA_CAP.bin.bz2 transfer completed with status 0. Total time:0.045930 sec. File size:628670 bytes. File mtime:11:36:23.※!!!Info:proc tran ftp://10.30.16.249//9437/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9855/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9990/ at 11:36:33......※Z_RADR_I_Z9990_20180727033500_O_DOR_CC_CAP.bin.bz2 transfer completed with status 0. Total time:0.043826 sec. File size:694433 bytes. File mtime:11:36:27.※!!!Info:proc tran ftp://10.30.16.249//9552/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9795/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9737/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9797/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9770/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9518/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9551/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9513/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9417/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9477/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9938/ at 11:36:33......※Z_RADR_I_Z9938_20180727033430_O_DOR_CD_CAP.bin.bz2 transfer completed with status 0. Total time:0.236065 sec. File size:94158 bytes. File mtime:11:36:22.※!!!Info:proc tran ftp://10.30.16.249//9794/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9871/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9071/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9570/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9290/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9220/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9021/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9998/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9753/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9831/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9970/ at 11:36:33......※Z_RADR_I_Z9970_20180727033450_O_DOR_CD_CAP.bin.bz2 transfer completed with status 0. Total time:0.028758 sec. File size:29489 bytes. File mtime:11:36:32.※Z_RADR_I_Z9970_20180727033031_O_DOR_CD_CAP.bin.bz2 transfer completed with status 0. Total time:0.003451 sec. File size:27340 bytes. File mtime:11:36:32.※!!!Info:proc tran ftp://10.30.16.249//9771/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9870/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9370/ at 11:36:33......※Z_RADR_I_Z9370_20180727033000_O_DOR_SB_CAP.bin.bz2 transfer completed with status 0. Total time:0.044741 sec. File size:712898 bytes. File mtime:11:36:25.※!!!Info:proc tran ftp://10.30.16.249//9858/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9746/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9859/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9778/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9730/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9371/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9833/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9002/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9470/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9379/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9453/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9377/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9538/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9571/ at 11:36:33......※!!!Info:proc tran ftp://10.30.16.249//9083/ at 11:36:33......※!!!Info:proc tran ftp://10.14.83.137//9574/ at 11:36:34......※###############################  process end at 11:36:34  ###############################※");

        System.out.println(processing(msg.toString()));
    }

    public static void test(){
//        对于采集文件的格式处理
//        String[] lines = msg.split("※");
//        String ip_addr = lines[0];
//        for(int i=0;i < lines.length;i++){
//            if(lines[i].indexOf("Z_RADR_I") > -1 ){
//                HashMap<String,String> file =new HashMap();
//                HashMap map = new HashMap();
//                String[] line = lines[i].split( " ");
//                for(int j = 0;j<line.length;j++){
//                    if(line[j].indexOf("Z_RADR_I") > -1 ){
//                        String removeStr = "_CAP.bin.bz2";
//                        String str=line[j].replaceAll(removeStr,"");
//                        String[] str1 = str.split("_");
//                        file.put("stopNumber",str1[3]);
//                        file.put("observationTime",str1[4]);
//                        file.put("parameter",str1[5]+"_"+str1[6]+"_"+str1[7]);
//                    }
//                }
//                file.put("event_status",line[5].replace(".",""));
//                file.put("totalTime",line[7].replace("time:",""));
//                file.put("file_Size",line[10].replace("size:","")+" bytes");
//                file.put("mtime",line[13].replace("mtime:","").replace(".",""));
//                file.put("module","采集");
//                file.put("ip_addr",ip_addr);
//                map.put("name","分钟降水");
//                map.put("type","分钟降水");
//                map.put("field",file);
//                JSONObject jsonObject = JSONObject.fromObject(map);
//                System.out.println(jsonObject.toString());
//            }
//
//        }

//        分发png5m格式处理
//        String msg = "10.30.16.220※log_tran_mqpfPngref5m_20180703.log※!!!Info:proc tran ftp://10.0.122.155/radardata/mqpf/png/ref5m/ at 16:47:01......※QPFRef_201807031710.png transfer completed with status 0. Total time:0.126302 sec. File size:253830 bytes. File mtime:16:46:48.※QPFRef_201807031730.png transfer completed with status 0. Total time:0.100409 sec. File size:248847 bytes. File mtime:16:46:48.※QPFRef_201807031835.png transfer completed with status 0. Total time:0.248254 sec. File size:244249 bytes. File mtime:16:46:47.※QPFRef_201807031750.png transfer completed with status 0. Total time:0.100288 sec. File size:246092 bytes. File mtime:16:46:46.※QPFRef_201807031815.png transfer completed with status 0. Total time:0.057799 sec. File size:244918 bytes. File mtime:16:46:47.※QPFRef_201807031700.png transfer completed with status 0. Total time:0.035733 sec. File size:259319 bytes. File mtime:16:46:48.※QPFRef_201807031805.png transfer completed with status 0. Total time:0.580099 sec. File size:245415 bytes. File mtime:16:46:47.※QPFRef_201807031650.png transfer completed with status 0. Total time:0.218044 sec. File size:270327 bytes. File mtime:16:46:48.※QPFRef_201807031740.png transfer completed with status 0. Total time:0.163183 sec. File size:246868 bytes. File mtime:16:46:48.※QPFRef_201807031825.png transfer completed with status 0. Total time:0.044405 sec. File size:245082 bytes. File mtime:16:46:47.※QPFRef_201807031720.png transfer completed with status 0. Total time:0.083010 sec. File size:250403 bytes. File mtime:16:46:49.※QPFRef_201807031755.png transfer completed with status 0. Total time:0.067068 sec. File size:245092 bytes. File mtime:16:46:48.※QPFRef_201807031745.png transfer completed with status 0. Total time:0.055186 sec. File size:246091 bytes. File mtime:16:46:48.※QPFRef_201807031830.png transfer completed with status 0. Total time:0.080084 sec. File size:244448 bytes. File mtime:16:46:48.※QPFRef_201807031655.png transfer completed with status 0. Total time:0.099614 sec. File size:263167 bytes. File mtime:16:46:46.※QPFRef_201807031820.png transfer completed with status 0. Total time:0.083207 sec. File size:245021 bytes. File mtime:16:46:49.※QPFRef_201807031645.png transfer completed with status 0. Total time:0.394877 sec. File size:282293 bytes. File mtime:16:46:47.※QPFRef_201807031735.png transfer completed with status 0. Total time:0.049340 sec. File size:248033 bytes. File mtime:16:46:48.※QPFRef_201807031725.png transfer completed with status 0. Total time:0.033922 sec. File size:249204 bytes. File mtime:16:46:44.※QPFRef_201807031840.png transfer completed with status 0. Total time:0.288940 sec. File size:244164 bytes. File mtime:16:46:48.※QPFRef_201807031810.png transfer completed with status 0. Total time:0.057373 sec. File size:244752 bytes. File mtime:16:46:47.※QPFRef_201807031800.png transfer completed with status 0. Total time:0.083129 sec. File size:245278 bytes. File mtime:16:46:49.※QPFRef_201807031705.png transfer completed with status 0. Total time:0.095169 sec. File size:255910 bytes. File mtime:16:46:47.※QPFRef_201807031715.png transfer completed with status 0. Total time:0.062386 sec. File size:251890 bytes. File mtime:16:46:48.※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/pic/ at 16:47:05......※QPFRef_201807031710.png transfer completed with status 0. Total time:0.021706 sec. File size:253830 bytes. File mtime:16:46:48.※QPFRef_201807031730.png transfer completed with status 0. Total time:0.008018 sec. File size:248847 bytes. File mtime:16:46:48.※QPFRef_201807031835.png transfer completed with status 0. Total time:0.008070 sec. File size:244249 bytes. File mtime:16:46:47.※QPFRef_201807031750.png transfer completed with status 0. Total time:0.007403 sec. File size:246092 bytes. File mtime:16:46:46.※QPFRef_201807031815.png transfer completed with status 0. Total time:0.012538 sec. File size:244918 bytes. File mtime:16:46:47.※QPFRef_201807031700.png transfer completed with status 0. Total time:0.057447 sec. File size:259319 bytes. File mtime:16:46:48.※QPFRef_201807031805.png transfer completed with status 0. Total time:0.159588 sec. File size:245415 bytes. File mtime:16:46:47.※QPFRef_201807031650.png transfer completed with status 0. Total time:0.240974 sec. File size:270327 bytes. File mtime:16:46:48.※QPFRef_201807031740.png transfer completed with status 0. Total time:0.013096 sec. File size:246868 bytes. File mtime:16:46:48.※QPFRef_201807031825.png transfer completed with status 0. Total time:0.072020 sec. File size:245082 bytes. File mtime:16:46:47.※QPFRef_201807031720.png transfer completed with status 0. Total time:0.043180 sec. File size:250403 bytes. File mtime:16:46:49.※QPFRef_201807031755.png transfer completed with status 0. Total time:0.008132 sec. File size:245092 bytes. File mtime:16:46:48.※QPFRef_201807031745.png transfer completed with status 0. Total time:0.010277 sec. File size:246091 bytes. File mtime:16:46:48.※QPFRef_201807031830.png transfer completed with status 0. Total time:0.077693 sec. File size:244448 bytes. File mtime:16:46:48.※QPFRef_201807031655.png transfer completed with status 0. Total time:0.257826 sec. File size:263167 bytes. File mtime:16:46:46.※QPFRef_201807031820.png transfer completed with status 0. Total time:0.130454 sec. File size:245021 bytes. File mtime:16:46:49.※QPFRef_201807031645.png transfer completed with status 0. Total time:0.076864 sec. File size:282293 bytes. File mtime:16:46:47.※QPFRef_201807031735.png transfer completed with status 0. Total time:0.086376 sec. File size:248033 bytes. File mtime:16:46:48.※QPFRef_201807031725.png transfer completed with status 0. Total time:0.060574 sec. File size:249204 bytes. File mtime:16:46:44.※QPFRef_201807031840.png transfer completed with status 0. Total time:0.007824 sec. File size:244164 bytes. File mtime:16:46:48.※QPFRef_201807031810.png transfer completed with status 0. Total time:0.008731 sec. File size:244752 bytes. File mtime:16:46:47.※QPFRef_201807031800.png transfer completed with status 0. Total time:0.009700 sec. File size:245278 bytes. File mtime:16:46:49.※QPFRef_201807031705.png transfer completed with status 0. Total time:0.009456 sec. File size:255910 bytes. File mtime:16:46:47.※QPFRef_201807031715.png transfer completed with status 0. Total time:0.008748 sec. File size:251890 bytes. File mtime:16:46:48.※###############################  process end at 16:47:06  ###############################※";

//      分发单站NC5m格式处理
//        String msg = "10.30.16.220※log_tran_mqpfNc5m_station_20180703.log※!!!Info:proc tran ftp://10.0.122.155/radardata/mqpf/station/nc/ at 17:12:02......※!!!Info:proc tran ftp://10.14.83.137/mqpf/ at 17:12:02......※Z9597_20180703085900_MQPF_20180703090656.nc transfer completed with status 0. Total time:0.222270 sec. File size:2950722 bytes. File mtime:17:12:01.※Z9759_20180703090000_MQPF_20180703090705.nc transfer completed with status 0. Total time:0.141360 sec. File size:0 bytes. File mtime:17:12:02.※Z9454_20180703090300_MQPF_20180703090108.nc transfer completed with status 0. Total time:0.227749 sec. File size:440451 bytes. File mtime:17:12:01.※Z9754_20180703090000_MQPF_20180703090704.nc transfer completed with status 0. Total time:0.212004 sec. File size:4961348 bytes. File mtime:17:12:02.※!!!Info:proc tran ftp://10.16.57.168/mqpf/station/nc/ at 17:12:03......※Z9597_20180703085900_MQPF_20180703090656.nc transfer completed with status 0. Total time:0.044517 sec. File size:2950722 bytes. File mtime:17:12:01.※Z9759_20180703090000_MQPF_20180703090705.nc transfer completed with status 0. Total time:0.020449 sec. File size:0 bytes. File mtime:17:12:02.※Z9454_20180703090300_MQPF_20180703090108.nc transfer completed with status 0. Total time:0.007988 sec. File size:440451 bytes. File mtime:17:12:01.※Z9754_20180703090000_MQPF_20180703090704.nc transfer completed with status 0. Total time:0.047002 sec. File size:4961348 bytes. File mtime:17:12:02.※!!!Info:proc tran ftp://10.30.16.240//mnt/data_nfs/cma/pmsc/mqpf/station/nc/ at 17:12:03......※Z9597_20180703085900_MQPF_20180703090656.nc transfer completed with status 0. Total time:0.111244 sec. File size:2950722 bytes. File mtime:17:12:01.※Z9759_20180703090000_MQPF_20180703090705.nc transfer completed with status 0. Total time:0.150265 sec. File size:0 bytes. File mtime:17:12:02.※Z9454_20180703090300_MQPF_20180703090108.nc transfer completed with status 0. Total time:0.026889 sec. File size:440451 bytes. File mtime:17:12:01.※Z9754_20180703090000_MQPF_20180703090704.nc transfer completed with status 0. Total time:0.160407 sec. File size:4961348 bytes. File mtime:17:12:02.※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/ at 17:12:03......※Z9597_20180703085900_MQPF_20180703090656.nc transfer completed with status 0. Total time:0.112102 sec. File size:2950722 bytes. File mtime:17:12:01.※Z9759_20180703090000_MQPF_20180703090705.nc transfer completed with status 0. Total time:0.045901 sec. File size:0 bytes. File mtime:17:12:02.※Z9454_20180703090300_MQPF_20180703090108.nc transfer completed with status 0. Total time:0.022751 sec. File size:440451 bytes. File mtime:17:12:01.※Z9754_20180703090000_MQPF_20180703090704.nc transfer completed with status 0. Total time:0.139852 sec. File size:4961348 bytes. File mtime:17:12:02.※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9001/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9002/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9010/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9020/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9021/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9060/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9070/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9071/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9072/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9080/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9081/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9082/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9083/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9084/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9085/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9090/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9091/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9092/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9200/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9210/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9220/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9230/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9240/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9250/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9270/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9280/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9290/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9311/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9313/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9314/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9317/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9335/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9351/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9352/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9355/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9357/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9358/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9370/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9371/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9377/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9379/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9393/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9396/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9398/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9411/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9417/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9421/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9431/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9433/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9436/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9437/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9438/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9439/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9451/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9452/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9453/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9454/ at 17:12:04......※Z9454_20180703090300_MQPF_20180703090108.nc transfer completed with status 0. Total time:0.035778 sec. File size:440451 bytes. File mtime:17:12:01.※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9455/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9456/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9457/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9458/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9470/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9471/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9475/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9476/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9477/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9478/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9513/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9515/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9516/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9517/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9518/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9519/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9523/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9527/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9531/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9532/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9535/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9536/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9538/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9539/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9543/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9551/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9552/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9555/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9556/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9558/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9559/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9562/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9570/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9571/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9572/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9574/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9576/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9577/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9578/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9579/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9580/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9591/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9592/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9593/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9595/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9596/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9597/ at 17:12:04......※Z9597_20180703085900_MQPF_20180703090656.nc transfer completed with status 0. Total time:0.116318 sec. File size:2950722 bytes. File mtime:17:12:01.※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9598/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9599/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9631/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9660/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9662/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9692/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9710/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9716/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9717/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9718/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9719/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9722/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9730/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9731/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9734/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9735/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9736/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9739/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9745/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9746/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9751/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9753/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9754/ at 17:12:04......※Z9754_20180703090000_MQPF_20180703090704.nc transfer completed with status 0. Total time:0.153030 sec. File size:4961348 bytes. File mtime:17:12:02.※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9755/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9758/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9759/ at 17:12:04......※Z9759_20180703090000_MQPF_20180703090705.nc transfer completed with status 0. Total time:0.045451 sec. File size:0 bytes. File mtime:17:12:02.※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9762/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9763/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9770/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9771/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9772/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9773/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9774/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9775/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9776/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9778/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9779/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9791/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9792/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9793/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9794/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9795/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9796/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9797/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9798/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9816/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9817/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9818/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9831/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9833/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9834/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9836/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9839/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9851/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9852/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9854/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9855/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9856/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9857/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9859/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9870/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9871/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9872/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9874/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9876/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9879/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9883/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9888/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9891/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9892/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9894/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9896/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9898/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9903/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9911/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9912/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9914/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9915/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9916/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9917/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9931/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9934/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9936/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9937/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9938/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9941/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9951/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9953/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9954/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9970/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9971/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9990/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9991/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9993/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9996/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9997/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9998/ at 17:12:04......※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/sm/Z9999/ at 17:12:04......※###############################  process end at 17:12:04  ###############################※\n";
//      分发拼图NC5m格式处理
        String msg = "10.30.16.220※log_tran_mqpfNc5m_20180703.log※!!!Info:proc tran ftp://10.0.122.155/radardata/mqpf/nc/5m/ at 23:22:01......※mqpf_20180703_2320.nc transfer completed with status 0. Total time:1.078332 sec. File size:34954046 bytes. File mtime:23:21:34.※!!!Info:proc tran ftp://10.16.57.168/mqpf/nc/5m/ at 23:22:02......※mqpf_20180703_2320.nc transfer completed with status 0. Total time:0.382235 sec. File size:34954046 bytes. File mtime:23:21:34.※!!!Info:proc tran ftp://10.14.83.37/nc_5m/ at 23:22:03......※mqpf_20180703_2320.nc transfer completed with status 0. Total time:0.476275 sec. File size:34954046 bytes. File mtime:23:21:34.※!!!Info:proc tran ftp://10.0.74.170/MQPF/ at 23:22:03......※mqpf_20180703_2320.nc transfer completed with status 0. Total time:0.425113 sec. File size:34954046 bytes. File mtime:23:21:34.※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/mqpf/fcst/m/ at 23:22:04......※mqpf_20180703_2320.nc transfer completed with status 0. Total time:0.361933 sec. File size:34954046 bytes. File mtime:23:21:34.※###############################  process end at 23:22:04  ###############################※\n" ;


        processing(msg);
    }
}

