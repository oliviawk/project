package hitec.service;

import com.alibaba.fastjson.JSON;
import hitec.repository.ESRepository;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
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
        System.out.println("0000000000000000000000---"+JSON.toJSONString(map));
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
}
