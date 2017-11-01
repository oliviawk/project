package com.cn.hitec.service;

import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.tools.Pub;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    EsQueryService esQueryService;
    /**
     * 查询
     * @return
     */
    public List<Map> getConfigAlert(String type){
        List<Map> resultList = null;

        if(StringUtils.isEmpty(type)){
            logger.error("请输入type");
            return null;
        }
        try {
            EsQueryBean esQueryBean = new EsQueryBean();
            esQueryBean.setIndices(new String[]{"config"});
            esQueryBean.setTypes(new String[]{type});

            Map<String,Object> resultMap = esQueryService.getAlertData(esQueryBean);
            if(resultMap == null ){
                logger.warn("getConfigAlert is null");
                return null;
            }else {
                if(resultMap.get("result").equals("success")){
                    resultList = (List<Map>) resultMap.get("resultData");
                }else{
                    logger.warn(resultMap.get("message").toString());
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            resultList = null;
        } finally {
            return resultList;
        }
    }

    public void initAlertMap(){
        /*---------------------采集------------------------*/
        List<Map> listMap_collect = getConfigAlert("collect");
        for (Map map : listMap_collect){
            String DI_name = map.get("DI_name").toString();
            Pub.alertMap_collect.put(DI_name,map);
        }
        List<Map> listMap_machining = getConfigAlert("machining");
        for (Map map : listMap_machining){
            String DI_name = map.get("DI_name").toString();
            Pub.alertMap_machining.put(DI_name,map);
        }
        List<Map> listMap_distribute = getConfigAlert("distribute");
        for (Map map : listMap_distribute){
            String DI_name = map.get("DI_name").toString();
            Pub.alertMap_distribute.put(DI_name,map);
        }
    }
}
