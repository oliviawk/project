package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.tools.Pub;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetResponse;
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
@Slf4j
@Service
public class ESClientAdminService {

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
    public Map<String , Object> getClusterHealth(ESRepository es){
        log.info("getClusterHealth:");
        Map<String,Object> map = new HashMap<>();
        ClusterHealthResponse healths = es.client.admin().cluster().prepareHealth().get();
        map.put("clusterName",healths.getClusterName());
        map.put("numberOfDataNodes",healths.getNumberOfDataNodes());
        map.put("numberOfNodes",healths.getNumberOfNodes());
        log.info("ES health:"+JSON.toJSONString(map));
        return map;
    }

    public String[] indexExists( ESRepository es,String[] indices) throws Exception{
        if(indices == null){
            return null;
        }
        List<String> list = new ArrayList();
        for(int i = 0 ; i < indices.length ; i++){
            if(Pub.indexExitsList.contains(indices[i])){
                list.add(indices[i]);
                continue;
            }
            IndicesExistsResponse response = es.client.admin().indices().prepareExists(indices[i]).execute().actionGet();
            if (response.isExists()) {
                list.add(indices[i]);
                Pub.indexExitsList.add(indices[i]);
            }else{
                log.warn(indices[i]+"不存在");
            }
        }

        String[] resultIndices = list.toArray(new String[list.size()]);

        return resultIndices;
    }



    /**
     * 查询单条数据
     *
     * @param indexs
     * @param type
     * @param id
     * @return
     */
    public Map<String, Object> getDocumentById(ESRepository es,String[] indexs, String type, String id) {
        Map<String, Object> resultMap = new HashMap<>();
        try {

            String[] indices = indexExists(es,indexs);
            if (indices == null || indices.length < 1) {
                return resultMap;
            }
            for (String s : indices){
//			    System.out.println(s+"--"+type+"---"+id);
                GetResponse response = es.client.prepareGet(s, type, id).get();
                if (response != null && response.getSource() != null){
                    resultMap = response.getSource();
                    resultMap.put("_id", response.getId());
                    resultMap.put("_type", response.getType());
                    resultMap.put("_index", response.getIndex());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return resultMap;
        }

    }

}
