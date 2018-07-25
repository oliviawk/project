package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.tools.Pub;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
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
import org.springframework.util.StringUtils;

import java.util.*;

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

    public List<Map> findDataByQuery(String[] indices, String[] types, Map<String, Object> params) throws Exception {
        System.out.println(JSON.toJSONString(params));
        List<Map> resultList = new ArrayList<>();
        int fromInt = 0;
        int sizeInt = 30;
        String strSort = "";
        String strSortType = "desc";
        boolean isRange = false;
        boolean is_ID = false;
        boolean is_Type = false;
        boolean is_Index = false;
        boolean resultAll = false;
        //值查询
        List<QueryBuilder> rangeBuilderList = null;
        List<QueryBuilder> rangeBuilderListNot = null;
        //创建查询类
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        if(params != null){
            if(params.containsKey("_id") && "true".equals(params.get("_id"))){
                is_ID = true;
                params.remove("_id");
            }
            if(params.containsKey("_type") && "true".equals(params.get("_type"))){
                is_Type = true;
                params.remove("_type");
            }
            if(params.containsKey("_index") && "true".equals(params.get("_index"))){
                is_Index = true;
                params.remove("_index");
            }
            if(params.containsKey("from") && !StringUtils.isEmpty(params.get("from"))){
                fromInt = Integer.valueOf(params.get("from").toString());
                params.remove("from");
            }
            if(params.containsKey("size") && !StringUtils.isEmpty(params.get("size"))){
                sizeInt = Integer.valueOf(params.get("size").toString());
                params.remove("size");
            }
            if(params.containsKey("sort") && !StringUtils.isEmpty(params.get("sort"))){
                strSort = params.get("sort").toString();
                params.remove("sort");
            }
            if(params.containsKey("sortType") && !StringUtils.isEmpty(params.get("sortType"))){
                strSortType = params.get("sortType").toString();
                params.remove("sortType");
            }
            if(params.containsKey("resultAll") && !StringUtils.isEmpty(params.get("resultAll"))){
                resultAll = (Boolean) params.get("resultAll");
                params.remove("resultAll");
            }
            if(params.containsKey("range")){
                isRange = true;
                rangeBuilderList = new ArrayList<>();
                List<Map> rangeList = (List<Map>)params.get("range");
                for(Map<String,Object>  map : rangeList){
                    String name = map.get("name").toString();
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(name);
                    for (String key:map.keySet()){
                        if(key.equals("name")){
                            continue;
                        }
                        rangeQueryBuilder = Pub.RangeChoiceTest(key,rangeQueryBuilder,map.get(key));
                    }
                    rangeBuilderList.add(rangeQueryBuilder);

                }
            }

            if(params.containsKey("rangeNot")){
                isRange = true;
                rangeBuilderListNot = new ArrayList<>();
                List<Map> rangeList = (List<Map>)params.get("range");
                for(Map<String,Object>  map : rangeList){
                    String name = map.get("name").toString();
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(name);
                    for (String key:map.keySet()){
                        if(key.equals("name")){
                            continue;
                        }
                        rangeQueryBuilder = Pub.RangeChoiceTest(key,rangeQueryBuilder,map.get(key));
                    }
                    rangeBuilderListNot.add(rangeQueryBuilder);

                }
            }

            if(params.containsKey("wildcard")){
                Map<String,Object> wildcardMap = (Map<String,Object>)params.get("wildcard");
                for (String strKey : wildcardMap.keySet()){
                    queryBuilder.must(QueryBuilders.wildcardQuery(strKey,wildcardMap.get(strKey).toString()));
                }
            }
            if(params.containsKey("wildcardNot")){
                Map<String,Object> wildcardNotMap = (Map<String,Object>)params.get("wildcardNot");
                for (String strKey : wildcardNotMap.keySet()){
                    queryBuilder.mustNot(QueryBuilders.wildcardQuery(strKey,wildcardNotMap.get(strKey).toString()));
                }
            }

            if(params.containsKey("must")){
                Map<String,Object> mustMap = (Map<String,Object>)params.get("must");
                for (String strKey : mustMap.keySet()){
                    if(mustMap.get(strKey) instanceof String){
                        queryBuilder.must(QueryBuilders.termQuery(strKey,mustMap.get(strKey)));
                    }else if(mustMap.get(strKey) instanceof List){
                        List<Object> list =(List<Object>)mustMap.get(strKey);
                        queryBuilder.must(QueryBuilders.termsQuery(strKey,list));
                    }

                }
            }
            if(params.containsKey("mustNot")){
                Map<String,Object> mustNotMap = (Map<String,Object>)params.get("mustNot");
                for (String strKey : mustNotMap.keySet()){
                    if(mustNotMap.get(strKey) instanceof String){
                        queryBuilder.mustNot(QueryBuilders.termQuery(strKey,mustNotMap.get(strKey)));
                    }else if(mustNotMap.get(strKey) instanceof List){
                        List<Object> list =(List<Object>)mustNotMap.get(strKey);
                        queryBuilder.mustNot(QueryBuilders.termsQuery(strKey,list));
                    }
                }
            }

            if(isRange){
                if (rangeBuilderList != null){
                    for (QueryBuilder qb:rangeBuilderList){
                        queryBuilder.must(qb);
                    }
                }
                if (rangeBuilderListNot != null){
                    for (QueryBuilder qb:rangeBuilderListNot){
                        queryBuilder.mustNot(qb);
                    }
                }
            }
        }

//        log.info(queryBuilder.toString());
        //验证去除没有的index
        List<String> listIndice = new ArrayList<>();
        if(indices != null){
            for (String str : indices){
                if(es.exists(str)){
                    listIndice.add(str);
                }
            }
            if(listIndice.size() > 0){
                indices = listIndice.toArray(new String [listIndice.size()]);
            }else{
                throw new Exception("index not be null");
            }
        }else {
            throw new Exception("index not be null");
        }
        log.info("indices:"+JSON.toJSONString(indices));
        SearchRequestBuilder requestBuilder = es.client.prepareSearch(indices)
                .setTypes(types)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder);

        if(!StringUtils.isEmpty(strSort)){
//            SortBuilder sortBuilder = SortBuilders.fieldSort("last_time");
            requestBuilder.addSort(strSort, strSortType.equals("desc")? SortOrder.DESC : SortOrder.ASC);
        }

        long start = System.currentTimeMillis();
        if(resultAll){
            requestBuilder.setSize(1000);
            //创建查询
            SearchResponse response = requestBuilder.setScroll(new TimeValue(2000)).get();
            do {
                for (SearchHit hits : response.getHits().getHits()) {
                    try {
                        if(is_ID){
                            hits.getSource().put("_id",hits.getId());
                        }
                        if(is_Type){
                            hits.getSource().put("_type",hits.getType());
                        }
                        if(is_Index){
                            hits.getSource().put("_index",hits.getIndex());
                        }
                        resultList.add(hits.getSource());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                response = es.client.prepareSearchScroll(response.getScrollId())
                        .setScroll(new TimeValue(2000))
                        .execute().actionGet();
            } while(response.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.

        }else{
            if(fromInt > 0){
                requestBuilder.setFrom(fromInt);
            }
            if(sizeInt > 0){
                requestBuilder.setSize(sizeInt);
            }
//            log.info(JSON.toJSONString(requestBuilder));

            //创建查询
            SearchResponse response = requestBuilder
                    .setExplain(false).get();

            SearchHit[] searchHits = response.getHits().getHits();
            for (SearchHit hits:searchHits) {
                try {
                    if(is_ID){
                        hits.getSource().put("_id",hits.getId());
                    }
                    if(is_Type){
                        hits.getSource().put("_type",hits.getType());
                    }
                    if(is_Index){
                        hits.getSource().put("_index",hits.getIndex());
                    }
                    resultList.add(hits.getSource());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("resultList.size = " + resultList.size());
        log.info("查询ES耗时："+(System.currentTimeMillis() - start));
        return resultList;
    }

}
