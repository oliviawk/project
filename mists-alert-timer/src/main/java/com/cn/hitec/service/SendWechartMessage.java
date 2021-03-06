package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsWriteBean;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.util.HttpPub;
import com.cn.hitec.util.Pub;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description  发送微信消息
 * @author fukeli
 * @data 2018.03.22 17:00
 */
@Service
public class SendWechartMessage {
    private static final Logger logger = LoggerFactory.getLogger(SendWechartMessage.class);
    @Autowired
    EsWriteService esWriteService;
    @Autowired
    EsQueryService esQueryService;
    @Autowired
    HttpPub httpPub;

    @Value("${send.WECHART}")
    private boolean sendWechart;
    @Value("${send.SMS}")
    private boolean sendSMS;

    public void sendWechart() throws  Exception{

        EsQueryBean esQueryBean = new EsQueryBean();
        String[] str_indexs = Pub.getIndices(new Date(),1);
        String str_type = "sendWeichart";
        esQueryBean.setIndices(str_indexs);
        esQueryBean.setTypes(new String[]{str_type});

        Map<String,Object> mustMap = new HashMap<>();
        Map<String,Object> params = new HashMap<>();
        params.put("_index","true");
        params.put("_id","true");
        params.put("sort","create_time");
        params.put("sortType","asc");
        params.put("size",100);
        mustMap.put("isSend","false");
        params.put("must",mustMap);

        esQueryBean.setParameters(params);
        //查询数据
        Map<String,Object> weChartMap = esQueryService.getData_new(esQueryBean);
        int num = 0 , sendNum = 0;
        if(weChartMap != null && "success".equals(weChartMap.get("result"))){
            List dataList = (List) weChartMap.get("resultData");
            num = dataList.size();
            for (Object object : dataList){
                try {
                    Map<String,Object> map = (Map<String, Object>) object;

                    //发送消息
                    if (sendWechart){
                        String alertTitle = map.get("alertTitle").toString();
                        if (alertTitle.indexOf(".tmp") > -0){
                            //去掉.tmp 后缀名
                            alertTitle.replace(".tmp","");
                        }

                        Map<String, Object> resultMap =  httpPub.httpPost(
                                StringUtils.isEmpty(map.get("sendUser").toString())? "@all":map.get("sendUser").toString(),
                                map.get("alertTitle").toString());
                        if(resultMap == null || !"ok".equals(resultMap.get("errmsg"))){
                            logger.error(JSON.toJSONString(resultMap));
                            continue;
                        }
                    }

                    String str_index = map.get("_index").toString();
                    String str_id = map.get("_id").toString();
                    Map<String, Object> pam = new HashMap<>();
                    EsWriteBean esWriteBean = new EsWriteBean();
                    esWriteBean.setIndex(str_index);
                    esWriteBean.setType(str_type);
                    esWriteBean.setId(str_id);
                    pam.put("send_time", System.currentTimeMillis());
                    pam.put("isSend", "true");
                    esWriteBean.setParams(pam);
                    Map<String,Object> updResultMap = esWriteService.update_field(esWriteBean);
                    if(updResultMap == null || !"success".equals(updResultMap.get("result"))){
                        logger.error(JSON.toJSONString(updResultMap));
                        continue;
                    }
                    sendNum ++;
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }else {
            logger.error(JSON.toJSONString(weChartMap));
        }

        logger.info("预发送微信数量："+num +", 实际发送数量："+sendNum+", 是否发送微信："+sendWechart);

    }



    public void sendSMS(){
//   短信那一套
        EsQueryBean esQueryBean1 = new EsQueryBean();
        String[] str_indexs1 = Pub.getIndices(new Date(),1);
        String str_type1 = "sendSMS";
        esQueryBean1.setIndices(str_indexs1);
        esQueryBean1.setTypes(new String[]{str_type1});

        Map<String,Object> mustMap1 = new HashMap<>();
        Map<String,Object> params1 = new HashMap<>();
        params1.put("_index","true");
        params1.put("_id","true");
        params1.put("sort","create_time");
        params1.put("sortType","asc");
        params1.put("size",100);
        mustMap1.put("isSend","false");
        params1.put("must",mustMap1);

        esQueryBean1.setParameters(params1);
        //查询数据
        Map<String,Object> SMStMap = esQueryService.getData_new(esQueryBean1);
        int num1 = 0 , sendNum1 = 0;
        if(SMStMap != null && "success".equals(SMStMap.get("result"))){
            List dataList = (List) SMStMap.get("resultData");
            num1 = dataList.size();
            String sendMessage = "业务告警:\n";
            for (int i = 0 ; i < dataList.size() ; i++){
                try {
                    Map<String,Object> map = (Map<String, Object>) dataList.get(i);
                    if (sendSMS){
                        String sendUser = map.get("sendUser").toString();
                        String sendTitle = map.get("alertTitle").toString();
                        if (StringUtils.isEmpty(sendUser)){
                            logger.error("发送短信的手机号码为空！");
                            break;
                        }
                        //测试用户
                        sendUser = "13810933845|13810168659|13717602125|18600023553|15201211053";
                        //超过字节数，分批次发送
                        if (sendMessage.length()+sendTitle.length() > 500){

                            Map<String, Object> resultMap =  httpPub.httpSMSPost(sendUser, sendMessage);
                            if(resultMap == null || !"Success".equals(resultMap.get("returnStatus"))){
                                logger.error(JSON.toJSONString(resultMap));
                                continue;
                            }
                            logger.info("阶段发送短信消息：{}",sendMessage);
                            sendMessage = sendTitle+"\n";
                        }else {
                            sendMessage += ("\t"+sendTitle+"\n");

                        }
                        //最后一次循环，发送所有拼接的短信
                        if (i == dataList.size() -1 ){

                            Map<String, Object> resultMap =  httpPub.httpSMSPost(sendUser, sendMessage);
                            if(resultMap == null || !"Success".equals(resultMap.get("returnStatus"))){
                                logger.error(JSON.toJSONString(resultMap));
                                continue;
                            }
                            logger.info("发送短信消息：{}",sendMessage);
                        }
                    }

                    String str_index = map.get("_index").toString();
                    String str_id = map.get("_id").toString();
                    Map<String, Object> pam = new HashMap<>();
                    EsWriteBean esWriteBean = new EsWriteBean();
                    esWriteBean.setIndex(str_index);
                    esWriteBean.setType(str_type1);
                    esWriteBean.setId(str_id);
                    pam.put("send_time", System.currentTimeMillis());
                    pam.put("isSend", "true");
                    esWriteBean.setParams(pam);
                    Map<String,Object> updResultMap = esWriteService.update_field(esWriteBean);
                    if(updResultMap == null || !"success".equals(updResultMap.get("result"))){
                        logger.error(JSON.toJSONString(updResultMap));
                        continue;
                    }
                    sendNum1 ++;
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }else {
            logger.error(JSON.toJSONString(SMStMap));
        }

        logger.info("预发送短信数量："+num1 +", 实际发送数量："+sendNum1+", 是否发送微信："+sendSMS);
    }
}
