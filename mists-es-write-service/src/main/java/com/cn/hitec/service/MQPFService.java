package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.bean.AlertBeanNew;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.repository.jpa.DataInfoRepository;
import com.cn.hitec.tools.AlertType;
import com.cn.hitec.tools.Pub;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.omg.CORBA.ORB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MQPFService {
    private static final Logger logger = LoggerFactory.getLogger(MQPFService.class);
    @Autowired
    private ESRepository es;


    /**
     * MQPF业务 雷达采集日志入库
     * @param listJson
     * @return
     */
    public int insertMQPFCollectionData(List<String> listJson){
        int error_num = 0;
        int listSize = 0;
        try {
            if (listJson == null || listJson.size() < 1) {
                logger.error("参数为空");
                return 0;
            }
            listSize = listJson.size();
            Map<String, Object> map = null;
            Map<String, Object> fields = null;
            String index = "";
            String type = "MQPF";
            for (String json : listJson) {
                try {
                    if (StringUtils.isEmpty(json)) {
                        logger.error("数据为空");
                        error_num++;
                        continue;

                    }
                    //给关键变量赋值
                    map = JSON.parseObject(json);
                    fields = (Map<String, Object>) map.get("fields");

                    String subType = map.get("type").toString(); // 数据名称
                    String name = map.get("name").toString();
                    String subModule = fields.get("module").toString();
                    String subIp = fields.get("ip_addr").toString();
                    String strDataTime = fields.get("data_time").toString();
                    String subKey = "MQPF"+","+subType+","+subModule+","+subIp;
                    String str_id = Pub.MD5(subKey + "," + strDataTime);

                    Date dataTime = Pub.transform_StringToDate(strDataTime, "yyyy-MM-dd HH:mm:ss.SSSZ");
                    index = Pub.Index_Head + Pub.transform_DateToString(dataTime, Pub.Index_Food_Simpledataformat);

                    map.put("aging_status", "正常");
                    if(fields.containsKey("event_status")){
                        // 判断数据状态
                        if (!fields.get("event_status").toString().toUpperCase().equals("OK")
                                && !fields.get("event_status").toString().equals("0")) {
                            map.put("aging_status", "异常");
                        }
                    }

                    // 数据入库
                    es.bulkProcessor.add(new IndexRequest(index, "MQPF", str_id).source(map));
//                    logger.info(JSON.toJSONString(map));
//						DIMap = null;

                    //开始预生成接下来加工、分发的数据
                    Map<String , Object> prebuilt_220Map = new HashMap<>();
                    Map<String , Object> prebuiltFields_220Map = new HashMap<>();
                    String subIp_220 = "10.30.16.220";


                    prebuilt_220Map.put("aging_status","未处理");
                    prebuilt_220Map.put("startMoniter","yes");
                    prebuilt_220Map.put("name",name);
                    prebuilt_220Map.put("type",subType);
//                    prebuilt_220Map.put("last_time",Pub.transform_DateToString(new Date(dataTime.getTime() + 90000),"yyyy-MM-dd HH:mm:ss.SSSZ"));
//                    prebuilt_220Map.put("should_time",Pub.transform_DateToString(new Date(dataTime.getTime() + 60000),"yyyy-MM-dd HH:mm:ss.SSSZ"));

                    prebuiltFields_220Map.put("data_time",strDataTime);
                    prebuiltFields_220Map.put("file_name",fields.get("file_name").toString());
                    prebuiltFields_220Map.put("file_size_define","");
                    prebuiltFields_220Map.put("ip_addr",subIp_220);
                    prebuiltFields_220Map.put("module","采集");
                    prebuiltFields_220Map.put("start_time",fields.get("start_time").toString());
                    prebuiltFields_220Map.put("end_time",fields.get("end_time").toString());

                    prebuilt_220Map.put("fields",prebuiltFields_220Map);

                    String str_id_220 = Pub.MD5("MQPF"+","+subType+","+subModule+","+subIp_220 + "," + strDataTime);

                    // 查询是否预生成过，预生成数据入库
                    GetResponse response = es.client.prepareGet(index, type, str_id_220).get();
                    if (response != null && response.getSource() != null){
                       continue;
                    }
                    es.bulkProcessor.add(new IndexRequest(index, type, str_id_220).source(prebuilt_220Map));

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("mqpf 错误数据："+json);
                    error_num++;
                }
            }
            System.out.println("mqpf---------------------------------------------------------------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return listSize - error_num;
        }

    }


}
