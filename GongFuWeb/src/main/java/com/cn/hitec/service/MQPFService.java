package com.cn.hitec.service;

import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_web;
import com.cn.hitec.controller.BaseController;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.tools.Pub;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class MQPFService extends BaseController {

    @Autowired
    EsQueryService esQueryService;

    public Map<String,Object> MQPFAggQuery(){
        long start = System.currentTimeMillis();
        try {
            long st = System.currentTimeMillis();

            EsQueryBean_web esQueryBean = new EsQueryBean_web();
            String[] index = Pub.getIndices(new Date(),2);
            esQueryBean.setIndices(index);
            esQueryBean.setTypes(new String[]{"MQPF"});

            Map params = new HashMap();
            params.put("subTypes","MQPF_NC1H,MQPF_PNG5M,MQPF_NC5M");
            esQueryBean.setParameters(params);
            outMap = esQueryService.lctAggQuery(esQueryBean);

            outMap.put("server_"+KEY_SPEND,(System.currentTimeMillis() - start)+"ms");
        } catch (Exception e) {
            outMap.put(KEY_RESULT,VAL_ERROR);
            outMap.put(KEY_RESULTDATA,null);
            outMap.put(KEY_MESSAGE,e.getMessage());
            e.printStackTrace();
        } finally {
            long spend = System.currentTimeMillis()-start;
            outMap.put(KEY_SPEND,spend+"ms");
            return outMap;
        }
    }


    public Map<String, Object> getHistory(EsQueryBean_web esQueryBean) {
        long start = System.currentTimeMillis();
        Map<String,Object> mapObject = null;
        try {
            //判断参数是否正确
            if(esQueryBean == null){
                outMap.put(KEY_RESULT,VAL_ERROR);
                outMap.put(KEY_RESULTDATA,null);
                outMap.put(KEY_MESSAGE,"参数错误！");
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
                Date d1 = DateUtils.truncate(new Date(), Calendar.MINUTE);
                String dateStr = DateFormatUtils.format(d1, "yyyy-MM-dd HH:mm:ss.SSSZ");


                Map<String, Object> mustMap = new HashMap<>();
                mustMap.put("type",esQueryBean.getSubType());
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
                outMap.put(KEY_RESULT,mapObject.get(KEY_RESULT));
                outMap.put(KEY_MESSAGE,mapObject.get(KEY_MESSAGE));
                outMap.put(KEY_RESULTDATA,mapObject.get("resultData"));
                outMap.put("server_"+KEY_SPEND,mapObject.get(KEY_SPEND));

                // TODO: just for debug
//                System.out.println("rand: " + esQueryBean.getRand()
//                        + "------ type: " + ((List<Map>)mapObject.get("resultData")).get(0).get("type"));

            }
        } catch (Exception e) {
            e.printStackTrace();
            outMap.put(KEY_RESULT,VAL_ERROR);
            outMap.put(KEY_RESULTDATA,null);
            outMap.put(KEY_MESSAGE,e.getMessage());
        } finally {
            long spend = System.currentTimeMillis()-start;
            outMap.put(KEY_SPEND,spend+"mm");
            return outMap;
        }

    }


    public Map<String,Object> findDataByQuery(EsQueryBean_web esQueryBean){
        long start = System.currentTimeMillis();
        Map<String,Object> mapObject = null;
        try {
            //判断参数是否正确
            if(esQueryBean == null){
                outMap.put(KEY_RESULT,VAL_ERROR);
                outMap.put(KEY_RESULTDATA,null);
                outMap.put(KEY_MESSAGE,"参数错误！");
            }else{
                if(StringUtils.isEmpty(esQueryBean.getIndices())){
                    // 查询今天和昨天的记录
                    String[] index = Pub.getIndices(new Date(),2);
                    esQueryBean.setIndices(index);

                }

                Map<String,Object> params = new HashMap<>();    //查询参数

                // 不区分过程和类型直接查
                Date d1 = DateUtils.truncate(new Date(), Calendar.MINUTE);
                String dateStr = DateFormatUtils.format(d1, "yyyy-MM-dd HH:mm:ss.SSSZ");

                if (!StringUtils.isEmpty(esQueryBean.getSubType())){
                    Map<String,Object> wildcardMap = new HashMap<>();
                    wildcardMap.put("type","*"+esQueryBean.getSubType().toUpperCase()+"*");
                    params.put("wildcard",wildcardMap);
                }

                Map<String, Object> mustMap = new HashMap<>();
                mustMap.put("name","雷达基数据");
                mustMap.put("fields.module",esQueryBean.getModule());
                mustMap.put("fields.ip_addr",esQueryBean.getStrIp());
                params.put("must", mustMap);

                if (!StringUtils.isEmpty(esQueryBean.getStatus())){
                    String status = esQueryBean.getStatus();
                    if ("正常".equals(status)){
                        mustMap.put("aging_status","正常");
                    }else if("异常".equals(status)){
                        mustMap.put("aging_status", new String[]{"异常","超时"});
                    }else if("全部".equals(status)){
                        mustMap.put("aging_status", new String[]{"正常","异常","超时"});
                    }
                }else {
                    Map<String, Object> mustNotMap = new HashMap<>();
                    mustNotMap.put("aging_status", "未处理");
                    params.put("mustNot", mustNotMap);
                }


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

                mapObject = esQueryService.findDataByQuery(esQuery);
                //这里做一些数据字段 转换、过滤
                outMap.put(KEY_RESULT,mapObject.get(KEY_RESULT));
                outMap.put(KEY_MESSAGE,mapObject.get(KEY_MESSAGE));
                outMap.put(KEY_RESULTDATA,mapObject.get("resultData"));
                outMap.put("server_"+KEY_SPEND,mapObject.get(KEY_SPEND));

                // TODO: just for debug
//                System.out.println("rand: " + esQueryBean.getRand()
//                        + "------ type: " + ((List<Map>)mapObject.get("resultData")).get(0).get("type"));

            }
        } catch (Exception e) {
            e.printStackTrace();
            outMap.put(KEY_RESULT,VAL_ERROR);
            outMap.put(KEY_RESULTDATA,null);
            outMap.put(KEY_MESSAGE,e.getMessage());
        } finally {
            long spend = System.currentTimeMillis()-start;
            outMap.put(KEY_SPEND,spend+"mm");
            return outMap;
        }

    }
}
