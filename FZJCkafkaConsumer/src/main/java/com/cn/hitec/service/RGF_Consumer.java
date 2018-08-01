package com.cn.hitec.service;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RGF_Consumer extends MsgConsumer{

    private static final Logger logger = LoggerFactory.getLogger(RGF_Consumer.class);
    private static String topic = "RGF";
    private static String type = "RGF";

    private static String INFO = "!!!Info";

    public RGF_Consumer(@Value("${RGF.group.id}")String group) {
        super(topic, group, type);
    }

    @Override
    public List<String> processing(String msg) throws ParseException {
        List<String> list = new ArrayList<>();

        SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmm");
        if(msg.indexOf("MSP1_") > -1){
            String[] lines = msg.split("※");
            String ip_addr = lines[0];
            String[] fileNames = lines[1].split("_");
            String strName = fileNames[3].replace(".log","");
            Date occTimeD = null;
            for(int i=0;i < lines.length;i++){
                if(lines[i].indexOf("MSP1_") > -1 ){
                    HashMap<String,Object> file =new HashMap();
                    HashMap map = new HashMap();
                    String[] line = lines[i].split( " ");

                    String[] str1 = line[0].split("_");
                    String Dt = str1[6];
                    int addtime = Integer.parseInt(str1[7].replace("-00000.nc","")) ;
                    System.out.println(addtime);

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
                    map.put("name","RGF");
                    map.put("type","RGF");
                    map.put("occur_time",occTimeD.getTime());
                    map.put("fields",file);

                    System.out.println(map.toString());

                }
            }
        }
        return list;
    }

}
