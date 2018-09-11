package com.cn.hitec.repository;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.collect.MapBuilder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ESRepository
 * @Description: ES客户端配置类
 * @author: fukl
 * @data: 2017年05月10日 下午1:14
 */
@Slf4j
@Service
public class ESRepository {
    public TransportClient client = null;               //客户端
    public BulkProcessor bulkProcessor = null;

    @Value("${es.strClusterName}")
    public String strClusterName;

    @Value("${es.strTransportHostNames}")
    private String strTransportHostNames;
    @Value("${es.strPort}")
    private int strPort;
    @Value("${es.index.strTemplateNamePrefixs}")
    private String strTemplateNamePrefixs;

    private Splitter splitter = Splitter.on(",").trimResults();

    /**
     * 创建ES客户端
     * @throws Exception
     */
    public void buildClient() throws Exception {
        long st = System.currentTimeMillis();
        Settings settings = Settings.builder()
                .put("cluster.name", strClusterName)
//                .put("client.transport.sniff",false)
//                .put("xpack.security.user", "elastic:changeme")//for x-pack
                .build();
        Iterable<String> itTransportHostName = splitter.split(strTransportHostNames);
        client = new PreBuiltTransportClient(settings);
//        client = new PreBuiltXPackTransportClient(settings);//for x-pack
        for (String strTransportHostName : itTransportHostName) {

            client.addTransportAddress(
                    new InetSocketTransportAddress(InetAddress.getByName(strTransportHostName), strPort));
        }
        log.info("init client: OK , 耗时："+(System.currentTimeMillis() - st));
    }

    /**
     * 创建ES 数据缓冲池
     * @throws Exception
     */
    public void bulidBulkProcessor() throws Exception {
        bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
//                log.info("---尝试操作 " + request.numberOfActions() + " 条数据---");
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
//                log.info("---尝试操作" + request.numberOfActions() + "条数据成功---");
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                log.info("---elastic录入数据：" + request.numberOfActions() + "条数据失败---");
            }
        }).setBulkActions(5000)
            .setBulkSize(new ByteSizeValue(3, ByteSizeUnit.MB))
            // 固定5s必须刷新一次
            .setFlushInterval(TimeValue.timeValueSeconds(5))
            // 并发请求数量, 0不并发, 1并发允许执行
            .setConcurrentRequests(1)
            // 设置退避, 100ms后执行, 最大请求3次
            .setBackoffPolicy(
                    BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
            .build();
    }

    public void closeClient() {
        if (client != null) {
            client.close();
        }
    }

    public void closeBulkProcessor() {
        if (bulkProcessor != null) {
            bulkProcessor.close();
        }
    }

    // 创建模版
    public void buildTemplate() throws Exception {
        Iterable<String> itTemplateNamePrefix = splitter.split(strTemplateNamePrefixs);
        IndicesAdminClient iac = client.admin().indices();
        for (String strTemplateNamePrefix : itTemplateNamePrefix) {
            PutIndexTemplateRequest pitr = new PutIndexTemplateRequest(strTemplateNamePrefix)
                    .template(strTemplateNamePrefix + "*");
            //number_of_shards 机器数减一,number_of_replicas 备份1份就是两份
            //如果你用单机测试，这段需要注释掉
            //我有18个node，所以设置为18个，分配均匀的话，每个node上会有一个shard，外加一个备份，每个node上有两个shard
            //分片数*副本数=集群数量
//            pitr.settings(new MapBuilder<String, Object>()
//                    .put("number_of_shards", 1)
//                    .put("number_of_replicas", 1)
//                    .put("refresh_interval", "1s").map());
            Map<String, Object> defaultMapping = new HashMap<String, Object>();
            // 关闭_all
            defaultMapping.put("_all", new MapBuilder<String, Object>().put("enabled", false).map());
            defaultMapping.put("numeric_detection", false);
            defaultMapping.put("dynamic_templates",
                    new Object[]{
                            new MapBuilder<String, Object>().put("date_tpl",
                                    new MapBuilder<String, Object>().put("match", "*_time")
                                            .put("mapping",
                                                    new MapBuilder<String, Object>()
                                                            .put("type", "date")
                                                            .put("format", "yyyy-MM-dd HH:mm:ss.SSSZ||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm||epoch_millis")
                                                            .put("index", "not_analyzed")
                                                            .put("doc_values", true)
                                                            .map())
                                            .map())
                                    .map(),
                            new MapBuilder<String, Object>().put("fileSize_tpl",
                                    new MapBuilder<String, Object>().put("match", "file_size")
                                            .put("mapping",
                                                    new MapBuilder<String, Object>()
                                                            .put("type", "long")
                                                            .put("index", "not_analyzed")
                                                            .put("doc_values", true)
                                                            .map())
                                            .map())
                                    .map(),
                            new MapBuilder<String, Object>().put("geo_point_tpl",
                                    new MapBuilder<String, Object>().put("match", "geop*")
                                            .put("mapping",
                                                    new MapBuilder<String, Object>().put("type", "geo_point")
                                                            .put("index", "not_analyzed").put("doc_values", true)
                                                            .map())
                                            .map())
                                    .map(),
                            new MapBuilder<String, Object>().put("all_tpl",
                                    new MapBuilder<String, Object>().put("match", "*").put("mapping",
                                            new MapBuilder<String, Object>().put("type", "{dynamic_type}")
                                                    .put("index", "not_analyzed").put("doc_values", true).map())
                                            .map())
                                    .map()});
            pitr.mapping("_default_", defaultMapping);
            iac.putTemplate(pitr);
        }
    }

    //判断index是否存在
    public boolean exists(String strIndex) {
        IndicesExistsRequest request = new IndicesExistsRequest(strIndex);
        IndicesExistsResponse response = client.admin().indices().exists(request).actionGet();
        if (response.isExists()) {
            return true;
        }
        return false;
    }

    //删除index
    public void delete(String strIndex) {
        if (exists(strIndex)) {
            client.admin().indices().prepareDelete(strIndex).get();
        }
    }

    //创建index
    public void create(String strIndex, int nShards, int nReplicas) {
        client.admin().indices().prepareCreate(strIndex)
                .setSettings(Settings.builder()
                        .put("index.number_of_shards", nShards)
                        .put("index.number_of_replicas", nReplicas)
                        .put("index.refresh_interval", "10s")
                ).get();
    }

    //创建mapping
    public void putMapping(String strIndex, String strType, String strMapping) {
        try {
            client.admin().indices().preparePutMapping(strIndex)
                    .setType(strType)
                    .setSource(strMapping)//这个方法被废弃了，有空了再来收拾它
                    .get();
        } catch (Exception e) {
            log.info("", e);
        }
    }

}
