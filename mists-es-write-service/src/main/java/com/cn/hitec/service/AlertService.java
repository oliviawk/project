package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cn.hitec.bean.AlertBeanNew;
import com.cn.hitec.domain.Users;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.repository.jpa.DataInfoRepository;
import com.cn.hitec.repository.jpa.UsersRepository;
import com.cn.hitec.tools.AlertType;
import com.cn.hitec.tools.Pub;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
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
    KafkaProducer kafkaProducer;
    @Autowired
    UsersRepository usersRepository;

    @Autowired
    DataInfoRepository dataInfoRepository;

    /**
     * 获取告警id
     * @param index
     * @param type
     * @param id
     * @return
     */
    public String getDocumentById(ESRepository es,String index , String type , String id){
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
    public void alert(ESRepository es,String index , String type , AlertBeanNew alertBean,JSONArray rulesArray){

        try {

            //判断是否重复告警
            String module_key = alertBean.getGroupId()+","+alertBean.getDataName()+","+alertBean.getIpAddr();
            String str_id = Pub.MD5(module_key+","+alertBean.getData_time());

            String documentId = getDocumentById(es,index,type,str_id);

            if(documentId != null){ //如果有ID
                //如果是超时、异常的告警，说明是重复，过滤掉。 如果是迟到的数据，修改告警信息（取消告警）
                if(AlertType.DELAY.getValue().equals(alertBean.getAlertType())){
                    //保存告警信息
                    es.bulkProcessor.add(new IndexRequest(index,type,documentId)
                            .source(JSON.toJSONString(alertBean), XContentType.JSON));

                    //是否迟到提示
                    if(rulesArray.size() > 0 && rulesArray.getInteger(5) == 0){
                        return;
                    }
                }
                else{
                    return;
                }

            }else{
                //保存告警信息
                IndexResponse response = es.client.prepareIndex(index,type,str_id)
                        .setSource(JSON.toJSONString(alertBean),XContentType.JSON).get();

                documentId = response.getId();

                if(StringUtils.isEmpty(documentId)){
                    throw new Exception("插入数据失败");
                }
                if(rulesArray.size() > 0 && AlertType.NOTE.getValue().equals(alertBean.getAlertType())){
                    dataInfoRepository.addAlertCnt(rulesArray.getLongValue(0));
                }
            }

            //消息推送
//                kafkaProducer.sendMessage("ALERT",null,JSON.toJSONString(alertBean));


            /*boolean isAlert_parent = false;
            //判断上游是否告警，如果告警，则该条告警不进行微信、短信告警
            String moduleKey = module_key;
            String moduleKeyParent = "";
            while (true){
                moduleKeyParent = Pub.alertModuleMap.containsKey(moduleKey) ? Pub.alertModuleMap.get(moduleKey):moduleKey;

                if (moduleKey.equals(moduleKeyParent)){
                    break;
                }
                String id_cj = getDocumentById(es,index,type,Pub.MD5(moduleKeyParent+","+alertBean.getData_time()));
                if (!StringUtils.isEmpty(id_cj)){
                    isAlert_parent = true;
                    log.info("-------> 存在上级告警");
                    log.info("过滤掉的告警信息："+JSON.toJSONString(alertBean));
                    break;
                }

                moduleKey = moduleKeyParent;
                moduleKeyParent = "";
            }*/

            /*====================modified by czt 2018.6.11=======================*/
            boolean isAlert = true;

            //如果不是提示类告警，要判断各种告警规则
            if(!AlertType.NOTE.getValue().equals(alertBean.getAlertType())){
                //先比较当前的告警时间范围和最大告警数
                isAlert = isAlert(rulesArray);
                //告警溯源
                if(isAlert){
                    List<Object> pres = dataInfoRepository.findPreModules(module_key);
                    for(Object pre : pres){
                        String id_cj = getDocumentById(es,index,type,Pub.MD5(pre+","+alertBean.getData_time()));
                        if (!org.apache.commons.lang.StringUtils.isEmpty(id_cj)){
                            isAlert = false;
                            logger.info("-------> 存在上级告警");
                            logger.info("过滤掉的告警信息："+JSON.toJSONString(alertBean));
                            break;
                        }
                    }
                }
            }


            if(isAlert){
                log.warn("生成微信、短信告警："+JSON.toJSONString(alertBean));

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
                String strKey = service_type+","+subName+","+module+","+ipAddr;
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

    boolean isAlert(JSONArray rulesArray){
        boolean isAlert = true;
        if(rulesArray.size() > 0){
            if(rulesArray.getInteger(2) != null ){
                if(rulesArray.getInteger(3) > rulesArray.getInteger(2)){
                    isAlert = false;
                }
            }

            if(isAlert && rulesArray.getString(1) != null && !"".equals(rulesArray.getString(1))){
                String[] range = rulesArray.getString(1).split("-");
                String[] times = range[0].split("-");
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                String now = df.format(new Date());
                if(times[0].compareTo(times[1]) >= 0){
                    if(now.compareTo(times[0]) >= 0 || now.compareTo(times[0]) <= 0){
                        isAlert = true;
                    }
                    else{
                        isAlert = false;
                    }
                }
                else{
                    if(now.compareTo(times[0]) >= 0 && now.compareTo(times[0]) <= 0){
                        isAlert = true;
                    }
                    else{
                        isAlert = false;
                    }
                }
            }
        }

        return isAlert;
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
            alertBean.setGroupId("OP_"+str_type +"_"+ module);
            alertBean.setOccur_time(Pub.transform_DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
            alertBean.setAlertType(alertType);
            alertBean.setEventType("OP_"+str_type +"_"+ module+"-1-"+alertType+"-01");
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

            if(AlertType.OVERTIME.getValue().equals(alertType)){
                alertBean.setDesc("超时未到达");
            }else if(AlertType.ABNORMAL.getValue().equals(alertType)){
                alertBean.setDesc("数据异常");
            }else if(AlertType.DELAY.getValue().equals(alertType)){
                String  temp = alertTitle.substring(alertTitle.indexOf(",延迟")+1,alertTitle.length());
                alertBean.setDesc(temp);
            }else /*if(AlertType.FILEEX.getValue().equals(alertType))*/{
                //文件错误或提前到达
                alertBean.setDesc(alertBean.getErrorMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return alertBean;
    }


}
