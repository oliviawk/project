package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.repository.ESRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequestBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName:
 * @Description:
 * @author: fukl
 * @data: 2017年08月3日 下午1:14
 */
@Slf4j
@Service
public class ESService {
    @Autowired
    private ESRepository es;


    /**
     * 普通查询
     * @param indices
     * @param types
     * @param moduleName
     * @param params
     * @return
     * @throws Exception
     */
    public List<Map> find_2(String[] indices, String[] types ,String moduleName,Map<String,Object> params) throws Exception{
        List<Map> resultList = new ArrayList<>();
        int fromInt = 0;
        int sizeInt = 30;
        String strSort = "receive_time";
        if(params != null){
            if(params.get("from") != null && Integer.valueOf(params.get("from").toString()) >= 0){
                fromInt = Integer.valueOf(params.get("from").toString());
                params.remove("from");
            }
            if(params.get("size") != null && Integer.valueOf(params.get("size").toString()) > 0){
                sizeInt = Integer.valueOf(params.get("size").toString());
                params.remove("size");
            }
            if(params.get("sort") != null ){
                strSort = params.get("sort").toString();
                params.remove("sort");
            }

        }

        //创建查询类
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (String strKey : params.keySet()){
            queryBuilder.must(QueryBuilders.termQuery(strKey,params.get(strKey)));
        }

//        log.info(queryBuilder.toString());
        SearchRequestBuilder requestBuilder = es.client.prepareSearch(indices)
                .setTypes(types)
                .setQuery(queryBuilder)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        requestBuilder.addSort(strSort,SortOrder.DESC);
        requestBuilder.setFrom(fromInt).setSize(sizeInt);
        //创建查询
        SearchResponse response = requestBuilder
                .setExplain(false).get();

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        SearchHit[] searchHits = response.getHits().getHits();
        for (SearchHit hits:searchHits) {
            try {
//                sourceMap = hits.getSource();
                resultList.add(hits.getSource());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultList;
    }











    /**
     * 模板查询测试
     * @param indices
     * @param types
     * @param templateName
     * @param params
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> find(String[] indices, String[] types ,String templateName, Map<String,Object> params) throws Exception{
        List<Map<String,Object>> resultList = new ArrayList<>();

        // 创建查询请求 ， 指定 index 和 type
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indices).types(types)
                .source(new SearchSourceBuilder()
                        .sort("receive_time", SortOrder.DESC)
                        .from(0).size(50))
                .source();


        SearchTemplateRequestBuilder searchTemplateRequestBuilder= new SearchTemplateRequestBuilder(es.client)
                .setRequest(searchRequest)
                .setScript(templateName)
                .setScriptType(ScriptType.STORED)
                .setScriptParams(params);

        SearchResponse response  = searchTemplateRequestBuilder.get().getResponse();
        SearchHit[] searchHits = response.getHits().getHits();
        Map<String,Object> sourceMap = null;        //接收es参数
        for (SearchHit hits:searchHits) {
            try {
                sourceMap = hits.getSource();
                resultList.add(sourceMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    public List<Map> temp(SearchTemplateRequestBuilder builder) throws Exception{
        List<Map> resultList = new ArrayList<>();


        SearchResponse response  = builder.get().getResponse();
        SearchHit[] searchHits = response.getHits().getHits();
        for (SearchHit hits:searchHits) {
            resultList.add(hits.getSource());
        }
        return resultList;
    }


    /**
     * 多个条件查询
     * @param listQueryBean
     * @return
     */
    public Map<String,Object> MutilQuery(List<EsQueryBean> listQueryBean){

        MultiSearchRequestBuilder multiSearchRequestBuilder = es.client.prepareMultiSearch();
        for (EsQueryBean esQueryBean : listQueryBean){
            Map<String,Object> params = esQueryBean.getParameters();
            //创建查询类
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            for (String strKey : params.keySet()){
                queryBuilder.must(QueryBuilders.termQuery(strKey,params.get(strKey)));
            }

            SearchRequestBuilder requestBuilder = es.client.prepareSearch(esQueryBean.getIndices())
                    .setTypes(esQueryBean.getTypes())
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setSize(1)
                    .setQuery(queryBuilder)
                    .addSort("fields.end_time.keyword",SortOrder.DESC);


            multiSearchRequestBuilder.add(requestBuilder);

        }

        MultiSearchResponse sr = multiSearchRequestBuilder.get();

// You will get all individual responses from MultiSearchResponse#getResponses()
        long nbHits = 0;
        for (MultiSearchResponse.Item item : sr.getResponses()) {
            SearchResponse response = item.getResponse();
            SearchHit[] searchHits = response.getHits().getHits();
            for (SearchHit hits:searchHits) {
                log.info(hits.getId()+" -- "+hits.getSource());
            }
            nbHits += response.getHits().getTotalHits();
        }
        log.info("");
        return null;
    }


    /**
     * 添加查询模板
     */
    public void addTemplate(String id , String queryJson){
       PutStoredScriptResponse response =  es.client.admin().cluster().preparePutStoredScript()
                .setLang("mustache")
                .setId(id)
                .setContent(new BytesArray(queryJson), XContentType.JSON)
                .get();
       System.out.println(response.isAcknowledged());
    }

    /**
     * 模板查询测试
     * @return
     */
    public String testServer(){

        SearchRequest request = new SearchRequest();
        request.indices("aaa","bbb");
        Map<String, Object> template_params = new HashMap<>();
        template_params.put("name", "zhangsan");
//        template_params.put("from",0);
//        template_params.put("size",10);

//        QueryBuilder queryBuilder = QueryBuilders.
        SearchResponse response = new SearchTemplateRequestBuilder(es.client)
                .setScript("template_gender")
                .setScriptType(ScriptType.STORED)
                .setScriptParams(template_params)
                .setRequest(request)
                .get()
                .getResponse();

        SearchHit[] searchHits = response.getHits().getHits();
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> sourceMap = null;        //接收es参数
        for (SearchHit hits:searchHits) {
            try {
                sourceMap = hits.getSource();
                System.out.println(sourceMap);
                list.add(sourceMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        es.closeClient();
        return JSON.toJSONString(list);
    }
}
