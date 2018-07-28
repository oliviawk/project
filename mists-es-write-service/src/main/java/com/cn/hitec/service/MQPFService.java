package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.bean.AlertBeanNew;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.repository.jpa.DataInfoRepository;
import com.cn.hitec.tools.AlertType;
import com.cn.hitec.tools.Pub;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class MQPFService {
    private static final Logger logger = LoggerFactory.getLogger(MQPFService.class);
    @Autowired
    private ESRepository es;
    @Autowired
    ESClientAdminService esClientAdminService;
    @Autowired
    AlertService alertService;
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
                    String subKey = type+","+subType+","+subModule+","+subIp;
                    String str_id = Pub.MD5(subKey + "," + strDataTime);

                    Date dataTime = Pub.transform_StringToDate(strDataTime, "yyyy-MM-dd HH:mm:ss.SSSZ");
                    index = Pub.Index_Head + Pub.transform_DateToString(dataTime, Pub.Index_Food_Simpledataformat);

                    map.put("aging_status", "正常");
                    fields.put("event_info", "正常");
                    if(fields.containsKey("event_status")){
                        // 判断数据状态
                        if (!fields.get("event_status").toString().toUpperCase().equals("OK")
                                && !fields.get("event_status").toString().equals("0")) {
                            map.put("aging_status", "异常");
                            fields.put("event_info", "数据异常");
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

                    String strFileSize = fields.get("file_size").toString();
                    prebuiltFields_220Map.put("data_time",strDataTime);
                    prebuiltFields_220Map.put("file_name",fields.get("file_name").toString());
                    prebuiltFields_220Map.put("file_size_define",strFileSize+","+strFileSize);
                    prebuiltFields_220Map.put("ip_addr",subIp_220);
                    prebuiltFields_220Map.put("module","采集");
                    prebuiltFields_220Map.put("start_time",fields.get("start_time").toString());
                    prebuiltFields_220Map.put("end_time",fields.get("end_time").toString());

                    prebuilt_220Map.put("fields",prebuiltFields_220Map);

                    String str_id_220 = Pub.MD5(type+","+subType+","+subModule+","+subIp_220 + "," + strDataTime);

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


    /**
     * MQPF业务 220 雷达采集日志入库
     * @param listJson
     * @return
     */
    public int insertMQPFCollectionData_220(String type , List<String> listJson){
        int error_num = 0;
        int listSize = 0;
        try {
            if (StringUtils.isEmpty(type) || listJson == null || listJson.size() < 1) {
                logger.error("参数为空");
                return 0;
            }
            listSize = listJson.size();
            Map<String, Object> map = null;
            Map<String, Object> fields = null;
            String index = "";
//            String type = "MQPF";
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
                    String subKey = type+","+subType+","+subModule+","+subIp;
                    String str_id = Pub.MD5(subKey + "," + strDataTime);

                    Date dataTime = Pub.transform_StringToDate(strDataTime, "yyyy-MM-dd HH:mm:ss.SSSZ");
                    index = Pub.Index_Head + Pub.transform_DateToString(dataTime, Pub.Index_Food_Simpledataformat);

                    String[] indices = Pub.getIndices(dataTime, 1); // 获取今天和昨天的
                    Map<String,Object> hitsSourceMap = esClientAdminService.getDocumentById(es,indices,type,str_id);

                    if (hitsSourceMap.containsKey("_id")) { // 如果查询到id
                        AlertBeanNew alertBean = null;
                        String alertType = "alert";
                        index = hitsSourceMap.get("_index").toString();
                        Map<String, Object> hitsSource_fields = (Map<String, Object>) hitsSourceMap.get("fields");
                        map.put("name", hitsSourceMap.containsKey("name") ? hitsSourceMap.get("name") : "");


                        // 当 数据库里的数据 和 当前数据 一样时（目前是按照数据状态来判断），放弃掉该条数据
                        if (hitsSource_fields.containsKey("event_status") && fields.get("event_status").toString()
                                .equals(hitsSource_fields.get("event_status"))) {
                            log.warn("MQPF:--舍弃掉 相同的数据：" + json);
                            continue;
                        }

                        /*-------6.19 设置文件名新代码*/
                        if (hitsSource_fields.containsKey("file_name") && !StringUtils.isEmpty(hitsSource_fields.get("file_name"))) {
                            if (fields.containsKey("file_name") && !StringUtils.isEmpty(fields.get("file_name"))) {
                                if (fields.get("file_name").toString().lastIndexOf("/") <= 0) {
                                    String old_fileName = hitsSource_fields.get("file_name").toString();
                                    old_fileName = old_fileName.substring(0, old_fileName.lastIndexOf("/") + 1);
                                    fields.put("file_name", old_fileName + fields.get("file_name").toString().replace("/", ""));
                                }
                            } else {
                                fields.put("file_name", hitsSource_fields.get("file_name"));
                            }
                        }

                        // 确定是否进行 数据状态 告警
                        if (fields.get("event_status").toString().toUpperCase().equals("OK")
                                || fields.get("event_status").toString().equals("0")) {
                            map.put("aging_status", "正常");

                            //判断文件大小是否正常
                            String strSizeDefine = hitsSource_fields.containsKey("file_size_define") ? hitsSource_fields.get("file_size_define").toString():"";
                            if (!StringUtils.isEmpty(strSizeDefine)){
                                long lFileSize = fields.containsKey("file_size") ? Long.parseLong(fields.get("file_size").toString()): 0L;
                                String[] strSizeDefines = strSizeDefine.split(",");
                                long mix = 0L;
                                long max = 0L;
                                //应为预生成数据是固定的，所以这里只有这一种比较方法
                                if(strSizeDefines.length == 2){
                                    mix = Long.parseLong(strSizeDefines[0]);
                                    max = Long.parseLong(strSizeDefines[1]);
                                    if (mix > lFileSize || lFileSize > max){
                                        map.put("aging_status", "异常");
                                        String alertTitle = subType + "--" + fields.get("module") + "--"
                                                + fields.get("data_time") + " 时次产品 ,文件大小 不在正常范围内。";
                                        fields.put("event_info", "文件大小异常: "+ "阈值范围为 "+mix+"--"+max+" byte ,实际值为 "+lFileSize+" byte");
                                        // 初始化告警实体类
                                        alertBean = alertService.getAlertBean(AlertType.FILEEX.getValue(), alertTitle, type,map);
                                    }
                                }
                            }

                        } else {
                            // 判断如果原数据是正确的， 新数据是错误的， 舍弃新数据
                            if (hitsSource_fields.containsKey("event_status")
                                    && (hitsSource_fields.get("event_status").toString().toUpperCase().equals("OK")
                                    || hitsSource_fields.get("event_status").toString().equals("0"))) {
                                log.warn("MQPF:--舍弃掉 预修改错误的数据：" + json);
                                continue;
                            }
                            map.put("aging_status", "异常");
                            String alertTitle = subType + "--" + fields.get("module") + "--"
                                    + fields.get("data_time") + " 时次产品发生错误，分发状态码是 "+ fields.get("event_status");
                            fields.put("event_info", "数据状态码："+fields.get("event_status"));
                            // 初始化告警实体类
                            alertBean = alertService.getAlertBean(AlertType.ABNORMAL.getValue(), alertTitle, type,map);
                        }


                        if (alertBean != null) {
                            /*   需要修改该方法  2018.3.20没有修改   */
                            alertService.alert_MQPF(es,index, alertType, alertBean); // 生成告警
                            alertBean = null;
                        }
                        // 数据入库
                        es.bulkProcessor.add(new IndexRequest(index, type, str_id).source(map));
//						DIMap = null;
                    } else {
                        if(fields.containsKey("event_status")){
                            // 判断数据状态
                            if (fields.get("event_status").toString().toUpperCase().equals("OK")
                                    || fields.get("event_status").toString().equals("0")) {
                                map.put("aging_status", "正常");
                                // fields.put("event_info","正常");
                            } else {
                                map.put("aging_status", "异常");
                            }
                        }

                        log.info("MQPF:这是一条未查询到的数据,类型为：{}, 时次为：{}", subType, fields.get("data_time"));
                        es.bulkProcessor.add(new IndexRequest(index, type,str_id).source(map));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("MQPF: 错误数据："+json);
                    error_num++;
                }
            }
            System.out.println("MQPF:---------------------------------------------------------------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return listSize - error_num;
        }

    }

}
