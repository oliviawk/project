package com.cn.hitec.service;

import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_web;
import com.cn.hitec.controller.BaseController;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.tools.Pub;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by Edward on 2017/12/14.
 * LAPS数据访问类
 *
 */
@Service
public class LAPSService extends BaseController{
    @Autowired
    EsQueryService esQueryService;


    /**
     * LAPS数据查询方法
     * @param esQueryBean
     * @return
     */
    public Map<String, Object> getData(EsQueryBean_web esQueryBean) {
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
                    String index = Pub.Index_Head + DateFormatUtils.format(new Date(), Pub.Index_Food_Simpledataformat);
                    esQueryBean.setIndices(new String[] { index });
                }

                Map<String,Object> params = new HashMap<>();    //查询参数

                // 加工时间为整点加40分的时间点，数据时间为当前小时的时间点
                /*
                if("LapsTRH".equals(esQueryBean.getSubType())) {

                }
                else if ("LapsTD".equals(esQueryBean.getSubType())) {

                }
                else if ("LapsRain1Hour".equals(esQueryBean.getSubType())) {

                }
                else if ("LapsWSWD".equals(esQueryBean.getSubType())) {

                }
                else {

                }
*/

                // 不区分过程和类型直接查
                Date d1 = DateUtils.truncate(new Date(), Calendar.HOUR);
                String dateStr = DateFormatUtils.format(d1, "yyyy-MM-dd HH:mm:ss.SSSZ");
                // TODO: just for debug
//                System.out.print("rand: " + esQueryBean.getRand() + " type: " + esQueryBean.getSubType());
//                System.out.println(">>>>>>>datestr " + dateStr);


                // 构造查询参数
//                params.put("type",esQueryBean.getSubType());
//                params.put("fields.module",esQueryBean.getModule());
//                params.put("fields.ip_addr",esQueryBean.getStrIp());

                Map<String, Object> mustMap = new HashMap<>();
                mustMap.put("type",esQueryBean.getSubType());
                mustMap.put("fields.module",esQueryBean.getModule());
                params.put("must", mustMap);

                List<Map> rangeList = new ArrayList<>();
                Map<String,String> map = new HashMap<>();
                map.put("name","fields.data_time");
                map.put("lte", dateStr);
                rangeList.add(map);
                params.put("range",rangeList);

                params.put("size",4);   // 分发分内外网可能有2条未处理数据
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


    /**
     * LAPS历史数据查询方法
     * @param esQueryBean
     * @return
     */
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
                    String index = Pub.Index_Head + DateFormatUtils.format(new Date(), Pub.Index_Food_Simpledataformat);
                    esQueryBean.setIndices(new String[] { index });
                }

                Map<String,Object> params = new HashMap<>();    //查询参数

                // 不区分过程和类型直接查
                Date d1 = DateUtils.truncate(new Date(), Calendar.HOUR);
                String dateStr = DateFormatUtils.format(d1, "yyyy-MM-dd HH:mm:ss.SSSZ");

//              // T639数据较少，查询范围扩大到6天
                if ("T639".equals(esQueryBean.getSubType())) {
                    String indexArr[] = new String[6];
                    for (int i=0; i<6; i++) {
                        Date d2 = DateUtils.addDays(d1, -i);
                        String d2Str = DateFormatUtils.format(d2, "yyyy-MM-dd HH:mm:ss.SSSZ");
                        System.out.println("date2Str: " + d2Str);
                        String d2Idx = Pub.Index_Head + DateFormatUtils.format(d2, Pub.Index_Food_Simpledataformat);
                        indexArr[i] = d2Idx;
                    }
                    esQueryBean.setIndices(indexArr);
                }

                // TODO: just for debug
//                System.out.print("rand: " + esQueryBean.getRand() + " type: " + esQueryBean.getSubType());
//                System.out.println(">>>>>>>datestr " + dateStr);


                // 构造查询参数
//                params.put("type",esQueryBean.getSubType());
//                params.put("fields.module",esQueryBean.getModule());
//                params.put("fields.ip_addr",esQueryBean.getStrIp());

                Map<String, Object> mustMap = new HashMap<>();
                mustMap.put("type",esQueryBean.getSubType());
                mustMap.put("fields.module",esQueryBean.getModule());

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


}
