package com.cn.hitec.service.task;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.AlertBeanNew;
import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsWriteBean;
import com.cn.hitec.domain.DataInfo;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.repository.jpa.DataInfoRepository;
import com.cn.hitec.service.ConfigService;
import com.cn.hitec.util.HttpPub;
import com.cn.hitec.util.Pub;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({ "fu" })
public class Task_create_DIdata_Test {

    @Autowired
    ConfigService configService;
    @Autowired
    DataInfoRepository dataInfoRepository;
    @Autowired
    EsWriteService esWriteService;
    @Autowired
    EsQueryService esQueryService;
    @Autowired
    HttpPub httpPub;

    @Test
    public void test1() throws Exception{
        List<DataInfo> listDataInfo = dataInfoRepository.findAllChilden(3);

        //循环所有数据,区分是采集、加工、分发 的数据，分别存入不同的map
        for (DataInfo di : listDataInfo){
            Map<String ,Object> map = new HashMap<>();
            map.put("DI_name",di.getName());
            map.put("time_interval",di.getMonitor_times());
            map.put("should_time",di.getShould_time());
            map.put("last_time",di.getTimeout_threshold());
            map.put("IP",di.getIp());
            map.put("path",di.getFile_path());
            map.put("module",di.getModule());
            map.put("serviceType",di.getService_type());
            map.put("startMonitor",di.getStart_moniter());

            if ("FZJC".equals(di.getService_type()) && ("T639".equals(di.getName()) || "风流场".equals(di.getName()))) {
                Pub.DIMap_t639.put(di.getName() + "," + di.getIp() + "," + di.getService_type() + "," + di.getModule(), map);
            } else if ("采集".equals(di.getModule())) {
                Pub.DIMap_collect.put(di.getName() + "," + di.getIp() + "," + di.getService_type() + "," + di.getModule(), map);
            } else if ("加工".equals(di.getModule())) {
                Pub.DIMap_machining.put(di.getName() + "," + di.getIp() + "," + di.getService_type() + "," + di.getModule(), map);
            } else if ("分发".equals(di.getModule())) {
                Pub.DIMap_distribute.put(di.getName() + "," + di.getIp() + "," + di.getService_type() + "," + di.getModule(), map);
            }

        }

        System.out.println("alertMap_collect.size:"+ Pub.DIMap_collect.size()+",--:"+ JSON.toJSONString(Pub.DIMap_collect));
        System.out.println("alertMap_machining.size:"+ Pub.DIMap_machining.size()+",--:"+ JSON.toJSONString(Pub.DIMap_machining));
        System.out.println("alertMap_distribute.size:"+ Pub.DIMap_distribute.size()+",--:"+ JSON.toJSONString(Pub.DIMap_distribute));
        System.out.println("DIMap_t639.size:"+ Pub.DIMap_t639.size()+",--:"+ JSON.toJSONString(Pub.DIMap_t639));

        configService.createAlertDI("采集", Pub.DIMap_collect,0,new Date());
        configService.createAlertDI("加工", Pub.DIMap_machining,0,new Date());
        configService.createAlertDI("分发", Pub.DIMap_distribute,0,new Date());

        configService.createT639DI("FZJC",Pub.DIMap_t639,5);

    }

    @Test
    public void sendWechart(){

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
        mustMap.put("isSend","true");
        params.put("must",mustMap);

        esQueryBean.setParameters(params);
        //查询数据
        Map<String,Object> weChartMap = esQueryService.getData_new(esQueryBean);
        if(weChartMap != null){
            if ( "success".equals(weChartMap.get("result"))){
                List dataList = (List) weChartMap.get("resultData");

                for (Object object : dataList){
                    Map<String,Object> map = (Map<String, Object>) object;

                    //发送消息
                    Map<String, Object> resultMap =  httpPub.httpPost(map.get("sendUser").toString(), map.get("alertTitle").toString());
                    if(resultMap == null || !"ok".equals(resultMap.get("errmsg"))){
                        System.out.println(JSON.toJSONString(resultMap));
                        continue;
                    }
                    String str_id = map.get("_id").toString();
                    Map<String, Object> pam = new HashMap<>();
                    EsWriteBean esWriteBean = new EsWriteBean();
                    esWriteBean.setIndex(map.get("_index").toString());
                    esWriteBean.setType(str_type);
                    esWriteBean.setId(str_id);
                    pam.put("send_time", System.currentTimeMillis());
                    pam.put("isSend", "true");
                    esWriteBean.setParams(pam);
//                    esWriteService.update_field(esWriteBean);
                }
            }else {
                System.out.println("error:"+weChartMap.get("message"));
            }
        }else {
            System.out.println("error:未知错误");
        }

    }

    @Test
    public void test3(){
        boolean isError = false;
        do {
            System.out.println("---------------------------------开始执行定时任务，生成第二天的数据--------------------------------");
            try {

                System.out.println("alertMap_collect.size:"+ Pub.DIMap_collect.size()+",--:"+ JSON.toJSONString(Pub.DIMap_collect));
                System.out.println("alertMap_machining.size:"+ Pub.DIMap_machining.size()+",--:"+ JSON.toJSONString(Pub.DIMap_machining));
                System.out.println("alertMap_distribute.size:"+ Pub.DIMap_distribute.size()+",--:"+ JSON.toJSONString(Pub.DIMap_distribute));
                System.out.println("DIMap_t639.size:"+ Pub.DIMap_t639.size()+",--:"+ JSON.toJSONString(Pub.DIMap_t639));

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
//                cal.set(Calendar.HOUR_OF_DAY, 5);
//                cal.set(Calendar.MINUTE, 0);
//                cal.set(Calendar.SECOND, 0);

                configService.createAlertDI("采集", Pub.DIMap_collect,0,cal.getTime());
                configService.createAlertDI("加工", Pub.DIMap_machining,0,cal.getTime());
                configService.createAlertDI("分发", Pub.DIMap_distribute,0,cal.getTime());

                configService.createT639DI("FZJC",Pub.DIMap_t639,5);
                configService.makeProjectTable(new Date(),0,Pub.DIMap_DS,cal.getTime());
            } catch (Exception e) {
                isError = true;
                e.printStackTrace();
                try {
                    //如果报错，过一分钟后再执行一次
                    Thread.sleep(6000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }while (isError);
    }

    @Test
    public void sendMessage(){
        //发送消息
        Map<String, Object> resultMap =  httpPub.httpPost("QQ670779441", "测试数据");
        if(resultMap == null || !"ok".equals(resultMap.get("errmsg"))){
            System.out.println(JSON.toJSONString(resultMap));
        }else{
            System.out.println(JSON.toJSONString(resultMap));
        }
    }

}