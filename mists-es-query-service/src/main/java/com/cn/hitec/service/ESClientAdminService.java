package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_Exsit;
import com.cn.hitec.repository.ESRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.get.GetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
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
        Map<String,Object> map = new HashMap<>();
        ClusterHealthResponse healths = es.client.admin().cluster().prepareHealth().get();
        map.put("clusterName",healths.getClusterName());
        map.put("numberOfDataNodes",healths.getNumberOfDataNodes());
        map.put("numberOfNodes",healths.getNumberOfNodes());
        System.out.println(JSON.toJSONString(map));
        return map;
    }

    /**
     * 查询index是否为空
     * @param index
     * @return
     * @throws Exception
     */
    public boolean indexIsExist(String index) throws Exception{
        boolean flag = false;
        if(StringUtils.isEmpty(index)){
            throw new Exception("index is null");
        }
        flag = es.exists(index);
        return flag;
    }


    /**
     * 根据查询id，返回id
     * @param json
     * @return
     */
    public String getDocumentById(String json){
        String documentId = null;
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            String index = jsonObject.getString("index");
            String type = jsonObject.getString("type");
            String id = jsonObject.getString("id");

            GetResponse response = es.client.prepareGet(index, type, id).get();
            if (response != null && response.isExists()){
                documentId = response.getId();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            return documentId;
        }
    }
}
