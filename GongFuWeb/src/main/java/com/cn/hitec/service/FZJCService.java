package com.cn.hitec.service;

import com.cn.hitec.bean.D3NetBean;
import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_Exsit;
import com.cn.hitec.bean.EsQueryBean_web;
import com.cn.hitec.controller.BaseController;
import com.cn.hitec.controller.FZJCController;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.tools.CronPub;
import com.cn.hitec.tools.DateTool;
import com.cn.hitec.tools.Pub;
import kafka.tools.ConsoleConsumer;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.misc.resources.Messages_pt_BR;

import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * 
 * 
 * @description: FZJC数据查询主类
 * @author fukl
 * @since 2017年8月27日 下午2:59:06 
 * @version 
 *
 */
@Service
public class FZJCService extends BaseController{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FZJCService.class);
	@Autowired
	EsQueryService esQueryService;

	public Map<String,Object> findData_FC(EsQueryBean_web esQueryBean){
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
                    String[] indices = Pub.getIndices(new Date(),1);
                    esQueryBean.setIndices(indices);
                }

                Map<String,Object> params = new HashMap<>();    //查询参数


                // 获取 24 分钟后的时间 （4个时次后的时间）
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                calendar.setTime(date);
                calendar.add(Calendar.MINUTE, -Integer.valueOf(esQueryBean.getMinute_before()));

                List<Map> list = new ArrayList<>();
                Map<String,String> map = new HashMap<>();
                map.put("name","fields.end_time");
                map.put("lte", Pub.transform_DateToString(date, "yyyy-MM-dd HH:mm:ss.SSSZ"));
                map.put("gte", Pub.transform_DateToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss.SSSZ"));
                list.add(map);
                params.put("range",list);

                Map<String,Object> mustMap = new HashMap<>();
//                mustMap.put("type",esQueryBean.getSubType());
                mustMap.put("fields.module",esQueryBean.getModule());

//                Map<String,Object> mustNotMap = new HashMap<>();
//                mustNotMap.put("aging_status","未处理");

                params.put("must",mustMap);
//                params.put("mustNot",mustNotMap);
                params.put("sort","fields.end_time");
                params.put("size",esQueryBean.getSize());


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
     * 查询告警信息
     * @param esQueryBean
     * @return
     */
    public Map<String,Object> findAlertData(EsQueryBean_web esQueryBean){
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
                    String index = Pub.Index_Head + Pub.transform_DateToString(new Date(),Pub.Index_Food_Simpledataformat);
                    String index_yest = Pub.Index_Head + DateFormatUtils.format(DateUtils.addDays(new Date(), -1), Pub.Index_Food_Simpledataformat);
                    esQueryBean.setIndices(new String[]{index, index_yest});
                }

                Map<String,Object> params = new HashMap<>();    //查询参数

//                params.put("mustNot",mustNotMap);
                params.put("sort","time");
                params.put("size",esQueryBean.getSize());
                params.put("_id","true");


                EsQueryBean esQuery = new EsQueryBean();
                esQuery.setIndices(esQueryBean.getIndices());
                esQuery.setTypes(esQueryBean.getTypes());
                esQuery.setParameters(params);

                mapObject = esQueryService.getData_new(esQuery);

//                logger.info(com.alibaba.fastjson.JSON.toJSONString(esQuery));
//                logger.info(com.alibaba.fastjson.JSON.toJSONString(mapObject));
//                logger.info("--");
                //这里做一些数据字段 转换、过滤
                outMap.put(KEY_RESULT,mapObject.get(KEY_RESULT));
                outMap.put(KEY_MESSAGE,mapObject.get(KEY_MESSAGE));
                outMap.put(KEY_RESULTDATA,mapObject.get("resultData"));
                outMap.put("server_"+KEY_SPEND,mapObject.get(KEY_SPEND));
            }
        } catch (Exception e) {
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
     * 云图、雷达， 实时数据查询
     * @param esQueryBean
     * @return
     */
	public Map<String,Object> findData_DI(EsQueryBean_web esQueryBean){
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
                    String index = Pub.Index_Head + Pub.transform_DateToString(new Date(),Pub.Index_Food_Simpledataformat);
                    esQueryBean.setIndices(new String[]{index});
                }

                Map<String,Object> params = new HashMap<>();    //查询参数
                Map<String,Object> DIMap  = new HashMap<>();    //配置信息列表
                if("采集".equals(esQueryBean.getModule())){
                    DIMap = (Map<String, Object>) Pub.alertMap_collect.get(esQueryBean.getSubType());
                }else if("加工".equals(esQueryBean.getModule())){
                    DIMap = (Map<String, Object>) Pub.alertMap_machining.get(esQueryBean.getSubType());
                }else if("分发".equals(esQueryBean.getModule())){
                    DIMap = (Map<String, Object>) Pub.alertMap_distribute.get(esQueryBean.getSubType());
                }
                /*------开始拼接查询参数--------*/
                //判断是风云系列数据
                if("ReadFY2NC".equals(esQueryBean.getSubType()) || "云图".equals(esQueryBean.getSubType()) || "炎热指数".equals(esQueryBean.getSubType())){
                    List<Map> list = new ArrayList<>();
                    Map<String,String> map = new HashMap<>();
                    map.put("name","last_time");
                    map.put("lt", CronPub.getOneTimeBycron_String(DIMap.get("time_interval").toString(),"yyyy-MM-dd HH:mm:ss.SSSZ",new Date()));
                    list.add(map);
                    params.put("range",list);
                    params.put("type",esQueryBean.getSubType());
                    params.put("fields.module",esQueryBean.getModule());
//                    params.put("fields.ip_addr",esQueryBean.getStrIp());
                    params.put("sort","last_time");
                    params.put("size","2");
                }else if("radarlatlon".equals(esQueryBean.getSubType()) || "雷达".equals(esQueryBean.getSubType())){        //判断是雷达系列数据
                    // 获取 24 分钟后的时间 （4个时次后的时间）
                    Calendar calendar = Calendar.getInstance();
                    Date date = new Date();
                    calendar.setTime(date);
                    calendar.add(Calendar.MINUTE,24);

                    List<Map> list = new ArrayList<>();
                    Map<String,String> map = new HashMap<>();
                    map.put("name","last_time");
                    map.put("lt", CronPub.getOneTimeBycron_String(DIMap.get("time_interval").toString(),"yyyy-MM-dd HH:mm:ss.SSSZ",calendar.getTime()));
                    list.add(map);
                    params.put("range",list);
                    params.put("type",esQueryBean.getSubType());
                    params.put("fields.module",esQueryBean.getModule());
//                    params.put("fields.ip_addr",esQueryBean.getStrIp());
                    params.put("sort","last_time");
                    params.put("size","5");
                }else if("风流场".equals(esQueryBean.getSubType()) || "T639".equals(esQueryBean.getSubType())){        //判断是雷达系列数据
                    Calendar calendar = Calendar.getInstance();
                    Date date = new Date();
                    calendar.setTime(date);
                    calendar.set(Calendar.MINUTE,0);
                    calendar.set(Calendar.SECOND,0);
                    calendar.set(Calendar.MILLISECOND , 0 );
                    Date startDate = calendar.getTime();

                    calendar.add(Calendar.DAY_OF_MONTH, 6);
                    calendar.set(Calendar.HOUR_OF_DAY,0);
                    Date endDate = calendar.getTime();

                    List<Date> timeList = CronPub.getTimeBycron_Date("0 0 2/3 * * ? *",startDate,endDate);
                    List<String> indicesList = new ArrayList<>();
                    List<String> temp = new ArrayList<>();
                    for (Date dt : timeList){
                        String indexKey = Pub.Index_Head+Pub.transform_DateToString(dt,Pub.Index_Food_Simpledataformat);
                        if(temp.contains(indexKey)){
                            continue;
                        }
                        if(isExist_DI_Data(indexKey,"FZJC",null)){
                            indicesList.add(indexKey);
                        }
                        temp.add(indexKey);
                    }
                    String[] indices = new String[indicesList.size()];
                    indicesList.toArray(indices);
                    esQueryBean.setIndices(indices);    //重新赋值indices

                    List<Map> list = new ArrayList<>();
                    Map<String,String> map = new HashMap<>();
                    map.put("name","fields.data_time");
                    map.put("gte", Pub.transform_DateToString(startDate,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    map.put("lt", Pub.transform_DateToString(endDate,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    list.add(map);
                    params.put("range",list);
                    params.put("type",esQueryBean.getSubType());
                    params.put("fields.module",esQueryBean.getModule());
                    params.put("size","100");
                }else /*if("LatLonQREFEnd".equals(esQueryBean.getSubType()))*/{
                    params.put("type",esQueryBean.getSubType());
                    params.put("fields.module",esQueryBean.getModule());
                    params.put("fields.ip_addr",esQueryBean.getStrIp());
                    params.put("sort","fields.data_time");  //注意这里，加工雷达没有时次
                    params.put("size","1");
                }

//                System.out.println(params);
                EsQueryBean esQuery = new EsQueryBean();
                esQuery.setIndices(esQueryBean.getIndices());
                esQuery.setTypes(esQueryBean.getTypes());
                esQuery.setParameters(params);

                //查询 获取数据
                mapObject = esQueryService.getData(esQuery);

//                logger.info(com.alibaba.fastjson.JSON.toJSONString(esQuery));
//                logger.info(com.alibaba.fastjson.JSON.toJSONString(mapObject));
//                logger.info("--");

                if(mapObject.get("result").equals("success") && mapObject.get("resultData") != null){
                    List<Object>  returnList = new ArrayList<>();
                    List<Object> listMap = (List<Object>)mapObject.get("resultData");

                    if("风流场".equals(esQueryBean.getSubType()) || "T639".equals(esQueryBean.getSubType())){        //判断是T639系列数据
                        for(int i = 0 ; i < listMap.size() ; i++){

                            Map<String,Object> mp = (Map<String, Object>) listMap.get(i);
                            Map<String ,Object> fieldMp = (Map<String ,Object>)mp.get("fields");
                            if(!fieldMp.containsKey("event_status") || !"正常".equals(mp.get("aging_status"))){
//                                System.out.println("1:"+com.alibaba.fastjson.JSON.toJSONString(mp));
//                                fieldMp.put("event_status","error");
                                returnList.add(mp);
                                break;
                            }
                            if(i == listMap.size()-1 && returnList.size() == 0){
//                                System.out.println("2:"+com.alibaba.fastjson.JSON.toJSONString(mp));
                                returnList.add(listMap.get(i));
                                break;
                            }
                        }
                    }else{
                        for(int i = 0 ; i < listMap.size() ; i++){

                            Map<String,Object> mp = (Map<String, Object>) listMap.get(i);
                            Map<String ,Object> fieldMp = (Map<String ,Object>)mp.get("fields");
                            if(fieldMp.containsKey("event_status")){
                                returnList.add(mp);
                                break;
                            }
                            if(i == listMap.size()-1 && returnList.size() == 0){
                                returnList.add(listMap.get(i));
                                break;
                            }

                        }
                    }


                    mapObject.put("resultData",returnList);
                }else{
                    mapObject.put("resultData","[]");
                }

                //拼接返回的map
                outMap.put(KEY_RESULT,mapObject.get(KEY_RESULT));
                outMap.put(KEY_MESSAGE,mapObject.get(KEY_MESSAGE));
                outMap.put(KEY_RESULTDATA,mapObject.get("resultData"));
                outMap.put("server_"+KEY_SPEND,mapObject.get(KEY_SPEND));

            }
        } catch (Exception e) {
            outMap.put(KEY_RESULT,VAL_ERROR);
            outMap.put(KEY_RESULTDATA,null);
            outMap.put(KEY_MESSAGE,e.getMessage());
            e.printStackTrace();
        } finally {
            long spend = System.currentTimeMillis()-start;
            outMap.put(KEY_SPEND,spend+"mm");
            return outMap;
        }
    }


    /**
     * 云图、雷达， 实时数据查询
     * @param esQueryBean
     * @return
     */
    public Map<String,Object> findData_DI_new(EsQueryBean_web esQueryBean){
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
                    String index = Pub.Index_Head + Pub.transform_DateToString(new Date(),Pub.Index_Food_Simpledataformat);
                    esQueryBean.setIndices(new String[]{index});
                }

                Calendar calendar = Calendar.getInstance();
                Date startDate = new Date();
                calendar.setTime(startDate);
                calendar.add(Calendar.HOUR_OF_DAY,-2);
                Date endDate = calendar.getTime();

                Map<String,Object> params = new HashMap<>();    //查询参数
                /*------开始拼接查询参数--------*/
//                雷达加工没有时次， type ：LatLonQREFEnd
//                云图、雷达、炎热指数的所有type："雷达","云图", "炎热指数","ReadFY2NC"
//                T639 单独查询 ， type：风流场、T639
                Map<String,Object> mustMap = new HashMap<>();
                mustMap.put("type",new String[]{"雷达","云图","炎热指数","ReadFY2NC"});
                mustMap.put("fields.ip_addr",new String[]{"10.30.16.220","10.30.16.223","10.0.74.236"});
                params.put("must",mustMap);

                List<Map> rangeList = new ArrayList<>();
                Map<String,String> rangeMap = new HashMap<>();
                rangeMap.put("name","last_time");
                rangeMap.put("gte", Pub.transform_DateToString(startDate,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                rangeMap.put("lte", Pub.transform_DateToString(calendar.getTime(),"yyyy-MM-dd HH:mm:ss.SSSZ"));
                rangeList.add(rangeMap);
                params.put("range",rangeList);

                params.put("sort","last_time");
                params.put("size","50");

                //拼接返回的map
                outMap.put(KEY_RESULT,mapObject.get(KEY_RESULT));
                outMap.put(KEY_MESSAGE,mapObject.get(KEY_MESSAGE));
                outMap.put(KEY_RESULTDATA,mapObject.get("resultData"));
                outMap.put("server_"+KEY_SPEND,mapObject.get(KEY_SPEND));

            }
        } catch (Exception e) {
            outMap.put(KEY_RESULT,VAL_ERROR);
            outMap.put(KEY_RESULTDATA,null);
            outMap.put(KEY_MESSAGE,e.getMessage());
            e.printStackTrace();
        } finally {
            long spend = System.currentTimeMillis()-start;
            outMap.put(KEY_SPEND,spend+"mm");
            return outMap;
        }
    }


    /**
     * 历史数据查询
     * @param esQueryBean
     * @return
     */
    public Map<String,Object> findData_DI_history(EsQueryBean_web esQueryBean){
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
//                    String index = "log_"+Pub.transform_DateToString(new Date() , "yyyyMMdd");
                    String[] indices = Pub.getIndices(new Date(),2);
                    esQueryBean.setIndices(indices);
                }

                Map<String,Object> params = new HashMap<>();    //查询参数
                /*------开始拼接查询参数--------*/
                //判断是 预先生成的数据
                if( "ReadFY2NC".equals(esQueryBean.getSubType()) || "云图".equals(esQueryBean.getSubType()) || "雷达".equals(esQueryBean.getSubType())
                            || "炎热指数".equals(esQueryBean.getSubType())){
                    Map<String,Object> mustMap = new HashMap<>();
                    mustMap.put("type",esQueryBean.getSubType());
                    mustMap.put("fields.module",esQueryBean.getModule());

                    Map<String,Object> mustNotMap = new HashMap<>();
                    mustNotMap.put("aging_status","未处理");

                    params.put("must",mustMap);
                    params.put("mustNot",mustNotMap);
                    params.put("sort","last_time");
                    params.put("size",esQueryBean.getSize());

                }else if("风流场".equals(esQueryBean.getSubType()) || "T639".equals(esQueryBean.getSubType())){        //判断是雷达系列数据
                    Calendar calendar = Calendar.getInstance();
                    Date date = new Date();
                    calendar.setTime(date);
                    calendar.set(Calendar.MINUTE,0);
                    calendar.set(Calendar.SECOND,0);
                    calendar.set(Calendar.MILLISECOND , 0 );
                    Date startDate = calendar.getTime();

                    calendar.add(Calendar.DAY_OF_MONTH, 6);
                    calendar.set(Calendar.HOUR_OF_DAY,0);
                    Date endDate = calendar.getTime();

                    List<Date> timeList = CronPub.getTimeBycron_Date("0 0 2/3 * * ? *",startDate,endDate);
                    List<String> indicesList = new ArrayList<>();
                    List<String> temp = new ArrayList<>();
                    for (Date dt : timeList){
                        String indexKey = Pub.Index_Head+Pub.transform_DateToString(dt,Pub.Index_Food_Simpledataformat);
                        if(temp.contains(indexKey)){
                            continue;
                        }
                        if(isExist_DI_Data(indexKey,"FZJC",null)){
                            indicesList.add(indexKey);
                        }
                        temp.add(indexKey);
                    }
                    String[] indices = new String[indicesList.size()];
                    indicesList.toArray(indices);
                    esQueryBean.setIndices(indices);    //重新赋值indices

                    List<Map> list = new ArrayList<>();
                    Map<String,String> map = new HashMap<>();
                    map.put("name","fields.data_time");
                    map.put("gte", Pub.transform_DateToString(startDate,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    map.put("lt", Pub.transform_DateToString(endDate,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    list.add(map);

                    Map<String,Object> mustMap = new HashMap<>();
                    mustMap.put("type",esQueryBean.getSubType());
                    mustMap.put("fields.module",esQueryBean.getModule());

                    params.put("must",mustMap);
                    params.put("range",list);
                    params.put("sort","fields.data_time");
                    params.put("size","100");
                }else /*if("LatLonQREFEnd".equals(esQueryBean.getSubType()))*/{
                    Map<String,Object> mustMap = new HashMap<>();
                    mustMap.put("type",esQueryBean.getSubType());
                    mustMap.put("fields.module",esQueryBean.getModule());

                    params.put("must",mustMap);
                    params.put("sort","fields.data_time");  //注意这里，加工雷达没有时次
                    params.put("size",esQueryBean.getSize());
                }


                EsQueryBean esQuery = new EsQueryBean();
                esQuery.setIndices(esQueryBean.getIndices());
                esQuery.setTypes(esQueryBean.getTypes());
                esQuery.setParameters(params);
//                System.out.println(params);
//                System.out.println(esQuery.toString());

                //查询 获取数据
                mapObject = esQueryService.getData_new(esQuery);
                //拼接返回的map
                outMap.put(KEY_RESULT,mapObject.get(KEY_RESULT));
                outMap.put(KEY_MESSAGE,mapObject.get(KEY_MESSAGE));
                outMap.put(KEY_RESULTDATA,mapObject.get("resultData"));
                outMap.put("server_"+KEY_SPEND,mapObject.get(KEY_SPEND));
            }
        } catch (Exception e) {
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
     * 判断是否有 预生成数据
     * @param index
     * @param type
     * @param subType
     * @return
     * @throws Exception
     */
    public boolean isExist_DI_Data(String index,String type,String subType) throws Exception{
        boolean flag = false;

        EsQueryBean_Exsit esQueryBean_exsit = new EsQueryBean_Exsit();
        esQueryBean_exsit.setIndex(index);
        esQueryBean_exsit.setType(type);
        esQueryBean_exsit.setSubType(subType);
        Map<String,Object> resultMap = esQueryService.indexIsExist(esQueryBean_exsit);
        if(!"success".equals(resultMap.get("result"))){
            throw new Exception("查询发生了错误,错误信息:"+resultMap.get("message"));
        }
        flag = (Boolean) resultMap.get("resultData");

        return flag;
    }

}
