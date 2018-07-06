package com.cn.hitec.service;

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
    private static String groupid = "mqpf_ac1";

//    @Value("${FZJC.send.target.ips}")
//    private String ips;
//    @Value("${FZJC.datatype}")
//    private String datatypes;
//    @Value("${collect}")
//    private String collect;
//    @Value("${send}")
//    private String send;


    public MQPF_AC_Consumer() {
        super(topic, groupid, type);
    }


    @Override
    public List<String> processing(String msg) throws ParseException {
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("※mqpf_");
        Pattern pattern1 = Pattern.compile("※QPFRef_");
        Matcher m = pattern.matcher(msg);
        Matcher m1 = pattern1.matcher(msg);

        SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat dt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        SimpleDateFormat dt3 = new SimpleDateFormat("yyyyMMddHH:mm:ss");
        if(m.find()){
//            log_tran_mqpfNc5m_20180705.log
            String[] lines = msg.split("※");
            String ip_addr = lines[0];
            String[] fileNames = lines[1].split("_");
            String strName = fileNames[3].replace(".log","");
            Date occTimeD = null;
            for(int i=0;i < lines.length;i++){
                if(lines[i].indexOf("mqpf_") > -1 ){
                    HashMap<String,Object> file =new HashMap();
                    HashMap map = new HashMap();
                    String[] line = lines[i].split( " ");
                    for(int j = 0;j<line.length;j++){
                        if(line[j].indexOf("mqpf_") > -1 ){
                            String[] str1 = line[j].split("_");
                            String Dt = str1[1]+str1[2].replace(".nc","");
                            Date oldDt = dt.parse(Dt);
                            file.put("data_time",dt2.format(oldDt));
                        }
                    }
                    file.put("event_status",line[5].replace(".",""));
                    file.put("totalTime",line[7].replace("time:",""));
                    Long size = Long.parseLong(line[10].replace("size:",""));
                    file.put("file_size",size);
                    String mtime = line[13].replace("mtime:","").replace(".","");
                    file.put("mtime",mtime);
                    occTimeD = dt3.parse(strName + mtime);

                    file.put("module","分发");
                    file.put("ip_addr",ip_addr);
                    map.put("name","MQPF_NC5M");
                    map.put("type","MQPF_NC5M");
                    map.put("occur_time",occTimeD.getTime());
                    map.put("fields",file);
                    JSONObject jsonObject = JSONObject.fromObject(map);
//                    System.out.println(jsonObject.toString());
                    String json = jsonObject.toString();
                    list.add(json);
                }

            }
        }
        if(m1.find()){
            String[] lines = msg.split("※");
            if (lines.length < 1){
                return list;
            }
            String ip_addr = lines[0];
            String[] fileNames = lines[1].split("_");
            String strName = fileNames[3].replace(".log","");
            Date occTimeD = null;
            for(int i=0;i < lines.length;i++){
                if(lines[i].indexOf("QPFRef") > -1 ){
                    HashMap<String,Object> file =new HashMap();
                    HashMap map = new HashMap();
                    String[] line = lines[i].split( " ");
                    for(int j = 0;j<line.length;j++){
                        if(line[j].indexOf("QPFRef") > -1 ){
                            String[] str1 = line[j].split("_");

                            String Dt = str1[1].replace(".png","");
                            Date oldDt = dt.parse(Dt);
                            file.put("data_time",dt2.format(oldDt));
                        }
                    }
                    file.put("event_status",line[5].replace(".",""));
                    file.put("totalTime",line[7].replace("time:",""));
                    Long size = Long.parseLong(line[10].replace("size:",""));
                    file.put("file_size",size);
                    String mtime = line[13].replace("mtime:","").replace(".","");
                    file.put("mtime",mtime);
                    occTimeD = dt3.parse(strName + mtime);

                    file.put("module","分发");
                    file.put("ip_addr",ip_addr);
                    map.put("name","MQPF_PNG5M");
                    map.put("type","MQPF_PNG5M");
                    map.put("occur_time",occTimeD.getTime());
                    map.put("fields",file);
                    JSONObject jsonObject = JSONObject.fromObject(map);
//                    System.out.println(jsonObject.toString());
                    String json = jsonObject.toString();
                    list.add(json);
                }
            }
        }
        return list;
    }
}
