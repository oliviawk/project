package com.cn.hitec.service;

import com.cn.hitec.repository.ESRepository;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description: 描述信息
 * @author: fukl
 * @data: 2018年07月23日 下午5:25
 */
public class EsQueryService {
    @Autowired
    private ESRepository es;

    public SearchResponse searchResponse(String[] indices, String[] types, QueryBuilder queryBuilder , AggregationBuilder aggregationBuilder){
        SearchResponse response = null;

        //返回查询结果
       response = es.client.prepareSearch(indices)
                .setTypes(types)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder).setSize(0)
                .addAggregation(aggregationBuilder)
                .setExplain(true).get();

       return response;
    }


}
