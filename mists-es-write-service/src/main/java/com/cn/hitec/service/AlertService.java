package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.AlertBean;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.tools.HttpPub;
import com.cn.hitec.tools.Pub;
import org.codehaus.groovy.util.StringUtil;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Service
public class AlertService {
    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

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
            queryBuilder.must(QueryBuilders.termQuery("type.keyword",alertBean.getType()));
            queryBuilder.must(QueryBuilders.termQuery("module.keyword",alertBean.getModule()));
            queryBuilder.must(QueryBuilders.termQuery("ip.keyword",alertBean.getIp()));
            queryBuilder.must(QueryBuilders.termQuery("data_name.keyword",alertBean.getData_name()));
            queryBuilder.must(QueryBuilders.termQuery("data_time.keyword",alertBean.getData_time()));
            //返回查询结果
            SearchResponse response = es.client.prepareSearch(index)
                    .setTypes(type)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(queryBuilder)
                    .setExplain(true).get();

            SearchHit[] searchHits = response.getHits().getHits();
            logger.info("alertData.dataLength :"+response.getHits().getTotalHits());
            for (SearchHit hits:searchHits) {
                documentId = hits.getId();
                break;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            return documentId;
        }
    }


    public void alert(String index , String type , AlertBean alertBean) throws Exception{

        String documentId = getDocumentId(index,type,alertBean);
        if(documentId != null){
            //保存告警信息
            es.bulkProcessor.add(new IndexRequest(index,type,documentId)
                    .source(JSON.toJSONString(alertBean), XContentType.JSON));
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
        //发送消息并推送
        kafkaProducer.sendMessage("ALERT",null,JSON.toJSONString(alertBean));
        HttpPub.httpPost("@all",alertBean.getTitle());
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
