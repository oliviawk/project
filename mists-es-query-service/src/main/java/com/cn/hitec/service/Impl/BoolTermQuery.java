package com.cn.hitec.service.Impl;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.RangeEs;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.service.BoolTermQuery_I;
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
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: Bool 查询类
 * @author: fukl
 * @data: 2017年09月21日 17:05
 */
@Slf4j
@Service
public class BoolTermQuery implements BoolTermQuery_I{
    @Autowired
    private ESRepository es;


    /**
     *
     * @param indices   //不可为空
     * @param types     //可以为空
     * @param params    //可以为空
     *       {
     *          "_id":"true",               //是否返回id，默认false
     *          "_type":"true",             //是否返回type，默认false
     *          "_index":"true",            //是否返回index，默认false
     *          "from":"10",                //从第几条开始返回数据， 默认0
     *          "size":"30",                //返回多少条数据，默认30
     *          "sortType":"desc",          //排序类型，默认desc倒序
     *          "sort":"last_time",         //可以为空
     *          "resultAll":"false",        //是否返回所有数据 , 默认false
     *          "must":{                    //可以为空,term查询，类似 ==
     *              "key_name":"value",
     *                  .....
     *          },
     *          "mustNot":{                 //可以为空，term查询，类似 !=
     *              "key_name":"value",
     *                  .....
     *          },
     *          "range":[                   //可以为空，数值比较查询
     *              {
     *                  "name":"value",
     *                  "gt":"10",
     *                  "lte":"30"
     *              },
     *              ....
     *          ]
     *       }
     * @return
     * @throws Exception
     */
    @Override
    public List<Map> query_new(String[] indices, String[] types, Map<String, Object> params) throws Exception {
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
                for (QueryBuilder qb:rangeBuilderList){
                    queryBuilder.must(qb);
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


    @Override
    public List<Map> query(String[] indices, String[] types, Map<String, Object> params) throws Exception {
        List<Map> resultList = new ArrayList<>();
        int fromInt = 0;
        int sizeInt = 30;
        String strSort = "";
        String strSortType = "desc";
        boolean isRange = false;
        List<QueryBuilder> rangeQueryBuilderList = null;
//        RangeQueryBuilder rangeQueryBuilder = null;
        if(params != null){
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
            if(params.containsKey("range")){
                isRange = true;
                rangeQueryBuilderList = new ArrayList<>();
                List<Map> rangeList = (List<Map>)params.get("range");
                for(Map<String,Object>  map : rangeList){
                    RangeQueryBuilder rangeQueryBuilder = null;
//                    rangeQueryBuilder = QueryBuilders.rangeQuery(map.get("name").toString()).lt("2017-09-29 23:00:00.000+0800");
                    for (String key:map.keySet()){
                        if(key.equals("name")){
                            rangeQueryBuilder = QueryBuilders.rangeQuery(map.get(key).toString());
                            continue;
                        }
                        rangeQueryBuilder = Pub.RangeChoiceTest(key,rangeQueryBuilder,map.get(key));
                    }
                    rangeQueryBuilderList.add(rangeQueryBuilder);

                }
                params.remove("range");
            }
        }

        //创建查询类
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        for (String strKey : params.keySet()){
            queryBuilder.must(QueryBuilders.termQuery(strKey,params.get(strKey)));
        }
//        queryBuilder.must(rangeQueryBuilder);
//        queryBuilder.must(QueryBuilders.rangeQuery("last_time.keyword").lte("2017-09-29 23:00:00.000+0800"));
        if(isRange){
            for (QueryBuilder qb:rangeQueryBuilderList){
                queryBuilder.must(qb);
            }
        }

//        log.info(queryBuilder.toString());
        SearchRequestBuilder requestBuilder = es.client.prepareSearch(indices)
                .setTypes(types)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder);
//                .setPostFilter(rangeQueryBuilder);

        if(!StringUtils.isEmpty(strSort)){
            requestBuilder.addSort(strSort, strSortType.equals("desc")? SortOrder.DESC : SortOrder.ASC);
        }
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

    @Override
    public Map query_resultId(String[] indices, String[] types, Map<String, Object> params) throws Exception {
        Map resultMap = new HashMap();
        int fromInt = 0;
        int sizeInt = 30;
        String strSort = "";
        String strSortType = "desc";
        boolean isRange = false;
        List<QueryBuilder> rangeQueryBuilderList = null;
        if(params != null){
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
            if(params.containsKey("range")){
                isRange = true;
                rangeQueryBuilderList = new ArrayList<>();
                List<Map> rangeList = (List<Map>)params.get("range");
                for(Map<String,Object>  map : rangeList){
                    RangeQueryBuilder rangeQueryBuilder = null;
//                    rangeQueryBuilder = QueryBuilders.rangeQuery(map.get("name").toString()).lt("2017-09-29 23:00:00.000+0800");
                    for (String key:map.keySet()){
                        if(key.equals("name")){
                            rangeQueryBuilder = QueryBuilders.rangeQuery(map.get(key).toString());
                            continue;
                        }
                        rangeQueryBuilder = Pub.RangeChoiceTest(key,rangeQueryBuilder,map.get(key));
                    }
                    rangeQueryBuilderList.add(rangeQueryBuilder);

                }
                params.remove("range");
            }
        }

        //创建查询类
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        for (String strKey : params.keySet()){
            String strValue = params.get(strKey).toString();
            if(strValue.indexOf(",") > -1){
                String[] strValues = strValue.split(",");
                queryBuilder.must(QueryBuilders.termsQuery(strKey,strValues));
            }else{
                queryBuilder.must(QueryBuilders.termQuery(strKey,params.get(strKey)));
            }

        }
        if(isRange){
            for (QueryBuilder qb:rangeQueryBuilderList){
                queryBuilder.must(qb);
            }
        }

//        log.info(queryBuilder.toString());
        SearchRequestBuilder requestBuilder = es.client.prepareSearch(indices)
                .setTypes(types)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder);
//                .setPostFilter(rangeQueryBuilder);

        if(!StringUtils.isEmpty(strSort)){
            requestBuilder.addSort(strSort, strSortType.equals("desc")? SortOrder.DESC : SortOrder.ASC);
        }
        if(fromInt > 0){
            requestBuilder.setFrom(fromInt);
        }
        requestBuilder.setSize(sizeInt);
        //创建查询
        SearchResponse response = requestBuilder.setScroll(new TimeValue(3000)).get();

        //Scroll until no hits are returneds
        do {
            for (SearchHit hits : response.getHits().getHits()) {
                //Handle the hit...
                try {
                    hits.getSource().put("_id",hits.getId());
                    hits.getSource().put("_type",hits.getType());
                    resultMap.put(hits.getId(),hits.getSource());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            response = es.client.prepareSearchScroll(response.getScrollId())
                    .setScroll(new TimeValue(3000))
                    .execute().actionGet();
        } while(response.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.

        return resultMap;
    }


}
