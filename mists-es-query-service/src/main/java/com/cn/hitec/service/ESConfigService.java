package com.cn.hitec.service;

import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.repository.ESRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName:
 * @Description: 查询配置文件
 * @author: fukl
 * @data: 2017年08月3日 下午1:14
 */
@Slf4j
@Service
public class ESConfigService {
    @Autowired
    private ESRepository es;


    /**
     * 查询配置表信息
     * @param indices
     * @param types
     * @param params
     * @return
     * @throws Exception
     */
    public List<Map> getConfigAlert(String[] indices, String[] types ,Map<String,Object> params) throws Exception{
        List<Map> resultList = new ArrayList<>();
        int sizeInt = 100;
        long timeValue = 15000;
        try {
            if (params != null){
                if(params.get("size") != null && (Integer)params.get("size") > 0){
                    sizeInt = (Integer)params.get("size");
                }
                if(params.get("timeValue") != null && (long)params.get("timeValue") > 0){
                    timeValue = (Integer)params.get("timeValue");
                }
            }
            SearchResponse scrollResp = es.client.prepareSearch(indices)
                    .setTypes(types)
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

                scrollResp = es.client.prepareSearchScroll(scrollResp.getScrollId())
                        .setScroll(new TimeValue(timeValue))
                        .execute().actionGet();
            } while(scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            resultList = null;
        } finally {
            return resultList;
        }
    }

}
