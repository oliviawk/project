package com.cn.hitec.repository;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPost;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

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
//    public RestClientBuilder restClient = null;
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
                .build();
        Iterable<String> itTransportHostName = splitter.split(strTransportHostNames);
        client = new PreBuiltTransportClient(settings);
//        List<HttpHost> httpHostList = new ArrayList<>();
        for (String strTransportHostName : itTransportHostName) {

            client.addTransportAddress(
                    new InetSocketTransportAddress(InetAddress.getByName(strTransportHostName), strPort));
//            HttpHost httpHost = new HttpHost(strTransportHostName,strPort,"http");
//            httpHostList.add(httpHost);
        }
//        HttpHost[] httpHosts = httpHostList.toArray(new HttpHost[httpHostList.size()]);
//        restClient =  RestClient.builder(httpHosts);

        log.info("init client: OK , 耗时:"+(System.currentTimeMillis() - st));
    }

    /**
     * 创建ES 数据缓冲池
     * @throws Exception
     */
    public void bulidBulkProcessor() throws Exception {
        bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
            }
        }).setBulkActions(5000)
                .setBulkSize(new ByteSizeValue(10, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(10))
                .setConcurrentRequests(1)
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
