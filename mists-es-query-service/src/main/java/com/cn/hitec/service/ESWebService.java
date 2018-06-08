package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.tools.Pub;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName:
 * @Description:
 * @author: fukl
 * @data: 2017年08月3日 下午1:14
 */
@Slf4j
@Service
public class ESWebService {
    private static final Logger logger = LoggerFactory.getLogger(ESWebService.class);
    @Autowired
    private ESRepository es;


    /**
     * 聚合查询
     * @param indices
     * @param types
     * @param params
     * @return
     * @throws Exception
     */
    public  Map<String,Object> find_AggTerms(String[] indices, String[] types , Map<String,Object> params) throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        try {
            Calendar calendar = Calendar.getInstance();
            Date date = new Date();
            calendar.setTime(date);

            calendar.set(Calendar.MINUTE,-50);

            Date endDate = calendar.getTime();
//
//            logger.info("------> 查询时间间隔:" + (endDate.getTime() - date.getTime()));

//            Map<String,String> queryMap = (Map<String, String>) params.get("query");
//            Map<String,String> aggTermsMap = (Map<String, String>) params.get("agg_terms");
            //创建查询类
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            for (String strKey : params.keySet()){
                queryBuilder.must(QueryBuilders.termQuery(strKey,params.get(strKey)));
            }
            logger.info(Pub.transform_DateToString(endDate,"yyyy-MM-dd HH:mm:ss.sssZ"));
            queryBuilder.must(QueryBuilders.rangeQuery("fields.end_time").gte(Pub.transform_DateToString(endDate,"yyyy-MM-dd HH:mm:ss.sssZ")));


            //创建 聚合 条件
            TermsAggregationBuilder aggbuilder = AggregationBuilders.terms("subtype").field("type.keyword").size(50)
                    .subAggregation(
                            AggregationBuilders.terms("status").field("fields.event_status.keyword").size(5)
                    );


            //返回查询结果
            SearchResponse response = es.client.prepareSearch(indices)
                    .setTypes(types)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(queryBuilder).setSize(0)
                    .addAggregation(aggbuilder)
                    .setExplain(true).get();

            //结果转换
            Terms terms = response.getAggregations().get("subtype");
            for (Terms.Bucket bucket : terms.getBuckets()){

                Terms childTerms = bucket.getAggregations().get("status");
                Map<String,Object> tempMap = new HashMap<>();
                int count = 0;
                for (Terms.Bucket childBucket : childTerms.getBuckets()){
                    if(childBucket.getKey().equals("OK")){
                        tempMap.put(childBucket.getKey().toString(),childBucket.getDocCount());
                    }
                    count += childBucket.getDocCount();
                }
                tempMap.put("count",count);
                resultMap.put(bucket.getKey().toString(),tempMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            resultMap = new HashMap<>();
        } finally {
            return resultMap;
        }

    }


    /**
     * 页面流程图显示聚合方法
     * @return
     * @throws Exception
     */
    public  Map<String,Object> lct_AggTerms(String[] indices, String[] types , String[] subTypes) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        //创建查询类
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termsQuery("type", subTypes));
        queryBuilder.mustNot(QueryBuilders.termQuery("aging_status", "未处理"));
        //创建 聚合 条件
        TermsAggregationBuilder aggbuilder = AggregationBuilders.terms("groupByType").field("type").size(20)
                .subAggregation(
                        AggregationBuilders.terms("groupByModule").field("fields.module").size(10)
                                .subAggregation(
                                        AggregationBuilders.terms("groupByIp").field("fields.ip_addr").size(10)
                                                .subAggregation(
                                                        AggregationBuilders.topHits("groupByDataTime").sort("fields.data_time", SortOrder.DESC).size(1)
                                                )
                                )
                );



        //返回查询结果
        SearchResponse response = es.client.prepareSearch(indices)
                .setTypes(types)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder).setSize(0)
                .addAggregation(aggbuilder)
                .setExplain(true).get();

        //结果转换
        Terms groupByTypeResponse = response.getAggregations().get("groupByType");

        for (Terms.Bucket groupType : groupByTypeResponse.getBuckets()) {
//            log.info("groupType key:{} , groupType value:{}", groupType.getKey(), groupType.getDocCount());

            Terms groupByModuleResponse = groupType.getAggregations().get("groupByModule");

            for (Terms.Bucket groupByModule : groupByModuleResponse.getBuckets()) {
//                log.info("\tgroupByModule key:{} , groupByModule value:{}", groupByModule.getKey(), groupByModule.getDocCount());

                Terms groupByIpResponse = groupByModule.getAggregations().get("groupByIp");
                for (Terms.Bucket groupByIp : groupByIpResponse.getBuckets()) {

//                    log.info("\t\tgroupByIp key:{} , groupByIp value:{}", groupByIp.getKey(), groupByIp.getDocCount());

                    TopHits groupByDataTime = groupByIp.getAggregations().get("groupByDataTime");

                    for (SearchHit hit : groupByDataTime.getHits().getHits()) {
                        String key = groupType.getKey() + "_" + groupByModule.getKey() + "_" + groupByIp.getKey();
                        resultMap.put(key, hit.getSource());
//                        log.info("\t\t\t" + JSON.toJSONString(hit.getSource()));
                    }
                }
            }

        }
        return resultMap;
    }
}
