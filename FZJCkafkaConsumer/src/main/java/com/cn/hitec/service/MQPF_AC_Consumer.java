package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MQPF_AC_Consumer extends MsgConsumer{
    private static final Logger logger = LoggerFactory.getLogger(MQPF_AC_Consumer.class);
    private static String topic = "MQPF_AC";
    private static String type = "MQPF";

    @Value("${MQPF.send.target.ips}")
    private String ips;
    @Value("${MQPF.datatype}")
    private String datatypes;
    @Value("${MQPF.collect}")
    private String collect;
    @Value("${MQPF.send}")
    private String send;

    public MQPF_AC_Consumer(@Value("${MQPF.group.id}")String group) {
        super(topic, group, type);
    }


    @Override
    public List<String> processing(String msg) throws ParseException {
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

//                                    System.out.println(obj.toString());
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

//                                        System.out.println(obj.toString());
                                    }

                                    list.clear();

                                }
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            logger.warn("!!!!!!error");
            logger.error(""+e);
            logger.error(msg);
            e.printStackTrace();
        }

        return toEsJsons;
    }
}
