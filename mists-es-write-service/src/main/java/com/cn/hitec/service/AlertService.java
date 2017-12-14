package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.AlertBean;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.tools.HttpPub;
import com.cn.hitec.tools.Pub;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;

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

    /**
     * 获取告警id
     * @param index
     * @param type
     * @param alertBean
     * @return
     */
    public String getDocumentId(String index , String type , AlertBean alertBean){
        String documentId = null;
        try {

            //创建查询类
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(QueryBuilders.termQuery("type",alertBean.getType()));
            queryBuilder.must(QueryBuilders.termQuery("module",alertBean.getModule()));
            queryBuilder.must(QueryBuilders.termQuery("ip",alertBean.getIp()));
            queryBuilder.must(QueryBuilders.termQuery("data_name",alertBean.getData_name()));
            queryBuilder.must(QueryBuilders.termQuery("data_time",alertBean.getData_time()));
            //返回查询结果
            SearchResponse response = es.client.prepareSearch(index)
                    .setTypes(type)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(queryBuilder)
                    .setExplain(true).get();

            SearchHit[] searchHits = response.getHits().getHits();
            log.info("alertData.dataLength :"+response.getHits().getTotalHits());
            for (SearchHit hits:searchHits) {
                documentId = hits.getId();
                break;
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
    public void alert(String index , String type , AlertBean alertBean){

        try {
            boolean isAlert_parent = false;
            //判断上游是否告警
            if("分发".equals(alertBean.getModule())){
                AlertBean alertBean_JG = alertBean;
                AlertBean alertBean_CJ = alertBean;
                alertBean_JG.setModule("加工");
                alertBean_CJ.setModule("采集");
                String documentId_JG = getDocumentId(index,type,alertBean_JG);
                if(StringUtils.isEmpty(documentId_JG)){
                    String documentId_CJ = getDocumentId(index,type,alertBean_CJ);
                    if(!StringUtils.isEmpty(documentId_CJ)){
                        isAlert_parent = true;
                    }
                }else{
                    isAlert_parent = true;
                }
            }else if("加工".equals(alertBean.getModule())){
                AlertBean alertBean_CJ = alertBean;
                alertBean_CJ.setModule("采集");
                String documentId_CJ = getDocumentId(index,type,alertBean_CJ);
                if(!StringUtils.isEmpty(documentId_CJ)){
                    isAlert_parent = true;
                }
            }

            //如果上游告警了，那么此条告警不生成
            if(!isAlert_parent){
                //判断是否重复告警
                String documentId = getDocumentId(index,type,alertBean);

                if(documentId != null){ //如果有ID ，说明是
                    //不是超时和异常的告警，只能是恢复告警，那么修改掉
                    if(!"超时".equals(alertBean.getAlertType()) && !"异常".equals(alertBean.getAlertType()) ){
                        //保存告警信息
                        es.bulkProcessor.add(new IndexRequest(index,type,documentId)
                                .source(JSON.toJSONString(alertBean), XContentType.JSON));
                    }

                }else{
                    //保存告警信息
                    IndexResponse response = es.client.prepareIndex(index,type)
                            .setSource(JSON.toJSONString(alertBean),XContentType.JSON).get();

                    documentId = response.getId();
                }

                if(StringUtils.isEmpty(documentId)){
                    throw new Exception("documentId 为空");
                }

                alertBean.setDocumentId(documentId);
                HttpPub.httpPost("@all",alertBean.getTitle());
                //发送消息并推送
                kafkaProducer.sendMessage("ALERT",null,JSON.toJSONString(alertBean));

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

    }

    /**
     * 生成告警类
     * @param type
     * @param alertTitle
     * @param fields
     * @return
     */
    public AlertBean getAlertBean(String type , String alertTitle,String data_name , Map<String,Object> fields){
        AlertBean alertBean = null;

        try {
            alertBean = new AlertBean();
            alertBean.setType("OP_FZJC_TIMER");
            alertBean.setAlertType(type);
            alertBean.setLevel(fields.containsKey("event_status") ? fields.get("event_status").toString():"");
            alertBean.setTitle(alertTitle);
            alertBean.setTime(Pub.transform_DateToString(new Date(),"yyyy-MM-dd HH:mm:ss"));
            alertBean.setIp(fields.containsKey("ip_addr")?fields.get("ip_addr").toString():"");
            alertBean.setDesc(fields.containsKey("event_info") ? fields.get("event_info").toString():"");
            alertBean.setCause("");
            alertBean.setData_name(data_name);
            alertBean.setData_time(fields.get("data_time").toString());
            alertBean.setModule(fields.get("module").toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return alertBean;
    }
}
