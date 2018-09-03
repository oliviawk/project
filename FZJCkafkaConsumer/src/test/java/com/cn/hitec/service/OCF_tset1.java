package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class OCF_tset1 {

    @Value("${OCF.send.target.ips}")
    private String ips;
    @Value("${OCF.datatype}")
    private String datatypes;

    @Autowired
    OCF_Consumer ocf_consumer;


    @Test
    public void test1(){

        ocf_consumer.consume();

    }

    @Test
    public void test2() {

        //String msg = "10.30.16.220※log_tran_ocf_new_20180827.log※!!!Info:proc tran ftp://10.0.122.172/ser/station/txt/ocf/fcst/3h/ at 11:18:02......※pmsc_ocf_ch_merge_3h_201808271200_00000-36000.gz transfer completed with status 0. Total time:0.135502 sec. File size:2731961 bytes. File mtime:11:21:45.※!!!Info:proc tran ftp://10.0.74.226//ocf/dat_25w/3h/ at 11:18:02......※pmsc_ocf_ch_merge_3h_201808271200_00000-36000.gz transfer completed with status 0. Total time:0.164936 sec. File size:2731961 bytes. File mtime:11:21:45.※!!!Info:proc tran ftp://10.0.122.172/ser/station/txt/ocf/fcst/12h/ at 11:18:02......※pmsc_ocf_ch_merge_12h_201808271200_00000-36000.gz transfer completed with status 0. Total time:0.220628 sec. File size:1732685 bytes. File mtime:11:21:46.※!!!Info:proc tran ftp://10.0.74.226//ocf/dat_25w/12h/ at 11:18:02......※pmsc_ocf_ch_merge_12h_201808271200_00000-36000.gz transfer completed with status 0. Total time:0.499853 sec. File size:1732685 bytes. File mtime:11:21:46.※!!!Info:proc tran ftp://10.0.122.172/ser/station/txt/ocf/fcst/1h/ at 11:18:03......※pmsc_ocf_ch_merge_1h_201808271200_00000-36000.gz transfer completed with status 0. Total time:0.356897 sec. File size:4602643 bytes. File mtime:11:21:48.※!!!Info:proc tran ftp://10.0.74.226//ocf/dat_25w/1h/ at 11:18:03......※pmsc_ocf_ch_merge_1h_201808271200_00000-36000.gz transfer completed with status 0. Total time:0.253500 sec. File size:4602643 bytes. File mtime:11:21:48.※###############################  process end at 11:18:03  ###############################※";
        String msg1 ="10.30.16.220※log_tran_cimiss_to_obs_20180827.log※!!!Info:proc tran ftp://10.30.16.204//surface.data/WMCF/DATA/OBS/ELE_HOUR_CIMISS/ at 15:00:01......※aglb_obs2018082706.dat transfer completed with status 0. Total time:0.212877 sec. File size:1378226 bytes. File mtime:14:56:07.※ch_obs2018082706.dat transfer completed with status 0. Total time:0.817773 sec. File size:14877878 bytes. File mtime:14:56:04.※!!!Info:proc tran ftp://10.30.16.206//surface.data/WMCF/DATA/OBS/ELE_HOUR_CIMISS/ at 15:00:02......※aglb_obs2018082706.dat transfer completed with status 0. Total time:0.072407 sec. File size:1378226 bytes. File mtime:14:56:07.※ch_obs2018082706.dat transfer completed with status 0. Total time:0.285374 sec. File size:14877878 bytes. File mtime:14:56:04.※!!!Info:proc tran ftp://10.0.74.226///cvs_new/obs/surf_obs/ at 15:00:03......※aglb_obs2018082706.dat transfer completed with status 0. Total time:0.137039 sec. File size:1378226 bytes. File mtime:14:56:07.※ch_obs2018082706.dat transfer completed with status 0. Total time:0.349787 sec. File size:14877878 bytes. File mtime:14:56:04.※###############################  process end at 15:00:03  ###############################※\n";
        String msg2="10.30.16.220※log_tran_T639_new_20180827.log※!!!Info:proc tran ftp://10.0.122.172/ser/station/dat/t639/fcst/ at 16:52:32......※T639_ELE2018082712.DAT.gz transfer completed with status 0. Total time:7.824159 sec. File size:297095404 bytes. File mtime:16:56:18.※!!!Info:proc tran ftp://10.0.74.226//cvs_new/ocf/t639/ at 16:52:40......※T639_ELE2018082712.DAT.gz transfer completed with status 0. Total time:6.029321 sec. File size:297095404 bytes. File mtime:16:56:18.※###############################  process end at 16:52:46  ###############################※T639_new";
        String msg3="10.30.16.220※log_tran_ECMWF_new_20180827.log※!!!Info:proc tran ftp://10.0.122.172/ser/station/dat/ecmwf/fcst/ at 16:52:32......※ECMWF_ELE2018082712.DAT.gz transfer completed with status 0. Total time:8.081537 sec. File size:302260969 bytes. File mtime:16:56:22.※!!!Info:proc tran ftp://10.0.74.226//cvs_new/ocf/ecmwf/ at 16:52:40......※ECMWF_ELE2018082712.DAT.gz transfer completed with status 0. Total time:6.457045 sec. File size:302260969 bytes. File mtime:16:56:22.※###############################  process end at 16:52:47  ###############################※";
        String msg4="10.30.16.220※log_tran_NCEP_new_20180827.log※!!!Info:proc tran ftp://10.0.122.172/ser/station/dat/ncep/fcst/ at 16:53:17......※ncep_ELE2018082712.DAT.gz transfer completed with status 0. Total time:5.631895 sec. File size:404688069 bytes. File mtime:16:57:04.※!!!Info:proc tran ftp://10.0.74.226//cvs_new/ocf/ncep/ at 16:53:22......※ncep_ELE2018082712.DAT.gz transfer completed with status 0. Total time:3.638179 sec. File size:404688069 bytes. File mtime:16:57:04.※###############################  process end at 16:53:26  ###############################※";
        String msg5="10.30.16.220※log_tran_RJTD_new_20180827.log※!!!Info:proc tran ftp://10.0.122.172/ser/station/dat/rjtd/fcst/ at 16:51:32......※RJTD_ELE2018082712.DAT.gz transfer completed with status 0. Total time:2.277641 sec. File size:172698119 bytes. File mtime:16:55:23.※!!!Info:proc tran ftp://10.0.74.226//cvs_new/ocf/rjtd/ at 16:51:34......※RJTD_ELE2018082712.DAT.gz transfer completed with status 0. Total time:1.748547 sec. File size:172698119 bytes. File mtime:16:55:23.※###############################  process end at 16:51:36  ###############################※";
        String msg="10.30.16.220※log_tran_OCF_FINAL_20180828.log※!!!Info:proc tran ftp://10.0.122.172/ser/station/dat/wmcf/ocf/final/ at 07:53:02......※WMCF_OCF_FINE2018082800.DAT.gz transfer completed with status 0. Total time:6.122353 sec. File size:339821358 bytes. File mtime:07:56:57.※!!!Info:proc tran ftp://10.0.74.226//cvs_new/ocf/final/ at 07:53:08......※WMCF_OCF_FINE2018082800.DAT.gz transfer completed with status 0. Total time:3.245893 sec. File size:339821358 bytes. File mtime:07:56:57.※###############################  process end at 07:53:11  ###############################※";
        List<String> toEsJsons = new ArrayList<>();

        Pattern ipspattern = Pattern.compile(ips);
        Matcher matcher = ipspattern.matcher(msg);

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

                                obj.put("type", "bhfk");
                                obj.put("name", "bhfk");
                                String time  = arr[6];

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else if(type.equals("ECMWF_new")){
                                String[] arr = matcher.group(1).split("_");
                                //  ECMWF_ELE2018082212.DAT.gz

                                obj.put("type", "ECMWF_new");
                                obj.put("name", "ECMWF_new");
                                String time  = arr[1].replace("ELE","").replace(".DAT.gz","")+"00";

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else if(type.equals("T639_new")){
                                String[] arr = matcher.group(1).split("_");
                                //  T639_ELE2018082212.DAT.gz

                                obj.put("type", "T639_new");
                                obj.put("name", "T639_new");
                                String time  = arr[1].replace("ELE","").replace(".DAT.gz","")+"00";

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else if(type.equals("NCEP_new")){
                                String[] arr = matcher.group(1).split("_");
                                //  ncep_ELE2018082212.DAT.gz

                                obj.put("type", "NCEP_new");
                                obj.put("name", "NCEP_new");
                                String time  = arr[1].replace("ELE","").replace(".DAT.gz","")+"00";

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else if(type.equals("RJTD_new")){
                                String[] arr = matcher.group(1).split("_");
                                //  RJTD_ELE2018082212.DAT.gz

                                obj.put("type", "RJTD_new");
                                obj.put("name", "RJTD_new");
                                String time  = arr[1].replace("ELE","").replace(".DAT.gz","")+"00";

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else if(type.equals("cimiss_to_obs")){
                                String[] arr = matcher.group(1).split("_");
                                //  aglb_obs2018082208.dat

                                obj.put("type", "cimiss_to_obs");
                                obj.put("name", "cimiss_to_obs");
                                String time  = arr[1].replace("obs","").replace(".dat","")+"00";

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

                                obj.put("type", "ocf_new");
                                obj.put("name", "ocf_new");
                                String time  = arr[5];

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

                                obj.put("type", "ocf_update");
                                obj.put("name", "ocf_update");
                                String time  = arr[4].replace("1H","").replace(".DAT","")+"00";

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
                            }
                            else if(type.equals("ocf_jiangji")){
                                String[] arr = matcher.group(1).split("_");
                                //  WMCF_OCF_LST_UPDATE_1H2018082220.DAT

                                obj.put("type", "ocf_jiangji");
                                obj.put("name", "ocf_jiangji");
                                String time  = arr[6]+"00";

                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
                                Date d = df.parse(time);

                                df.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
                                subobj.put("data_time", df.format(d.getTime()));
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
                            System.out.println(123);
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

        }
        System.out.println(JSON.toJSONString(toEsJsons));
    }




    @Test
    public void test3() throws ParseException {

        try {
            ocf_consumer.getHistory();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}

