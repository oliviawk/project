package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.EsQueryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ESServiceTest {
    @Autowired
    ESService esService;

    @Test
    public void testServer() throws Exception {
        esService.testServer();
    }

    @Test
    public void addTemplate() throws Exception{
//        String queryJson = "{\n" +
//                "  \"query\": {\n" +
//                "    \"term\": {\n" +
//                "      \"name\": \"{{name}}\"\n" +
//                "    }\n" +
//                "  }\n" +
//                "}";
//        esService.addTemplate("template_gender",queryJson);
        String queryJsonMust = "{\n" +
                "    \"query\": {\n" +
                "        \"bool\": {\n" +
                "            \"must\": [\n" +
                "                {\n" +
                "                    \"term\": {\n" +
                "                        \"fields.module.keyword\": \"{{module}}\"\n" +
                "                    }\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"sort\": [\n" +
                "        {\n" +
                "            \"receive_time\": {\n" +
                "                \"order\": \"{{order}}{{^order}}desc{{/order}}\"\n" +
                "            }\n" +
                "        }\n" +
                "    ],\n" +
                "    \"from\": \"{{from}}{{^from}}0{{/from}}\",\n" +
                "    \"size\": \"{{size}}{{^size}}50{{/size}}\"\n" +
                "}";
        System.out.println(queryJsonMust);
        esService.addTemplate("query_must_test",queryJsonMust);
    }

    @Test
    public void find() throws Exception{
        String[] indices = new String[]{"log_20170906"};
        String[] types = new String[]{"FZJC"};
        Map<String,Object> params = new HashMap<>();
        params.put("module","采集");
        List<Map<String,Object>> list = esService.find(indices,types,"query_must_test",params);
        for (Map map: list) {
            System.out.println(JSON.toJSONString(map));
        }
    }

    @Test
    public void find2() throws  Exception{
//        String[] indices = new String[]{"log_20170906"};
//        String[] types = new String[]{"FZJC"};
//        List<Map> list = esService.find(indices,types,"采集");
//        for (Map map: list) {
//            System.out.println(JSON.toJSONString(map));
//        }
    }

    @Test
    public void getDocumentID(){
        String json = "{\"type\":\"LAPS3KM\",\"name\":\"\",\"occur_time\":1505068250011,\"receive_time\":1505068245624,\"fields\":{\"data_time\":\"2017-09-15 02:00:00.000+0800\",\"end_time\":\"2017-09-15 02:30:50.011+0800\",\"event_info\":\"数据接口获取LAPS3KM数据失败\",\"event_status\":\"Major\",\"file_name\":\"-1\",\"file_size\":\"-1\",\"ip_addr\":\"120.26.9.109\",\"module\":\"采集\",\"start_time\":\"2017-09-15 02:30:50.003+0800\",\"step\":\"1\"}}";
        Map<String,Object> map = JSON.parseObject(json);
//        esService.getDocumentId("log_20170915","FZJC",map);

    }

    @Test
    public void MutilQuery(){
        List<EsQueryBean> list = new ArrayList<>();
        EsQueryBean queryBean1 = new EsQueryBean();
        queryBean1.setIndices(new String[]{"log_20170928"});
        queryBean1.setTypes(new String[]{"FZJC"});
        Map<String,Object> params = new HashMap<>();
        params.put("type.keyword","satellite");
        params.put("fields.module.keyword","采集");
//        params.put("fields.data_time.keyword","2017-09-28 12:00:00.000+0800");
        queryBean1.setParameters(params);

        EsQueryBean queryBean2 = new EsQueryBean();
        queryBean2.setIndices(new String[]{"log_20170928"});
        queryBean2.setTypes(new String[]{"FZJC"});
        Map<String,Object> params2 = new HashMap<>();
        params2.put("type.keyword","ReadFY2NC");
        params2.put("fields.module.keyword","加工");
//        params2.put("fields.data_time.keyword","2017-09-28 12:00:00.000+0800");
        queryBean2.setParameters(params2);

        EsQueryBean queryBean3 = new EsQueryBean();
        queryBean3.setIndices(new String[]{"log_20170928"});
        queryBean3.setTypes(new String[]{"FZJC"});
        Map<String,Object> params3 = new HashMap<>();
        params3.put("type.keyword","FY-2E/G云图数据");
        params3.put("fields.module.keyword","分发");
//        params2.put("fields.data_time.keyword","2017-09-28 12:00:00.000+0800");
        queryBean3.setParameters(params2);


        list.add(queryBean1);
        list.add(queryBean2);
        esService.MutilQuery(list);
    }
}