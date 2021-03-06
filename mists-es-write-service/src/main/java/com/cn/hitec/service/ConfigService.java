package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cn.hitec.domain.DataInfo;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.repository.jpa.DataInfoRepository;
import com.cn.hitec.tools.Pub;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.byscroll.BulkByScrollResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.omg.CORBA.DATA_CONVERSION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

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


    public void initAlertMould(){
        List<Object> list = dataInfoRepository.findAlertModule();
        for (Object obj : list){
            JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(obj));
            if (jsonArray.size() == 2){
                Pub.alertModuleMap.put(jsonArray.get(0).toString(),jsonArray.get(1).toString());
            }
        }

    }

    public long  deletepreparedata(List<DataInfo> list){
        long num = 0;
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String[] indexs = Pub.getIndices(new Date(),1);
        for (DataInfo dataInfo : list){
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(QueryBuilders.matchPhraseQuery("fields.module", dataInfo.getModule()));
            queryBuilder.must(QueryBuilders.matchPhraseQuery("type", dataInfo.getName()));
            queryBuilder.must(QueryBuilders.matchPhraseQuery("fields.ip_addr", dataInfo.getIp()));
            queryBuilder.must(QueryBuilders.matchPhraseQuery("aging_status", "未处理"));
            BulkByScrollResponse response =
                    DeleteByQueryAction.INSTANCE.newRequestBuilder(esRepository.client)
                            .filter(queryBuilder)
                            .source(indexs).get();

             num= response.getDeleted()+num;

        }

        return num;
    }
}
