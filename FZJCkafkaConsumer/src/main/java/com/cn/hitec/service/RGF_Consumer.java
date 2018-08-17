package com.cn.hitec.service;


import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RGF_Consumer extends MsgConsumer{

    private static final Logger logger = LoggerFactory.getLogger(RGF_Consumer.class);
    private static String topic = "RGF";
    private static String type = "RGF";

    @Value("${RGF.send.target.ips}")
    private String ips;
    @Value("${RGF.datatype}")
    private String datatypes;
//    @Value("${RGF.collect}")
//    private String collect;
//    @Value("${RGF.send}")
//    private String send;


    public RGF_Consumer(@Value("${RGF.group.id}")String group) {
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

                            subobj.put("module", "分发");

//                            h8_nc,eleh

                            if(type.equals("h8_nc")){
                                String[] arr = matcher.group(1).split("_");
                                //HS_H08_20180728_1540_B01_FLDK_R10_S0110.nc

                                obj.put("type", "H8_NC");
                                obj.put("name", "H8_NC");
                                String time  = arr[2]+arr[3];

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                Calendar cal = Calendar.getInstance();
                                cal.setTime(d);
                                cal.add(Calendar.HOUR, 8);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(cal.getTime()));
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
                                cal.add(Calendar.MINUTE, addtime+480);

                                obj.put("type", "ELEH");
                                obj.put("name", "ELEH");

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
        logger.info(JSON.toJSONString(toEsJsons));
        return toEsJsons;
    }

}
