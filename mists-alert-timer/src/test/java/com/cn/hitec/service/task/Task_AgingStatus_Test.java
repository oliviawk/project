package com.cn.hitec.service.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsWriteBean;
import com.cn.hitec.domain.DataInfo;
import com.cn.hitec.domain.Users;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.repository.jpa.DataInfoRepository;
import com.cn.hitec.repository.jpa.UsersRepository;
import com.cn.hitec.service.AgingStatusService;
import com.cn.hitec.service.ConfigService;
import com.cn.hitec.service.SendAlertMessage;
import com.cn.hitec.util.Pub;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({ "dev" })
public class Task_AgingStatus_Test {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AgingStatusService.class);
    @Autowired
    EsQueryService esQueryService;
    @Autowired
    SendAlertMessage sendMessage;
    @Autowired
    EsWriteService esWriteService;
    @Autowired
    DataInfoRepository dataInfoRepository;
    @Autowired
    UsersRepository usersRepository;


    @Test
    public void test4(){
        //查询发送的用户
        String[] strIds = "100,101".split(",");
        long[] longIds = new long[strIds.length];
        for (int i = 0 ; i < strIds.length ; i ++){
            longIds[i] = Long.parseLong(strIds[i]);
        }
        List<Users> usersList = usersRepository.findAllByIds(longIds);
        String strUsers = "";
        for (Users use : usersList){
            if ("".equals(strUsers)){
                strUsers += use.getWechart();
            }else {
                strUsers += "|"+use.getWechart();
            }

        }

        System.out.println(strUsers);
    }


    @Test
    public void test2() throws Exception{
        int up_number = 0;
        int all_number = 0;
        Date nowDate = new Date();
        EsQueryBean esQueryBean = new EsQueryBean();
        String[] str_indexs = Pub.getIndices(new Date(), 1);
        esQueryBean.setIndices(str_indexs);
        esQueryBean.setTypes(new String[]{"DATASOURCE"});

        Map<String, Object> mustMap = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        mustMap.put("aging_status", "未处理");

        List<Map> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "last_time");
        map.put("lt", Pub.transform_DateToString(nowDate, "yyyy-MM-dd HH:mm:ss"));
        list.add(map);
        params.put("range", list);
        params.put("must", mustMap);
        params.put("sort", "fields.data_time");
        params.put("sortType", "asc");
        params.put("_index", "true");
        params.put("_type", "true");
        params.put("_id", "true");
        params.put("resultAll", true);

        esQueryBean.setParameters(params);

        System.out.println(JSON.toJSONString(esQueryBean));
        // 查询到 所有未处理状态的数据，按照资料时间排序
        Map<String, Object> responseMap = esQueryService.getData_new(esQueryBean);

        if (responseMap != null && Pub.VAL_SUCCESS.equals(responseMap.get(Pub.KEY_RESULT))) {
            List dataList = (List) responseMap.get(Pub.KEY_RESULTDATA);
            System.out.println("--> "+ JSON.toJSONString(dataList));
            all_number = dataList.size();
            for (Object object : dataList) {
                try {
                    Map<String, Object> objMap = (Map<String, Object>) object;
                    System.out.println(JSON.toJSONString(objMap));
                    String str_index = objMap.get("_index").toString();
                    String str_type = objMap.get("_type").toString();
                    String str_id = objMap.get("_id").toString();

                    Map<String, Object> pam = new HashMap<>();
                    EsWriteBean esWriteBean = new EsWriteBean();
                    esWriteBean.setIndex(str_index);
                    esWriteBean.setType(str_type);
                    esWriteBean.setId(str_id);
                    pam.put("aging_status", "超时");
                    esWriteBean.setParams(pam);
                    esWriteService.update_field(esWriteBean);
                    up_number++;

                    if ("yes".equals(objMap.get("startMoniter").toString())) {
                        //修改发送消息代码，先入es，再定时执行发送， 这样不会妨碍入库程序的速度
                        sendMessage.sendAlert(str_index, "alert", objMap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            logger.error(JSON.toJSONString(responseMap));
        }

        logger.info("---查询出: " + all_number + " 条数据源 超时 数据，修改了：" + up_number + " 条");
    }

    @Test
    public void testGetDocumentId(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("index","hx_20180413");
        jsonObject.put("type","alert");
        jsonObject.put("id","9D7478EFC36F10D85FA1D719650AA0DF");
        String id = esQueryService.getDocumentById(jsonObject.toJSONString());
        System.out.println(id);
        if (StringUtils.isEmpty(id)){
            System.out.println("2");
        }else {
            System.out.println("1");
        }
    }
}