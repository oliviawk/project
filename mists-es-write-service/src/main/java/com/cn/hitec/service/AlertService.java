package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.AlertBeanNew;
import com.cn.hitec.domain.Users;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.repository.jpa.UsersRepository;
import com.cn.hitec.tools.Pub;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Description: 这里是描述信息
 * @author: fukl
 * @data: 2017年10月23日 14:25
 */
@Slf4j
@Service
public class AlertService {

    @Autowired
    private ESRepository es;
    @Autowired
    KafkaProducer kafkaProducer;
    @Autowired
    UsersRepository usersRepository;

    /**
     * 获取告警id
     * @param index
     * @param type
     * @param id
     * @return
     */
    public String getDocumentById(String index , String type , String id){
        String documentId = null;
        try {

            GetResponse response = es.client.prepareGet(index, type, id).get();
            if (response != null && response.isExists()){
                documentId = response.getId();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            return documentId;
        }
    }



    /**
     * 告警信息生成 参数必填
     * @param index
     * @param type
     * @param alertBean
     * @throws Exception
     */
    public void alert(String index , String type , AlertBeanNew alertBean){

        try {

            //判断是否重复告警
            String dataName = Pub.dataNameMap.containsKey(alertBean.getDataName())? Pub.dataNameMap.get(alertBean.getDataName()) : alertBean.getDataName();
            String str_id = Pub.MD5(alertBean.getGroupId()+","+dataName+","+alertBean.getData_time());

            String documentId = getDocumentById(index,type,str_id);

            if(documentId != null){ //如果有ID
                //如果是超时、异常的告警，说明是重复，过滤掉。 如果是迟到的数据，修改告警信息（取消告警）
                if("03".equals(alertBean.getAlertType())){
                    //保存告警信息
                    es.bulkProcessor.add(new IndexRequest(index,type,documentId)
                            .source(JSON.toJSONString(alertBean), XContentType.JSON));
                }

            }else{
                //保存告警信息
                IndexResponse response = es.client.prepareIndex(index,type,str_id)
                        .setSource(JSON.toJSONString(alertBean),XContentType.JSON).get();

                documentId = response.getId();
            }

            if(StringUtils.isEmpty(documentId)){
                throw new Exception("插入数据失败");
            }


            boolean isAlert_parent = false;
            //判断上游是否告警，如果告警，则该条告警不进行微信、短信告警
            if("分发".equals(alertBean.getModule())){
                AlertBeanNew alertBean_CJ = JSON.parseObject(JSON.toJSONString(alertBean),AlertBeanNew.class);
                alertBean_CJ.setGroupId(alertBean_CJ.getGroupId().substring(0,alertBean_CJ.getGroupId().lastIndexOf("_"+Pub.moduleMap.get("分发").toString())) + "_" +Pub.moduleMap.get("采集").toString());
                // 根据id ，查询数据是否存在
                String dataName_cj = Pub.dataNameMap.containsKey(alertBean_CJ.getDataName())? Pub.dataNameMap.get(alertBean_CJ.getDataName()) : alertBean_CJ.getDataName();
                String id_cj = getDocumentById(index,type,Pub.MD5(alertBean_CJ.getGroupId()+","+dataName_cj+","+alertBean_CJ.getData_time()));
                if (StringUtils.isEmpty(id_cj)){
                    AlertBeanNew alertBean_JG = JSON.parseObject(JSON.toJSONString(alertBean),AlertBeanNew.class);
                    alertBean_JG.setGroupId(alertBean_JG.getGroupId().substring(0,alertBean_JG.getGroupId().lastIndexOf("_"+Pub.moduleMap.get("分发").toString())) + "_" +Pub.moduleMap.get("加工").toString());
                    String dataName_jg = Pub.dataNameMap.containsKey(alertBean_JG.getDataName())? Pub.dataNameMap.get(alertBean_JG.getDataName()) : alertBean_JG.getDataName();
                    String id_jg = getDocumentById(index,type,Pub.MD5(alertBean_JG.getGroupId()+","+dataName_jg+","+alertBean_JG.getData_time())) ;
                    if (!org.apache.commons.lang.StringUtils.isEmpty(id_jg)){
                        isAlert_parent = true;
                        log.info("-------> 存在加工告警");
                    }
                }else{
                    isAlert_parent = true;
                    log.info("-------> 存在采集告警");
                }

            }else if("加工".equals(alertBean.getModule())){
                AlertBeanNew alertBean_CJ = JSON.parseObject(JSON.toJSONString(alertBean),AlertBeanNew.class);
                alertBean_CJ.setGroupId(alertBean_CJ.getGroupId().substring(0,alertBean_CJ.getGroupId().lastIndexOf("_"+Pub.moduleMap.get("加工").toString())) + "_" +Pub.moduleMap.get("采集").toString());

                String dataName_cj = Pub.dataNameMap.containsKey(alertBean_CJ.getDataName())? Pub.dataNameMap.get(alertBean_CJ.getDataName()) : alertBean_CJ.getDataName();
                log.info("index:{},type:{},id:{}",index,type,Pub.MD5(alertBean_CJ.getGroupId()+","+dataName_cj+","+alertBean_CJ.getData_time()));
                String id_cj = getDocumentById(index,type,Pub.MD5(alertBean_CJ.getGroupId()+","+dataName_cj+","+alertBean_CJ.getData_time()));
                if(!StringUtils.isEmpty(id_cj)){
                    isAlert_parent = true;
                    log.info("-------> 存在采集告警");
                }
            }

            //如果上游告警了，那么此条告警不生成
            if(!isAlert_parent){
                log.warn("生成微信、短信告警："+JSON.toJSONString(alertBean));

                //                //发送消息并推送
//                kafkaProducer.sendMessage("ALERT",null,JSON.toJSONString(alertBean));

                /* 生成 微信告警信息*/
                String[] s = alertBean.getGroupId().split("_");
                if(s.length != 3){
                    System.err.println(JSON.toJSONString(s));
                    return ;
                }
                String service_type = s[1];
                String subName = alertBean.getDataName();
                String module = s[2];
                String ipAddr = alertBean.getIpAddr();


                Map<String,Object> strategyMap = null;
                String strKey = service_type+","+subName+","+Pub.moduleMapGet.get(module)+","+ipAddr;
//                log.info("配置map:"+Pub.DI_ConfigMap.size()+",--"+strKey);
                if(Pub.DI_ConfigMap.containsKey(strKey)){
                    strategyMap = (Map<String, Object>) Pub.DI_ConfigMap.get(strKey);
                    if(strategyMap == null ){
                        System.err.println("创建告警信息失败");
                        return ;
                    }
                    String weChartContent = strategyMap.get("wechart_content").toString();
                    String smsContent = strategyMap.get("sms_content").toString();
                    String wechart_send_enable = strategyMap.get("wechart_send_enable").toString();
                    String sms_send_enable = strategyMap.get("sms_send_enable").toString();

                    //转换微信格式告警信息
                    weChartContent = Pub.transformTitle(weChartContent,alertBean);

                    if("1".equals(wechart_send_enable)){
                        //查询发送的用户

                        String strParentId = strategyMap.get("send_users").toString();
                        long parentId = Long.parseLong(strParentId);
                        List<Users> usersList = usersRepository.findAllByPid(parentId);
                        String strUsers = "";
                        for (Users use : usersList){
                            if ("".equals(strUsers)){
                                strUsers += use.getWechart();
                            }else {
                                strUsers += "|"+use.getWechart();
                            }

                        }
                        // 存入微信待发送消息
                        Map<String,Object> weichartMap = new HashMap<>();
//                        weichartMap.put("sendUser","QQ670779441|FuTieQiang");      //测试
                        weichartMap.put("sendUser", StringUtils.isEmpty(strUsers) ? "@all":strUsers);         //正式
                        weichartMap.put("alertTitle",weChartContent);
                        weichartMap.put("isSend","false");
                        weichartMap.put("send_time",0);
                        weichartMap.put("create_time",System.currentTimeMillis());
                        es.bulkProcessor.add(new IndexRequest(index,"sendWeichart")
                                .source(JSON.toJSONString(weichartMap), XContentType.JSON));

                        System.out.println("------生成微信");
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

    }

    /**
     * 生成告警类
     * @param alertType 告警类型
     * @param alertTitle    告警标题
     * @param map   元数据
     * @return
     * @desc 3.20修改新代码
     */
    public AlertBeanNew getAlertBean(String alertType , String alertTitle, String str_type,  Map<String,Object> map){
        AlertBeanNew alertBean = null;

        try {

            Map<String,Object> fields = (Map<String, Object>) map.get("fields");
            String module = fields.get("module").toString();
            alertBean = new AlertBeanNew();
            alertBean.setType("SYSTEM.ALARM.EI");
            alertBean.setName(""+str_type+"业务告警");
            alertBean.setMessage(str_type+"业务告警");
            alertBean.setGroupId("OP_"+str_type +"_"+ Pub.moduleMap.get(module));
            alertBean.setOccur_time(Pub.transform_DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
            alertBean.setAlertType(alertType);
            alertBean.setEventType("OP_"+str_type +"_"+ Pub.moduleMap.get(module)+"-1-"+alertType+"-01");
            alertBean.setEventTitle(alertTitle);
            alertBean.setLevel(fields.containsKey("event_status") ? fields.get("event_status").toString() : "1");
            alertBean.setErrorMessage(fields.containsKey("event_info") ? fields.get("event_info").toString() : alertTitle);
            alertBean.setCause("-");

            alertBean.setModule(module);
            alertBean.setDataName(map.get("type").toString());
            alertBean.setSubName(map.get("name").toString());
            alertBean.setData_time(fields.get("data_time").toString());
            alertBean.setIpAddr(fields.containsKey("ip_addr") ? fields.get("ip_addr").toString() : "-");
            if(!"DATASOURCE".equals(str_type)){
                alertBean.setShould_time(map.containsKey("should_time") ?
                        Pub.transform_DateToString(
                                Pub.transform_StringToDate(map.get("should_time").toString(), "yyyy-MM-dd HH:mm:ss.SSSZ"),
                                "yyyy-MM-dd HH:mm:ss")
                        : "0");
                alertBean.setLast_time(map.containsKey("last_time") ?
                        Pub.transform_DateToString(
                                Pub.transform_StringToDate(map.get("last_time").toString(), "yyyy-MM-dd HH:mm:ss.SSSZ"),
                                "yyyy-MM-dd HH:mm:ss")
                        : "0");
            }else{
                alertBean.setShould_time(map.containsKey("should_time")? map.get("should_time").toString():"0");
                alertBean.setLast_time(map.containsKey("last_time")? map.get("last_time").toString():"0");
            }

            alertBean.setReceive_time("0");
            alertBean.setPath(fields.containsKey("path") ? fields.get("path").toString() : "-");
            alertBean.setFileName(fields.containsKey("file_name") ? fields.get("file_name").toString() : "-");

            if("01".equals(alertType)){
                alertBean.setDesc("超时未到达");
            }else if("02".equals(alertType)){
                alertBean.setDesc("数据异常");
            }else if("03".equals(alertType)){
                String  temp = alertTitle.substring(alertTitle.indexOf(",延迟")+1,alertTitle.length());
                alertBean.setDesc(temp);
            }else if("04".equals(alertType)){
                alertBean.setDesc(alertBean.getErrorMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return alertBean;
    }


}
