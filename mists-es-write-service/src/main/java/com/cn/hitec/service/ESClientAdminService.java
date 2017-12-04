package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.repository.ESRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName:
 * @Description: 获取ES状态信息
 * @author: fukl
 * @data: 2017年05月10日 下午1:14
 */
@Service
public class ESClientAdminService {
    private static final Logger logger = LoggerFactory.getLogger(ESClientAdminService.class);
    @Autowired
    private ESRepository es;

//    public void clusterHealth() {
//        ClusterHealthResponse healths = es.client.admin().cluster().prepareHealth().get();
//        String clusterName = healths.getClusterName();
//        int numberOfDataNodes = healths.getNumberOfDataNodes();
//        int numberOfNodes = healths.getNumberOfNodes();
//        log.info("getClusterName:{}, getNumberOfDataNodes:{}, getNumberOfNodes:{}", clusterName, numberOfDataNodes, numberOfNodes);
//        for (ClusterIndexHealth health : healths.getIndices().values()) {
//            String index = health.getIndex();
//            int numberOfShards = health.getNumberOfShards();
//            int numberOfReplicas = health.getNumberOfReplicas();
//            ClusterHealthStatus status = health.getStatus();
//            status.value();
//            log.info("index:{}, numberOfShards:{}, numberOfReplicas:{}, status.name:{}, status.value:{}", index, numberOfShards, numberOfReplicas, status.name(), status.value());
//        }
//    }

    /**
     * 查询集群健康状态
     * @return  Map
     */
    public Map<String , Object> getClusterHealth(){
        System.out.println("getClusterHealth:");
        Map<String,Object> map = new HashMap<>();
        ClusterHealthResponse healths = es.client.admin().cluster().prepareHealth().get();
        map.put("clusterName",healths.getClusterName());
        map.put("numberOfDataNodes",healths.getNumberOfDataNodes());
        map.put("numberOfNodes",healths.getNumberOfNodes());
        System.out.println("ES health:"+JSON.toJSONString(map));
        return map;
    }

    public String[] indexExists(String[] indices){
        if(indices == null){
            return null;
        }
        List<String> list = new ArrayList();
        for(int i = 0 ; i < indices.length ; i++){
            IndicesExistsRequest request = new IndicesExistsRequest(indices[i]);
            IndicesExistsResponse response = es.client.admin().indices().exists(request).actionGet();
            if (response.isExists()) {
                list.add(indices[i]);
            }else{
                logger.warn("------------未查询到index:"+indices[i]);
            }
        }

        String[] resultIndices = new String[list.size()];
        for (int i = 0; i < list.size() ; i++){
            resultIndices[i] = list.get(i);
        }
        return resultIndices;
    }
}
