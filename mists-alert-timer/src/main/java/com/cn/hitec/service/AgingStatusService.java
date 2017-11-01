package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsWriteBean;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.util.Pub;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

/**
 * @Description: 这里是描述信息
 * @author: fukl
 * @data: 2017年09月30日 21:56
 */
@Service
public class AgingStatusService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AgingStatusService.class);
    @Autowired
    EsQueryService esQueryService;
    @Autowired
    SendAlertMessage sendMessage;
    @Autowired
    EsWriteService esWriteService;

    public int collect_task(Date nowDate){
        int up_number = 0;
        try {

            EsQueryBean esQueryBean = new EsQueryBean();
            String index = Pub.Index_Head+ Pub.transform_DateToString(nowDate,Pub.Index_Food_Simpledataformat);
            esQueryBean.setIndices(new String[]{index});
            esQueryBean.setTypes(new String[]{"FZJC"});

            Map<String,Object> params = new HashMap<>();
//            params.put("type.keyword","satellite");
//            params.put("fields.module.keyword","采集");
            params.put("aging_status.keyword","未处理");
            params.put("size","50");
//            params.put("sort","fields.data_time.keyword");
            List<Map> list = new ArrayList<>();
            Map<String,String> map = new HashMap<>();
            map.put("name","last_time.keyword");
            map.put("lt", Pub.transform_DateToString(nowDate,"yyyy-MM-dd HH:mm:ss.SSSZ"));
            list.add(map);
            params.put("range",list);

            esQueryBean.setParameters(params);

//            System.out.println(Pub.transform_DateToString(nowDate,"yyyy-MM-dd HH:mm:ss.SSSZ"));
            //查询到 所有未处理状态的数据，按照资料时间排序
            Map<String , Object> responseMap = esQueryService.getData_resultId(esQueryBean);

            if(!responseMap.get(Pub.KEY_RESULT).equals(Pub.VAL_SUCCESS)){
                return -1;
            }

            //得到结果集
            Map<String,Object> tempMap  = (Map<String,Object>) responseMap.get(Pub.KEY_RESULTDATA);

            Map<String,Object> fields = null;
            EsWriteBean esWriteBean = null;
            for (String uid : tempMap.keySet()){
                try {
                    fields = (Map<String, Object>) tempMap.get(uid);

                    Map<String,Object> pam = new HashMap<>();
                    esWriteBean = new EsWriteBean();
                    esWriteBean.setIndex(index);
                    esWriteBean.setType("FZJC");
                    esWriteBean.setId(uid);
                    pam.put("aging_status","超时");
                    esWriteBean.setParams(pam);
                    esWriteService.update_field(esWriteBean);
                    up_number ++;

                    sendMessage.sendAlert(index,"alert",fields);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            logger.info("---查询出: "+tempMap.size() +" 条超时数据，修改了："+up_number+" 条");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return up_number;
    }

}
