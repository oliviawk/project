package com.cn.hitec.service.impl;

import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_web;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.service.BaseServiceInterface;
import com.cn.hitec.tools.Pub;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @description: 描述信息
 * @author: fukl
 * @data: 2018年07月23日 上午11:39
 */
public class BaseService implements BaseServiceInterface{

    @Autowired
    private EsQueryService esQueryService;

    @Override
    public Map<String, Object> aggQuery(String [] types, String subType) {
        Map outMap = new HashMap();
        long start = System.currentTimeMillis();
        try {
            long st = System.currentTimeMillis();

            EsQueryBean_web esQueryBean = new EsQueryBean_web();
            String[] index = Pub.getIndices(new Date(),2);
            esQueryBean.setIndices(index);
            esQueryBean.setTypes(types);

            Map params = new HashMap();
//            "MQPF_NC1H,MQPF_PNG5M,MQPF_NC5M"
            params.put("subTypes",subType);
            esQueryBean.setParameters(params);
            outMap = esQueryService.lctAggQuery(esQueryBean);

            outMap.put("server_"+Pub.KEY_SPEND,(System.currentTimeMillis() - start)+"ms");
        } catch (Exception e) {
            outMap.put(Pub.KEY_RESULT,Pub.VAL_ERROR);
            outMap.put(Pub.KEY_RESULTDATA,null);
            outMap.put(Pub.KEY_MESSAGE,e.getMessage());
            e.printStackTrace();
        } finally {
            long spend = System.currentTimeMillis()-start;
            outMap.put(Pub.KEY_SPEND,spend+"ms");
            return outMap;
        }
    }

//    @Override
//    public Map<String, Object> getHistory(EsQueryBean_web esQueryBean) {
//        long start = System.currentTimeMillis();
//        Map<String,Object> mapObject = null;
//        Map outMap = new HashMap();
//        try {
//            //判断参数是否正确
//            if(esQueryBean == null){
//                outMap.put(Pub.KEY_RESULT,Pub.VAL_ERROR);
//                outMap.put(Pub.KEY_RESULTDATA,null);
//                outMap.put(Pub.KEY_MESSAGE,"参数错误！");
//            }else{
//                if(StringUtils.isEmpty(esQueryBean.getIndices())){
//                    // 查询今天和昨天的记录
//                    String index = Pub.Index_Head + DateFormatUtils.format(new Date(), Pub.Index_Food_Simpledataformat);
//                    Date d2 = DateUtils.addDays(new Date(), -1);    // 昨天的日期
//                    String index2 = Pub.Index_Head + DateFormatUtils.format(d2, Pub.Index_Food_Simpledataformat);
//                    esQueryBean.setIndices(new String[] { index, index2 });
//                }
//
//                Map<String,Object> params = new HashMap<>();    //查询参数
//
//                // 不区分过程和类型直接查
//                Date d1 = DateUtils.truncate(new Date(), Calendar.HOUR);
//                String dateStr = DateFormatUtils.format(d1, "yyyy-MM-dd HH:mm:ss.SSSZ");
//
////              // T639数据较少，查询范围扩大到6天
//                if ("T639".equals(esQueryBean.getSubType())) {
//                    String indexArr[] = new String[6];
//                    for (int i=0; i<6; i++) {
//                        Date d2 = DateUtils.addDays(d1, -i);
//                        String d2Str = DateFormatUtils.format(d2, "yyyy-MM-dd HH:mm:ss.SSSZ");
//                        System.out.println("date2Str: " + d2Str);
//                        String d2Idx = Pub.Index_Head + DateFormatUtils.format(d2, Pub.Index_Food_Simpledataformat);
//                        indexArr[i] = d2Idx;
//                    }
//                    esQueryBean.setIndices(indexArr);
//                }
//
//
//                Map<String, Object> mustMap = new HashMap<>();
//                mustMap.put("type",esQueryBean.getSubType());
//                mustMap.put("fields.module",esQueryBean.getModule());
//                mustMap.put("fields.ip_addr",esQueryBean.getStrIp());
//
//                Map<String, Object> mustNotMap = new HashMap<>();
//                mustNotMap.put("aging_status", "未处理");
//
//                params.put("must", mustMap);
//                params.put("mustNot", mustNotMap);
//
//                List<Map> rangeList = new ArrayList<>();
//                Map<String,String> map = new HashMap<>();
//                map.put("name","fields.data_time");
//                map.put("lte", dateStr);
//                rangeList.add(map);
//                params.put("range",rangeList);
//
//                params.put("size",esQueryBean.getSize());
//                params.put("sort","fields.data_time");
//
//
//                // 去做查询
//                EsQueryBean esQuery = new EsQueryBean();
//                esQuery.setIndices(esQueryBean.getIndices());
//                esQuery.setTypes(esQueryBean.getTypes());
//                esQuery.setParameters(params);
//
//                mapObject = esQueryService.getData_new(esQuery);
//                //这里做一些数据字段 转换、过滤
//                outMap.put(Pub.KEY_RESULT,mapObject.get(Pub.KEY_RESULT));
//                outMap.put(Pub.KEY_MESSAGE,mapObject.get(Pub.KEY_MESSAGE));
//                outMap.put(Pub.KEY_RESULTDATA,mapObject.get("resultData"));
//                outMap.put("server_"+Pub.KEY_SPEND,mapObject.get(Pub.KEY_SPEND));
//
//                // TODO: just for debug
////                System.out.println("rand: " + esQueryBean.getRand()
////                        + "------ type: " + ((List<Map>)mapObject.get("resultData")).get(0).get("type"));
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            outMap.put(Pub.KEY_RESULT,Pub.VAL_ERROR);
//            outMap.put(Pub.KEY_RESULTDATA,null);
//            outMap.put(Pub.KEY_MESSAGE,e.getMessage());
//        } finally {
//            long spend = System.currentTimeMillis()-start;
//            outMap.put(Pub.KEY_SPEND,spend+"mm");
//            return outMap;
//        }
//    }
}
