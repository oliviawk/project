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
public class OCF_Consumer extends MsgConsumer{
    private static final Logger logger = LoggerFactory.getLogger(RGF_Consumer.class);
    private static String topic = "OCF";
    private static String type = "OCF";

    @Value("${OCF.send.target.ips}")
    private String ips;
    @Value("${OCF.datatype}")
    private String datatypes;
//    @Value("${RGF.collect}")
//    private String collect;
//    @Value("${RGF.send}")
//    private String send;


    public OCF_Consumer(@Value("${OCF.group.id}")String group) {
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
                    System.out.println(type);
                    date = matcher.group(2);
                    System.out.println(date);

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

                            if(type.equals("bhfk")){
                                String[] arr = matcher.group(1).split("_");
                                //  MSP3_PMSC_OCF12H_ME_L88_GLB_201808220600_00000-36000.TXT

                                obj.put("type", "BHFK");
                                obj.put("name", "BHFK");
                                String time  = arr[6];

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else if(type.equals("ECMWF_new")){
                                String[] arr = matcher.group(1).split("_");
                                //  ECMWF_ELE2018082212.DAT.gz

                                obj.put("type", "ECMWF_NEW");
                                obj.put("name", "ECMWF_NEW");
                                String time  = arr[1].replace("ELE","").replace(".DAT.gz","")+"00";

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else if(type.equals("T639_new")){
                                String[] arr = matcher.group(1).split("_");
                                //  T639_ELE2018082212.DAT.gz

                                obj.put("type", "T639_NEW");
                                obj.put("name", "T639_NEW");
                                String time  = arr[1].replace("ELE","").replace(".DAT.gz","")+"00";

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else if(type.equals("NCEP_new")){
                                String[] arr = matcher.group(1).split("_");
                                //  ncep_ELE2018082212.DAT.gz

                                obj.put("type", "NCEP_NEW");
                                obj.put("name", "NCEP_NEW");
                                String time  = arr[1].replace("ELE","").replace(".DAT.gz","")+"00";

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else if(type.equals("RJTD_NEW")){
                                String[] arr = matcher.group(1).split("_");
                                //  RJTD_ELE2018082212.DAT.gz

                                obj.put("type", "RJTD_NEW");
                                obj.put("name", "RJTD_NEW");
                                String time  = arr[1].replace("ELE","").replace(".DAT.gz","")+"00";

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else if(type.equals("cimiss_to_obs")){
                                String[] arr = matcher.group(1).split("_");
                                //  aglb_obs2018082208.dat

                                obj.put("type", "CIMISS_TO_OBS");
                                obj.put("name", "CIMISS_TO_OBS");
                                String time  = arr[1].replace("obs","").replace(".dat","")+"00";

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else if(type.equals("OCF_FINAL")){
                                String[] arr = matcher.group(1).split("_");
                                //  WMCF_OCF_FINE2018081800.DAT.gz

                                obj.put("type", "OCF_FINAL");
                                obj.put("name", "OCF_FINAL");
                                String time  = arr[2].replace("FINE","").replace(".DAT.gz","")+"00";

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else if(type.equals("ocf_update")){
                                String[] arr = matcher.group(1).split("_");
                                //  WMCF_OCF_LST_UPDATE_1H2018082220.DAT



                                String time = null;
                                if(matcher.group(1).indexOf("1H") > -1){
                                    obj.put("type", "OCF_1H_UPDATE");
                                    obj.put("name", "OCF_1H_UPDATE");
                                    time = arr[4].replace("1H","").replace(".DAT","")+"00";
                                }
                                if(matcher.group(1).indexOf("3H") > -1){
                                    obj.put("type", "OCF_3H_UPDATE");
                                    obj.put("name", "OCF_3H_UPDATE");
                                    time = arr[4].replace("3H","").replace(".DAT","")+"00";
                                }
                                if(matcher.group(1).indexOf("12H") > -1){
                                    obj.put("type", "OCF_12H_UPDATE");
                                    obj.put("name", "OCF_12H_UPDATE");
                                    time = arr[4].replace("12H","").replace(".DAT","")+"00";
                                }

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else if(type.equals("ocf_jiangji")){
                                String[] arr = matcher.group(1).split("_");
                                //  WMCF_OCF_LST_UPDATE_1H2018082220.DAT

                                if(matcher.group(1).indexOf("OCF1H") > -1){
                                    obj.put("type", "OCF1H_ME_L88_GLB");
                                    obj.put("name", "OCF1H_ME_L88_GLB");
                                }
                                if(matcher.group(1).indexOf("OCF3H") > -1){
                                    obj.put("type", "OCF3H_ME_L88_GLB");
                                    obj.put("name", "OCF3H_ME_L88_GLB");
                                }
                                if(matcher.group(1).indexOf("OCF12H") > -1){
                                    obj.put("type", "OCF12H_ME_L88_GLB");
                                    obj.put("name", "OCF12H_ME_L88_GLB");
                                }

                                String time = arr[6]+"00";
                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else if(type.equals("ocf_new")){
                                String[] arr = matcher.group(1).split("_");
                                //  pmsc_ocf_aglb_3h_201808222000_00000-36000.gz
                                //  pmsc_ocf_aglb_merge_3h_201808192000_00000-36000.gz
                                //  pmsc_ocf_ch_1h_201808192000_00000-36000.gz

                                String time = null;
                                String num = null;
                                if(matcher.group(1).indexOf("1h") > -1){
                                    num = "1";
                                }
                                if(matcher.group(1).indexOf("3h") > -1){
                                    num = "3";
                                }
                                if(matcher.group(1).indexOf("12h") > -1){
                                    num = "12";
                                }
                                if(matcher.group(1).indexOf("ch_merge_"+num+"h") > -1){
                                    obj.put("type", "CH_MERGE_"+num+"H");
                                    obj.put("name", "CH_MERGE_"+num+"H");
                                    time  = arr[5];
                                }else if(matcher.group(1).indexOf("aglb_merge_"+num+"h") > -1){
                                    obj.put("type", "AGLB_MERGE_"+num+"H");
                                    obj.put("name", "AGLB_MERGE_"+num+"H");
                                    time  = arr[5];
                                }
                                else if(matcher.group(1).indexOf("aglb_"+num+"h") > -1){
                                    obj.put("type", "AGLB_"+num+"H");
                                    obj.put("name", "AGLB_"+num+"H");
                                    time  = arr[4];
                                }
                                else if(matcher.group(1).indexOf("ch_"+num+"h") > -1){
                                    obj.put("type", "CH_"+num+"H");
                                    obj.put("name", "CH_"+num+"H");
                                    time  = arr[4];
                                }

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else{
                                obj.put("type", type.toUpperCase());
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
            logger.warn("!!!!!!error");
            logger.error(""+e);
            logger.error(msg);
            e.printStackTrace();
        }
        logger.info(JSON.toJSONString(toEsJsons));
        return toEsJsons;
    }



}
