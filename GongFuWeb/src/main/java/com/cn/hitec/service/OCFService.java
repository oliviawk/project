package com.cn.hitec.service;

import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_web;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.tools.Pub;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.util.*;

@Service
public class OCFService extends BaseService {

    @Autowired
    private EsQueryService esQueryService;

    public Map<String, Object> aggQuery(){
        return aggQuery(new String[]{"OCF"},"CH_MERGE_1H,AGLB_MERGE_1H,AGLB_1H,CH_1H,CH_MERGE_3H,AGLB_MERGE_3H,AGLB_3H,CH_3H,CH_MERGE_12H,AGLB_MERGE_12H,AGLB_12H,CH_12H,T639_NEW,NCEP_NEW,RJTD_NEW,ECMWF_NEW,OCF_FINAL,AGLB_MERGE_1H_DEBACKUP,AGLB_1H_DEBACKUP,CH_1H_DEBACKUP,CH_MERGE_1H_DEBACKUP,CH_MERGE_3H_DEBACKUP,AGLB_MERGE_3H_DEBACKUP,AGLB_3H_DEBACKUP,CH_3H_DEBACKUP,CH_MERGE_12H_DEBACKUP,AGLB_MERGE_12H_DEBACKUP,AGLB_12H_DEBACKUP,CH_12H_DEBACKUP,OCF_1H_UPDATE,OCF_3H_UPDATE,OCF_12H_UPDATE,OCF_JIANGJI,OCF1H_ME_L88_GLB,OCF3H_ME_L88_GLB,OCF12H_ME_L88_GLB,BHFK,AGLB_OBS,CH_OBS");
    }


    public Map<String, Object> getHistorys(EsQueryBean_web esQueryBean){

        long start = System.currentTimeMillis();
        Map<String,Object> mapObject = null;
        Map outMap = new HashMap();
        try {
            //判断参数是否正确
            if(esQueryBean == null){
                outMap.put(Pub.KEY_RESULT,Pub.VAL_ERROR);
                outMap.put(Pub.KEY_RESULTDATA,null);
                outMap.put(Pub.KEY_MESSAGE,"参数错误！");
            }else{
                if(StringUtils.isEmpty(esQueryBean.getIndices())){
                    // 查询今天和昨天的记录
                    String index = Pub.Index_Head + DateFormatUtils.format(new Date(), Pub.Index_Food_Simpledataformat);
                    Date d2 = DateUtils.addDays(new Date(), -1);    // 昨天的日期
                    String index2 = Pub.Index_Head + DateFormatUtils.format(d2, Pub.Index_Food_Simpledataformat);
                    esQueryBean.setIndices(new String[] { index, index2 });
                }

                Map<String,Object> params = new HashMap<>();    //查询参数

                // 不区分过程和类型直接查
                Date d1 = DateUtils.truncate(new Date(), Calendar.HOUR);
                String dateStr = DateFormatUtils.format(d1, "yyyy-MM-dd HH:mm:ss.SSSZ");



                Map<String, Object> mustMap = new HashMap<>();
                if(esQueryBean.getSubType().indexOf(",") > -1){
                    String[] arr = esQueryBean.getSubType().split(",");
                    List<String> arrList = Arrays.asList(arr);
                    mustMap.put("type",arrList);
                }else{
                    mustMap.put("type",esQueryBean.getSubType());
                }

                mustMap.put("fields.module",esQueryBean.getModule());
                mustMap.put("fields.ip_addr",esQueryBean.getStrIp());

                Map<String, Object> mustNotMap = new HashMap<>();
                mustNotMap.put("aging_status", "未处理");

                params.put("must", mustMap);
                params.put("mustNot", mustNotMap);

                List<Map> rangeList = new ArrayList<>();
                Map<String,String> map = new HashMap<>();
                map.put("name","fields.data_time");
                map.put("lte", dateStr);
                rangeList.add(map);
                params.put("range",rangeList);

                params.put("size",esQueryBean.getSize());
                params.put("sort","fields.data_time");


                // 去做查询
                EsQueryBean esQuery = new EsQueryBean();
                esQuery.setIndices(esQueryBean.getIndices());
                esQuery.setTypes(esQueryBean.getTypes());
                esQuery.setParameters(params);

                mapObject = esQueryService.getData_new(esQuery);
                //这里做一些数据字段 转换、过滤
                outMap.put(Pub.KEY_RESULT,mapObject.get(Pub.KEY_RESULT));
                outMap.put(Pub.KEY_MESSAGE,mapObject.get(Pub.KEY_MESSAGE));
                outMap.put(Pub.KEY_RESULTDATA,mapObject.get("resultData"));
                outMap.put("server_"+Pub.KEY_SPEND,mapObject.get(Pub.KEY_SPEND));

                // TODO: just for debug
//                System.out.println("rand: " + esQueryBean.getRand()
//                        + "------ type: " + ((List<Map>)mapObject.get("resultData")).get(0).get("type"));

            }
        } catch (Exception e) {
            e.printStackTrace();
            outMap.put(Pub.KEY_RESULT,Pub.VAL_ERROR);
            outMap.put(Pub.KEY_RESULTDATA,null);
            outMap.put(Pub.KEY_MESSAGE,e.getMessage());
        } finally {
            long spend = System.currentTimeMillis()-start;
            outMap.put(Pub.KEY_SPEND,spend+"mm");
            return outMap;
        }

    }
}
