package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.AlertBean;
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
    public void add(){
        List<String> jsonlist = new ArrayList<>();
        jsonlist.add("{\"name\":\"测试数据2\",\"type\":\"测试数据2\"}");
        esService.add("log_1","FZJC",jsonlist);
    }

    @Test
    public void add_resultId(){
        String json = "{\"name\":\"aaaaa\",\"type\":\"测试数据2\"}";
        AlertBean alertBean = new AlertBean();
        alertBean.setTitle("111");
        alertBean.setAlertType("22");
        String id = esService.add_resultId("log_1","FZJC",JSON.toJSONString(alertBean));
        System.out.println(id);
    }

    @Test
    public void update(){
//        XContentBuilder jsonBuilder = XContentFactory.jsonBuilder();
//                        jsonBuilder.startObject()
//                                .field("alert_status",map.get("alert_status"))
//                                .field("name",map.get("name"))
//                                .field("occur_time",map.get("occur_time"))
//                                .field("receive_time",map.get("receive_time"))
//                                .endObject()
//                                .startObject("fields")
//                                .field("start_time",fields.get("start_time"))
//                                .field("end_time",fields.get("end_time"))
//                                .field("event_info",fields.get("event_info"))
//                                .field("event_status",fields.get("event_status"))
//                                .field("file_name",fields.get("file_name"))
//                                .field("file_size",fields.get("file_size"))
//                                .field("id",fields.get("id"))
//                                .field("ip_addr",fields.get("ip_addr"))
//                                .field("step",fields.get("step"))
//                                .endObject();
//                        //修改数据
//                        logger.info("upd Document : "+ resMap.get("id"));
//                        UpdateRequest updateRequest = new UpdateRequest();
//                        updateRequest.index(index);
//                        updateRequest.type(type);
//                        updateRequest.id(resMap.get("id"));
//                        updateRequest.doc(jsonBuilder);
//                        es.client.update(updateRequest).get();
        List<String> jsonlist = new ArrayList<>();
        jsonlist.add("{\"type\":\"LapsTemperature\",\"name\":\"\",\"occur_time\":1505787603000,\"receive_time\":1505787639567,\"fields\":{\"module\":\"加工\",\"file_name\":\"-1\",\"file_size\":\"-1\",\"start_time\":\"2017-09-19 10:20:00.000+0800\",\"end_time\":\"2017-09-19 10:20:03.000+0800\",\"data_time\":\"2017-09-19 10:20:00.000+0800\",\"step\":1,\"ip_addr\":\"10.30.16.224\",\"event_status\":\"Major\",\"event_info\":\"Submitted.\\nExecuting...\\nNone\\n\\n:数据源[\\\\\\\\10.30.16.242\\\\lapsmoc\\\\lapsprd\\\\lsx\\\\172620200.lsx]不存在。\\nFailed to execute (Laps2TifScript).\\nFailed to execute (LapsfuzhuTemperature).\\nFailed to execute (LapsfuzhuTemperature).\\nFailed.\\n\"}}");
        esService.update("log_20170919","FZJC",jsonlist);
    }

    @Test
    public void update_feild(){
        Map<String,String> params = new HashMap<>();
        params.put("aging_status","正常");
//        esService.update_field("log_1","FZJC","1",params);
    }

    @Test
    public void getDocumentId(){
        List<String> list = new ArrayList<>();
        String json  = "{\"type\":\"雷达\",\"receive_time\":1508290260448,\"name\":\"ACHN.QREF000\"," +
                "\"fields\":{\"start_time\":\"2017-10-25 09:30:05.000+0800\",\"ip_addr\":\"10.30.16.220\",\"ip_addr_target\":\"10.30.16.223\",\"module\":\"采集\"" +
                ",\"data_time\":\"2017-10-25 09:00:00.000+0800\",\"file_name\":\"ACHN.QREF000.20171018.011800.latlon\"," +
                "\"event_status\":\"3\",\"total_time\":\"0.400023\",\"file_size\":\"13563898\",\"mtime\":\"09:37:11\"," +
                "\"event_info\":\"这是一条测试数据3\"," +
                "\"end_time\":\"2017-10-25 09:30:21.000+0800\"},\"occur_time\":1508290211000}";
//        list.add(json);

        String json2 = "{\"occur_time\":1508976362000,\"receive_time\":1508976462000,\"name\":\"雷达\",\"type\":\"雷达\",\"fields\":{\"" +
                "start_time\":\"2017-10-26 08:06:02.000+0800\",\"event_info\":\"正常\",\"" +
                "data_time\":\"2017-10-26 07:36:00.000+0800\",\"file_name\":\"MSP3_PMSC_RADAR_BREF_L88_CHN_201710260736_00000-00000.PNG\",\"module\":\"分发\",\"" +
                "end_time\":\"2017-10-26 08:06:02.000+0800\",\"ip_addr_target\":\"121.40.192.103\",\"ip_addr\":\"10.0.74.226\",\"total_time\":\"0.493829\",\"mtime\":\"08:05:13\",\"file_size\":\"18346\",\"" +
                "event_status\":\"1\"}}";
        list.add(json2);
        esService.update("log_20171026","FZJC" , list);
        System.out.println("");
    }


    @Test
    public void insertTest_T639(){
        List<String> list = new ArrayList<>();
        list.add("{\"receive_time\":1509292854042,\"type\":\"T639\",\"name\":\"T639\",\"fields\":{\"start_time\":\"2017-11-07 00:01:01.000+0800\",\"ip_addr\":\"10.0.74.226\",\"ip_addr_target\":\"121.40.192.103\",\"module\":\"分发\",\"data_time\":\"2017-11-07 08:00:00.000+0800\",\"file_name\":\"T639_GMFS_WIND_2017103008.json\",\"event_status\":\"0\",\"total_time\":\"0.000000\",\"file_size\":\"1035317\",\"mtime\":\"00:00:16\",\"end_time\":\"2017-11-07 00:00:54.000+0800\"},\"occur_time\":1509292854000}");
        list.add("{\"receive_time\":1509292854042,\"type\":\"T639\",\"name\":\"T639\",\"fields\":{\"start_time\":\"2017-11-07 00:01:01.000+0800\",\"ip_addr\":\"10.0.74.226\",\"ip_addr_target\":\"121.40.192.103\",\"module\":\"分发\",\"data_time\":\"2017-11-07 23:00:00.000+0800\",\"file_name\":\"T639_GMFS_WIND_2017103023.json\",\"event_status\":\"0\",\"total_time\":\"0.480946\",\"file_size\":\"1040863\",\"mtime\":\"00:00:19\",\"end_time\":\"2017-11-07 00:00:54.000+0800\"},\"occur_time\":1509292854000}");
        list.add("{\"receive_time\":1509326506268,\"type\":\"T639\",\"name\":\"T639\",\"fields\":{\"start_time\":\"2017-11-07 09:21:01.000+0800\",\"ip_addr\":\"10.0.74.226\",\"ip_addr_target\":\"121.40.192.103\",\"module\":\"分发\",\"data_time\":\"2017-11-07 20:00:00.000+0800\",\"file_name\":\"T639_GMFS_WIND_2017102923.json\",\"event_status\":\"0\",\"total_time\":\"0.825240\",\"file_size\":\"1037285\",\"mtime\":\"09:20:17\",\"end_time\":\"2017-11-07 09:21:02.000+0800\"},\"occur_time\":1509326462000}");
        esService.update("","FZJC" , list);
        System.out.println("");

    }

    @Test
    public void insertTest_hot(){
        List<String> list = new ArrayList<>();
        list.add("{\"occur_time\":1510190402148,\"last_time\":\"2017-11-09 09:26:00.000+0800\",\"should_time\":\"2017-11-09 09:21:00.000+0800\",\"name\":\"20\",\"receive_time\":1510190808694,\"type\":\"炎热指数\",\"fields\":{\"start_time\":\"2017-11-09 09:20:01.192+0800\",\"event_info\":\"正常\",\"data_time\":\"2017-11-09 09:00:00.000+0800\",\"file_name\":\"Z://NoGeography//live//hotIndex//hot2017110909.txt\",\"module\":\"加工\",\"end_time\":\"2017-11-09 09:20:02.148+0800\",\"step\":\"1\",\"ip_addr\":\"10.30.16.223\",\"file_size\":\"139510\",\"event_status\":\"ok\"}}");
        esService.update("","FZJC",list);
        System.out.println("");
    }

}