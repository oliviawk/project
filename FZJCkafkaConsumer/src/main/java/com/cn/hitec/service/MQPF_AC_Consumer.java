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
    private static final Logger logger = LoggerFactory.getLogger(FZJCSendConsumer.class);
    private static String topic = "MQPF_AC";
    private static String type = "MQPF";


    private static String INFO = "!!!Info";
    @Value("${MQPF.NC5M.PATH}")
    private String MQPF_NC5M_PATH;
    @Value("${MQPF.NC1H.PATH}")
    private String MQPF_NC1H_PATH;
    @Value("${MQPF.PNG5M.PATH}")
    private String MQPF_PNG5M_PATH;
    @Value("${MQPF.DISTRIBUTE.220.PATH}")
    private String MQPF_DISTRIBUTE_220_PATH;

    public MQPF_AC_Consumer(@Value("${MQPF.group.id}")String group) {
        super(topic, group, type);
    }


    @Override
    public List<String> processing(String msg) throws ParseException {
        List<String> list = new ArrayList<>();

        SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmm");
        if(msg.indexOf(MQPF_NC5M_PATH) > -1){
            String[] lines = msg.split("※");
            if (lines.length < 1){
                return null;
            }

            boolean isOk = false;
            String ip_addr = lines[0];
            String[] fileNames = lines[1].split("_");
            String strName = fileNames[3].replace(".log","");
            Date occTimeD = null;
            for(int i=0;i < lines.length;i++){
                if (lines[i].indexOf(INFO) > -1){
                    if (lines[i].indexOf(MQPF_NC5M_PATH) > -1){
                        isOk = true;
                    }else {
                        isOk = false;
                    }
                    continue;
                }
                if (!isOk){
                    continue;
                }
                if(lines[i].indexOf("mqpf_") > -1 ){
                    HashMap<String,Object> file =new HashMap();
                    HashMap map = new HashMap();
                    String[] line = lines[i].split( " ");

                    String[] str1 = line[0].split("_");
                    String Dt = str1[1]+str1[2].replace(".nc","");
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
                    map.put("name","MQPF_NC5M");
                    map.put("type","MQPF_NC5M");
                    map.put("occur_time",occTimeD.getTime());
                    map.put("fields",file);
                    list.add(JSON.toJSONString(map));
                }


            }
            list.add("分发");
        }else if(msg.indexOf(MQPF_PNG5M_PATH) > -1){
            String[] lines = msg.split("※");
            if (lines.length < 1){
                return null;
            }
            boolean isOk = false;
            String ip_addr = lines[0];
            String[] fileNames = lines[1].split("_");
            String strName = fileNames[3].replace(".log","");
            Date occTimeD = null;
            for(int i=0;i < lines.length;i++){
                if (lines[i].indexOf(INFO) > -1){
                    if (lines[i].indexOf(MQPF_PNG5M_PATH) > -1){
                        isOk = true;
                    }else {
                        isOk = false;
                    }
                    continue;
                }
                if (!isOk){
                    continue;
                }

                if(lines[i].indexOf("QPFRef") > -1 ){
                    HashMap<String,Object> file =new HashMap();
                    HashMap map = new HashMap();
                    String[] line = lines[i].split( " ");

                    String[] str1 = line[0].split("_");

                    String Dt = str1[1].replace(".png","");
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
                    map.put("name","MQPF_PNG5M");
                    map.put("type","MQPF_PNG5M");
                    map.put("occur_time",occTimeD.getTime());
                    map.put("fields",file);
                    list.add(JSON.toJSONString(map));
                }
            }
            list.add("分发");
        }else if(msg.indexOf(MQPF_NC1H_PATH) > -1){
            //当前没有该类型日志，先返回空
            logger.info(msg);
            return null;
        }else if(msg.indexOf(MQPF_DISTRIBUTE_220_PATH) > -1){
            String[] lines = msg.split("※");
            if (lines.length < 1){
                return null;
            }
            boolean isOk = false;
            String ip_addr = lines[0];
            String[] fileNames = lines[1].split("_");
            String strTime = fileNames[3].replace(".log","");
            Date occTimeD = null;
            for(int i=0;i < lines.length;i++){
                if (lines[i].indexOf(INFO) > -1){
                    if (lines[i].indexOf(MQPF_DISTRIBUTE_220_PATH) > -1){
                        isOk = true;
                    }else {
                        isOk = false;
                    }
                    continue;
                }
                if (!isOk){
                    continue;
                }

//                Z_RADR_I_Z9240_20180727033525_O_DOR_SC_CAP.bin.bz2 transfer completed with status 0. Total time:0.045687 sec. File size:645867 bytes. File mtime:11:36:22.
                if(lines[i].indexOf("Z_RADR_I") > -1 ){
                    HashMap<String,Object> file =new HashMap();
                    HashMap map = new HashMap();
                    String[] line = lines[i].split( " ");

                    String[] str1 = line[0].split("_");
                    String Dt = str1[4];
                    String strType = str1[0]+"_"+str1[1]+"_"+str1[2]+"_"+str1[3];
                    dt.applyPattern("yyyyMMddHHmmss");
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
                    occTimeD = dt.parse(strTime + mtime);

                    file.put("module","采集");
                    file.put("ip_addr",ip_addr);
                    map.put("name","雷达基数据");
                    map.put("type",strType);
                    map.put("occur_time",occTimeD.getTime());
                    map.put("fields",file);
                    list.add(JSON.toJSONString(map));
                }
            }
            list.add("采集");
        }
        return list;
    }
}
