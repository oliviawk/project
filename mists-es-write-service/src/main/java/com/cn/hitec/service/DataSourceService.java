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
public class DataSourceService {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceService.class);
    /*-------------------------------------->数据源代码<-------------------------------------------*/
    @Autowired
    private ESRepository es;
    @Autowired
    private ESClientAdminService esClientAdminService;
    @Autowired
    AlertService alertService;

    @Autowired
    DataInfoRepository dataInfoRepository;

    /**
     * 添加数据 指定id
     *
     * @param index
     * @param type
     * @param id
     * @param json
     */
    public void add(String index, String type, String id, String json) {

        try {
            //判断数据是否被修改过(aging_status 不是'未处理'状态 ，表示为修改过)，如果修改过，则不再修改
            Map<String, Object> tempMap = getDocumentById(new String[]{index},type,id);
            if (tempMap.containsKey("aging_status") && !tempMap.get("aging_status").equals("未处理")){
                logger.info("已修改："+id);
                return;
            }
            es.bulkProcessor.add(new IndexRequest(index, type, id).source(json, XContentType.JSON));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加数据
     * @param index
     * @param type
     * @param listJson
     * @return
     */
    public int add(String index, String type, List<String> listJson) {
        int error_num = 0;
        int listSize = 0;
        try {
            if (listJson == null || listJson.size() < 1) {
                return 0;
            }
            listSize = listJson.size();
            for (String json : listJson) {
                if (StringUtils.isEmpty(json)) {
                    error_num++;
                    continue;
                }
                JSONObject jsonObject = JSON.parseObject(json);
                if (!jsonObject.containsKey("_id")){
                    es.bulkProcessor.add(new IndexRequest(index, type).source(json, XContentType.JSON));
                }else{
                    String id = Pub.MD5(jsonObject.getString("_id"));
                    jsonObject.remove("_id");
                    es.bulkProcessor.add(new IndexRequest(index, type,id).source(jsonObject.toJSONString(), XContentType.JSON));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e+"");
        } finally {
            return listSize - error_num;
        }
    }

    /**
     * 数据源数据入库解析
     * @param dataList
     * {
     *
     * }
     */
    public void update_dataSource(List<Map> dataList){
        if (dataList == null ){
            return ;
        }

        for (Map dataMap  : dataList){
            try {
                Map<String,Object> fieldsMap = (Map<String, Object>) dataMap.get("fields");

                Date occrTime = Pub.transform_StringToDate(dataMap.get("occur_time").toString(),"yyyy-MM-dd HH:mm:ss");
                String str_index = Pub.Index_Head + Pub.transform_DateToString(occrTime, Pub.Index_Food_Simpledataformat);
                String str_type = "DATASOURCE";

                String sub_type = dataMap.get("type").toString();
                String dataTime = fieldsMap.get("data_time").toString();
                String module = fieldsMap.get("module").toString();
                String ipAddr = fieldsMap.get("ip_addr").toString();

                String subKey = str_type+","+sub_type+","+module+","+ipAddr;
                String str_id = Pub.MD5(subKey+","+dataTime);

                logger.info(""+subKey+","+dataTime);

//                if (Pub.alert_time_map.containsKey(subKey)) {
//
//                }
                String[] indices = Pub.getIndices(occrTime, 1); // 获取今天和昨天的
                Map<String,Object> resultMap = esClientAdminService.getDocumentById(es,indices, str_type, str_id) ;
                logger.info("indices :{} , str_type: {} , str_id :{}" , indices,str_type,str_id);

                List<Object> curmodules = dataInfoRepository.findAlertRules(str_type,module,sub_type,ipAddr);
                JSONArray rulesArray = JSON.parseArray(JSON.toJSONString(curmodules));
                if(rulesArray.size() > 0){
                    rulesArray = (JSONArray)rulesArray.get(0);
                }

                if(resultMap.containsKey("_id")){
                    AlertBeanNew alertBean = null;
                    String alertType = "alert";
                    Map<String, Object> hitsSource_fields = (Map<String, Object>) resultMap.get("fields");
                    if (hitsSource_fields.containsKey("event_status") && fieldsMap.get("event_status").toString()
                            .equals(hitsSource_fields.get("event_status"))) {
                        logger.warn("--舍弃掉 相同的数据：" + JSON.toJSONString(dataMap));
                        continue;
                    }else{
                        dataMap.put("should_time",
                                resultMap.containsKey("should_time") ? resultMap.get("should_time") : "");
                        dataMap.put("last_time",
                                resultMap.containsKey("last_time") ? resultMap.get("last_time") : "");
                        dataMap.put("name",
                                resultMap.containsKey("name") ? resultMap.get("name") : "");
                        // 确定是否进行 数据状态 告警
                        if ("0".equals(fieldsMap.get("event_status").toString())){
                            Date nowDate = Pub.transform_StringToDate(dataMap.get("occur_time").toString(),"yyyy-MM-dd HH:mm:ss");
                            Date shouldDate = Pub.transform_StringToDate(resultMap.get("should_time").toString(), "yyyy-MM-dd HH:mm:ss");
                            Date lastDate = Pub.transform_StringToDate(resultMap.get("last_time").toString(), "yyyy-MM-dd HH:mm:ss");
                            // 确定是否 时效告警 ,修改时效状态
                            if (nowDate.getTime() - lastDate.getTime() >= 1000) {
                                dataMap.put("aging_status", "迟到");
                                String temp = Pub.transform_time((int) (nowDate.getTime() - shouldDate.getTime()));
                                String alertTitle = "数据源数据，" + sub_type + ",IP:"+ipAddr
                                        + "时次："+dataTime + " 产品 ，延迟" + temp + "到达";

                                fieldsMap.put("event_info", "延迟" + temp + "到达");
                                // 初始化告警实体类
                                alertBean = alertService.getAlertBean(AlertType.DELAY.getValue(), alertTitle, str_type, dataMap);
                            } else {
                                dataMap.put("aging_status", "正常");
                            }
                        }else{
                            dataMap.put("aging_status", "异常");
                            String alertTitle = "数据源数据，" + sub_type + ",IP:"+ipAddr
                                    + "时次："+dataTime + " 产品 ，发送失败";
                            fieldsMap.put("event_info", "数据异常");
                                // 初始化告警实体类
                            alertBean = alertService.getAlertBean(AlertType.ABNORMAL.getValue(), alertTitle, str_type,dataMap);
                        }
                    }

                    if (alertBean != null) {
                        alertService.alert(es,str_index, alertType, alertBean,rulesArray); // 生成告警
                        alertBean = null;
                    }else{
                        if(rulesArray.size() > 0){
                            //告警状态正常置零告警次数
                            if(rulesArray.getInteger(3) != 0){
                                dataInfoRepository.resetAlertCnt(rulesArray.getLongValue(0));
                            }
                            //无告警时提前到达是否提示
                            if(rulesArray.getInteger(4) == 1){
                                String alertTitle = "数据源数据，" + sub_type + ",IP:"+ipAddr
                                        + "时次："+dataTime + " 产品到达";
                                alertBean = alertService.getAlertBean(AlertType.NOTE.getValue(), alertTitle, str_type,dataMap);
                                alertService.alert(es,str_index, alertType, alertBean,rulesArray);
                            }
                        }
                    }
                    // 数据入库
                    es.bulkProcessor.add(new IndexRequest(str_index, str_type, str_id).source(dataMap));

                }else{
                    logger.info("这是一条新数据源数据："+JSON.toJSONString(dataMap));
                    es.bulkProcessor.add(new IndexRequest(str_index, str_type, str_id).source(dataMap, XContentType.JSON));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public int deleteById(String str_index, String str_type,String str_id){
        int num;
        DeleteResponse deleteResponse = es.client.prepareDelete(str_index,str_type,str_id).get();
        RestStatus restStatus = deleteResponse.status();
        num = restStatus.getStatus();
        return num;
    }

    /**
     * 查询单条数据
     *
     * @param indexs
     * @param type
     * @param id
     * @return
     */
    public Map<String, Object> getDocumentById(String[] indexs, String type, String id) {
        Map<String, Object> resultMap = new HashMap<>();
        try {

            String[] indices = esClientAdminService.indexExists(es,indexs);
            if (indices == null || indices.length < 1) {
                return resultMap;
            }
            for (String s : indices){
//			    System.out.println(s+"--"+type+"---"+id);
                GetResponse response = es.client.prepareGet(s, type, id).get();
                if (response != null && response.getSource() != null){
                    resultMap = response.getSource();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            resultMap = new HashMap<>();
        } finally {
            return resultMap;
        }

    }
}
