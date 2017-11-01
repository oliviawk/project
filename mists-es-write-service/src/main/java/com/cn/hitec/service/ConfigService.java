package com.cn.hitec.service;

import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.tools.Pub;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 这里是描述信息
 * @author: fukl
 * @data: 2017年10月26日 14:29
 */
@Service
public class ConfigService {
    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);
    @Autowired
    ESRepository esRepository;

    public void initAlertMap(){
        try {
        /*---------------------采集------------------------*/
            List<Map> listMap_collect = getConfigAlert("config","collect",null);
            for (Map map : listMap_collect){
                String DI_name = map.get("DI_name").toString();
                Pub.alertMap_collect.put(DI_name,map);
            }

            List<Map> listMap_machining = getConfigAlert("config","machining",null);
            for (Map map : listMap_machining){
                String DI_name = map.get("DI_name").toString();
                Pub.alertMap_machining.put(DI_name,map);
            }

            List<Map> listMap_distribute = getConfigAlert("config","distribute",null);
            for (Map map : listMap_distribute){
                String DI_name = map.get("DI_name").toString();
                Pub.alertMap_distribute.put(DI_name,map);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            logger.error(e.getMessage());
            resultList = null;
        } finally {
            return resultList;
        }
    }
}
