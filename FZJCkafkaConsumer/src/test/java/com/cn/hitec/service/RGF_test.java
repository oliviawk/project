package com.cn.hitec.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class RGF_test {

    @Autowired
    RGF_Consumer rgf_consumer;

    @Test
    public void test1(){

        rgf_consumer.consume();

    }

    @Test
    public  void  test2() throws ParseException {

        String msg = "10.30.16.220※log_tran_eleh_20180802.log※!!!Info:proc tran ftp://10.0.74.170/eleh/ at 10:53:18......※MSP1_PMSC_ELEH_ME_L88_CHN_201808020230_00030-00000.nc transfer completed with status 0. Total time:11.581553 sec. File size:129105920 bytes. File mtime:10:53:18.※!!!Info:proc tran ftp://10.14.83.137/eleh/ at 10:53:29......※MSP1_PMSC_ELEH_ME_L88_CHN_201808020230_00030-00000.nc transfer completed with status 0. Total time:1.161015 sec. File size:129105920 bytes. File mtime:10:53:18.※!!!Info:proc tran ftp://10.0.122.172/ser/grid/nc/obs/10m/ at 10:53:31......※10.30.16.220※log_tran_eleh_20180802.log※!!!Info:proc tran ftp://10.0.74.170/eleh/ at 10:53:32......※MSP1_PMSC_ELEH_ME_L88_CHN_201808020230_00030-00000.nc transfer completed with status 0. Total time:1.380464 sec. File size:129105920 bytes. File mtime:10:53:18.※###############################  process end at 10:53:32  ###############################※";
//        String msg = "10.30.16.220※log_tran_h8_nc_20180802.log※!!!Info:proc tran ftp://10.14.83.137/h8_nc/ at 11:20:18......※10.30.16.220※log_tran_h8_nc_20180802.log※!!!Info:proc tran ftp://10.14.83.137/h8_nc/ at 11:20:33......※10.30.16.220※log_tran_h8_nc_20180802.log※!!!Info:proc tran ftp://10.14.83.137/h8_nc/ at 11:20:48......※HS_H08_20180802_0250_B01_FLDK_R10_S0110.nc transfer completed with status 0. Total time:30.969668 sec. File size:83361792 bytes. File mtime:11:20:18.※!!!Info:proc tran ftp://10.0.74.170///home/docker/TDS/data/h8_nc/ at 11:20:52......※HS_H08_20180802_0250_B01_FLDK_R10_S0110.nc transfer completed with status 21. Total time:18.944589 sec. File size:98548430 bytes. File mtime:11:20:21.※curl_error message:QUOT string not accepted: RNFR HS_H08_20180802_0250_B01_FLDK_R10_S0110.nc.tmp※!!!Info:proc tran ftp://10.0.74.170///home/docker/TDS/data/h8_nc/ at 11:20:54......※HS_H08_20180802_0250_B01_FLDK_R10_S0110.nc transfer completed with status 0. Total time:1.843645 sec. File size:83361792 bytes. File mtime:11:20:18.※###############################  process end at 11:20:55  ###############################※";

        List<String> list = new ArrayList<>();

        SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmm");
        if(msg.indexOf("MSP1_") > -1){
            String[] lines = msg.split("※");
            String ip_addr = lines[0];
            String[] fileNames = lines[1].split("_");
            String strName = fileNames[3].replace(".log","");
            Date occTimeD = null;
            for(int i=0;i < lines.length;i++){
                if(lines[i].indexOf("MSP1_") > -1 ||lines[i].indexOf("mtime") > -1){
                    HashMap<String,Object> file =new HashMap();
                    HashMap map = new HashMap();
                    String[] line = lines[i].split( " ");

                    String[] str1 = line[0].split("_");
                    String Dt = str1[6];
                    int addtime = Integer.parseInt(str1[7].replace("-00000.nc","")) ;

                    dt.applyPattern("yyyyMMddHHmm");
                    Date oldDt = dt.parse(Dt);
                    dt.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(oldDt);
                    cal.add(Calendar.MINUTE, addtime);
                    oldDt = cal.getTime();

                    file.put("data_time",dt.format(oldDt));
                    file.put("event_status",line[5].replace(".",""));
                    file.put("totalTime",line[7].replace("time:",""));
                    Long size = Long.parseLong(line[10].replace("size:",""));
                    file.put("file_size",size);
                    String mtime = line[13].replace("mtime:","").replace(".","");
                    file.put("mtime",mtime);
                    dt.applyPattern("yyyyMMddHH:mm:ss");
                    occTimeD = dt.parse(strName + mtime);

                    file.put("module","分发");
                    file.put("ip_addr",ip_addr);
                    map.put("name","eleh");
                    map.put("type","eleh");
                    map.put("occur_time",occTimeD.getTime());
                    map.put("fields",file);

                    System.out.println(map.toString());

                }
            }
        }else if(msg.indexOf("HS_") > -1 ){
            String[] lines = msg.split("※");
            String ip_addr = lines[0];
            String[] fileNames = lines[1].split("_");
            String strName = fileNames[4].replace(".log","");
            Date occTimeD = null;
            for(int i=0;i < lines.length;i++){
                if(lines[i].indexOf("HS_") > -1 && lines[i].indexOf("mtime") > -1){
                    HashMap<String,Object> file =new HashMap();
                    HashMap map = new HashMap();
                    String[] line = lines[i].split( " ");

                    String[] str1 = line[0].split("_");
                    String Dt = str1[2]+str1[3];

                    dt.applyPattern("yyyyMMddHHmm");
                    Date oldDt = dt.parse(Dt);
                    dt.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");

                    file.put("data_time",dt.format(oldDt));
                    file.put("event_status",line[5].replace(".",""));
                    file.put("totalTime",line[7].replace("time:",""));
                    Long size = Long.parseLong(line[10].replace("size:",""));
                    file.put("file_size",size);
                    String mtime = line[13].replace("mtime:","").replace(".","");
                    file.put("mtime",mtime);
                    dt.applyPattern("yyyyMMddHH:mm:ss");
                    occTimeD = dt.parse(strName + mtime);

                    file.put("module","分发");
                    file.put("ip_addr",ip_addr);
                    map.put("name","h8_nc");
                    map.put("type","h8_nc");
                    map.put("occur_time",occTimeD.getTime());
                    map.put("fields",file);

                    System.out.println(map.toString());

                }
            }
        }

    }
}
