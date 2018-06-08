package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.domain.DataInfo;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.repository.jpa.DataInfoRepository;
import com.cn.hitec.tools.Pub;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 这里是描述信息
 * @author: fukl
 * @data: 2017年10月26日 14:29
 */
@Slf4j
@Service
public class ConfigService {
    @Autowired
    ESRepository esRepository;
    @Autowired
    DataInfoRepository dataInfoRepository;

    public void initAlertMap(){
        try {
            /*  ------------ > 3.8日修改的新代码 < ------------------*/
            List<DataInfo> listDataInfo = dataInfoRepository.findAll_isData();

            for (DataInfo di : listDataInfo){
                Map<String ,Object> map = new HashMap<>();
                map.put("DI_name",di.getName());
                map.put("sub_name",di.getSub_name());
//                map.put("time_interval",di.getMonitor_times());
//                map.put("should_time",di.getShould_time());
//                map.put("last_time",di.getTimeout_threshold());
                map.put("IP",di.getIp());
                map.put("path",di.getFile_path());
                map.put("module",di.getModule());
                map.put("serviceType",di.getService_type());

                Pub.alert_time_map.put(di.getService_type()+","+di.getName()+","+di.getModule()+","+di.getIp(), map);

            }

            List<Object> listStrategy = dataInfoRepository.findDataStrategyAll();
            //循环所有数据,区分是采集、加工、分发 的数据，分别存入不同的map
            for (Object di : listStrategy){
                Map<String ,Object> map = new HashMap<>();

                List list = JSON.parseArray(JSON.toJSONString(di),String.class);
                if(list.size() == 10 ){
                    map.put("serviceType",list.get(0));
                    map.put("DI_name",list.get(1));
                    map.put("module",list.get(2));
                    map.put("ip",list.get(3));
                    map.put("strategy_name",list.get(4));
                    map.put("wechart_send_enable",list.get(5));
                    map.put("wechart_content",list.get(6));
                    map.put("sms_send_enable",list.get(7));
                    map.put("sms_content",list.get(8));
                    map.put("send_users",list.get(9));

                    Pub.DI_ConfigMap.put(list.get(0)+","+list.get(1)+","+list.get(2)+","+list.get(3), map);
                }

            }
            log.info("DI_configMap.size = "+Pub.DI_ConfigMap.size());
        /*---------------------3.8 日，旧代码------------------------*/
//            List<Map> listMap_collect = getConfigAlert("config","collect",null);
//            for (Map map : listMap_collect){
//                String DI_name = map.get("DI_name").toString();
//                Pub.alertMap_collect.put(DI_name,map);
//            }
//
//            List<Map> listMap_machining = getConfigAlert("config","machining",null);
//            for (Map map : listMap_machining){
//                String DI_name = map.get("DI_name").toString();
//                Pub.alertMap_machining.put(DI_name,map);
//            }
//
//            List<Map> listMap_distribute = getConfigAlert("config","distribute",null);
//            for (Map map : listMap_distribute){
//                String DI_name = map.get("DI_name").toString();
//                Pub.alertMap_distribute.put(DI_name,map);
//            }
//            List<Map> list_alert_time_map = getConfigAlertDI("config");
//            for (Map map : list_alert_time_map){
//                 if(map.containsKey("DI_name")){
//                    String DI_name = map.get("DI_name").toString();
//                    Pub.alert_time_map.put(DI_name,1);
//                }
//
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询配置表信息
     * @param indice
     * @return
     * @throws Exception
     */
    public List<Map>  getConfigAlertDI(String indice) throws Exception{
        List<Map> resultList = new ArrayList<>();
        int sizeInt = 10;
        long timeValue = 5000;
        try {

            SearchResponse scrollResp = esRepository.client.prepareSearch(indice)
                  //  .setTypes(type)
                    .setScroll(new TimeValue(timeValue))
                    .setSize(sizeInt).get(); //max of 100 hits will be returned for each scroll
            //Scroll until no hits are returned
            do {
                for (SearchHit hit : scrollResp.getHits().getHits()) {
                    //Handle the hit...
                    try {
                        hit.getSource().put("id",hit.getId());
                        //System.out.println(hit.getId());
                        resultList.add(hit.getSource());
                       // System.out.println(hit.getSource().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                scrollResp = esRepository.client.prepareSearchScroll(scrollResp.getScrollId())
                        .setScroll(new TimeValue(timeValue))
                        .execute().actionGet();
            } while(scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.

        } catch (Exception e) {
            e.printStackTrace();
//            log.error(e.getMessage());
            resultList = null;
        } finally {
       //     System.out.println(resultList.toString());
            return resultList;
        }
    }




    /**
     * 查询配置表信息
     * @param indice
     * @param type
     * @param params
     * @return
     * @throws Exception
     */
    public List<Map> getConfigAlert(String indice, String type ,Map<String,Object> params) throws Exception{
        List<Map> resultList = new ArrayList<>();
        int sizeInt = 10;
        long timeValue = 5000;
        try {
            if (params != null){
                if(params.get("size") != null && (Integer)params.get("size") > 0){
                    sizeInt = (Integer)params.get("size");
                }
                if(params.get("timeValue") != null && (long)params.get("timeValue") > 0){
                    timeValue = (Integer)params.get("timeValue");
                }
            }
            SearchResponse scrollResp = esRepository.client.prepareSearch(indice)
                     .setTypes(type)
                    .setScroll(new TimeValue(timeValue))
                    .setSize(sizeInt).get(); //max of 100 hits will be returned for each scroll
            //Scroll until no hits are returned
            do {
                for (SearchHit hit : scrollResp.getHits().getHits()) {
                    //Handle the hit...
                    try {
                        hit.getSource().put("id",hit.getId());
                        resultList.add(hit.getSource());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                scrollResp = esRepository.client.prepareSearchScroll(scrollResp.getScrollId())
                        .setScroll(new TimeValue(timeValue))
                        .execute().actionGet();
            } while(scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.

        } catch (Exception e) {
            e.printStackTrace();
//            log.error(e.getMessage());
            resultList = null;
        } finally {
            System.out.println(resultList.toString());
            return resultList;
        }
    }




}
