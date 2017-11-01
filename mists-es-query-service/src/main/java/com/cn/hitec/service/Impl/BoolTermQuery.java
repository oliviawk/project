package com.cn.hitec.service.Impl;

import com.cn.hitec.bean.RangeEs;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.service.BoolTermQuery_I;
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


    @Override
    public List<Map> query_new(String[] indices, String[] types, Map<String, Object> params) throws Exception {
        List<Map> resultList = new ArrayList<>();
        int fromInt = 0;
        int sizeInt = 30;
        String strSort = "";
        String strSortType = "desc";
        boolean isRange = false;
        boolean is_ID = false;
        boolean is_Type = false;
        boolean is_Index = false;
        //值查询
        List<QueryBuilder> rangeBuilderList = null;
        //创建查询类
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

//        RangeQueryBuilder rangeQueryBuilder = null;
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

            if(params.containsKey("range")){
                isRange = true;
                rangeBuilderList = new ArrayList<>();
                List<Map> rangeList = (List<Map>)params.get("range");
                for(Map<String,Object>  map : rangeList){
                    RangeQueryBuilder rangeQueryBuilder = null;
                    for (String key:map.keySet()){
                        if(key.equals("name")){
                            rangeQueryBuilder = QueryBuilders.rangeQuery(map.get(key).toString());
                            continue;
                        }
                        rangeQueryBuilder = RangeChoiceTest(key,rangeQueryBuilder,map.get(key));
                    }
                    rangeBuilderList.add(rangeQueryBuilder);

                }
            }

            if(params.containsKey("must")){
                Map<String,Object> mustMap = (Map<String,Object>)params.get("must");
                for (String strKey : mustMap.keySet()){
                    queryBuilder.must(QueryBuilders.termQuery(strKey,mustMap.get(strKey)));
                }
            }
            if(params.containsKey("mustNot")){
                Map<String,Object> mustNotMap = (Map<String,Object>)params.get("mustNot");
                for (String strKey : mustNotMap.keySet()){
                    queryBuilder.mustNot(QueryBuilders.termQuery(strKey,mustNotMap.get(strKey)));
                }
            }

            if(isRange){
                for (QueryBuilder qb:rangeBuilderList){
                    queryBuilder.must(qb);
                }
            }
        }

        log.info(queryBuilder.toString());
        SearchRequestBuilder requestBuilder = es.client.prepareSearch(indices)
                .setTypes(types)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder);

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
                if(is_ID){
                    hits.getSource().put("_id",hits.getId());
                }
                if(is_Type){
                    hits.getSource().put("_type",hits.getType());
                }
                if(is_Index){
                    hits.getSource().put("_index",hits.getIndex());
                }
//                sourceMap = hits.getSource();
                resultList.add(hits.getSource());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
                        rangeQueryBuilder = RangeChoiceTest(key,rangeQueryBuilder,map.get(key));
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
                        rangeQueryBuilder = RangeChoiceTest(key,rangeQueryBuilder,map.get(key));
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
        if(isRange){
            for (QueryBuilder qb:rangeQueryBuilderList){
                queryBuilder.must(qb);
            }
        }

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


    public RangeQueryBuilder RangeChoiceTest(String rangeType,RangeQueryBuilder builder , Object param){
        if("gt".equals(rangeType)) {
           builder.gt(param);
       }else if("gte".equals(rangeType)) {
           builder.gte(param);
       }else if("lt".equals(rangeType)) {
           builder.lt(param);
       }else if("lte".equals(rangeType)) {
           builder.lte(param);
       }

       return builder;
    }
}
