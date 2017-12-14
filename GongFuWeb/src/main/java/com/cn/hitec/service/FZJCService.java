package com.cn.hitec.service;
import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_Exsit;
import com.cn.hitec.bean.EsQueryBean_web;
import com.cn.hitec.controller.BaseController;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.tools.CronPub;
import com.cn.hitec.tools.Pub;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.*;

/**
 * 
 * 
 * @description: FZJC数据查询主类
 * @author fukl
 * @since 2017年8月27日 下午2:59:06 
 * @version 
 *
 */
@Slf4j
@Service
public class FZJCService extends BaseController{
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
            log.error(e.getMessage());
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
            log.error(e.getMessage());
        } finally {
            long spend = System.currentTimeMillis()-start;
            outMap.put(KEY_SPEND,spend+"mm");
            return outMap;
        }
    }


    /**
     * 同意查询实时信息 ，已做到最少查询次数
     * @param esQueryBean  需要 setTypes 和 setIndices，为空的话会默认查询今天的数据
     * @return
     */
    public Map<String,Object> findDataNew(EsQueryBean_web esQueryBean){
        long start = System.currentTimeMillis();
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

                long st = System.currentTimeMillis();
//                雷达加工没有时次， type ：LatLonQREFEnd
//                云图、雷达、炎热指数的所有type："雷达","云图", "炎热指数","ReadFY2NC"
//                T639 单独查询 ， type：风流场、T639
                Map<String,Map> typeMap = new HashMap<>();

                if("regular".equals(esQueryBean.getFindType())){        //有规律的数据
                    Calendar calendar = Calendar.getInstance();
                    Date startDate = new Date();
                    calendar.setTime(startDate);
                    calendar.add(Calendar.HOUR_OF_DAY,-2);
                    Date endDate = calendar.getTime();
                /*------开始拼接查询参数--------*/
                    Map<String,Object> mapObject = null;
                    Map<String,Object> params = new HashMap<>();    //查询参数
                    Map<String,Object> mustMap = new HashMap<>();
                    mustMap.put("type",new String[]{"雷达","云图","炎热指数","ReadFY2NC"});
                    mustMap.put("fields.ip_addr",new String[]{"10.30.16.220","10.30.16.223","10.0.74.226"});
                    params.put("must",mustMap);

                    List<Map> rangeList = new ArrayList<>();
                    Map<String,String> rangeMap = new HashMap<>();
                    rangeMap.put("name","last_time");
                    rangeMap.put("gte", Pub.transform_DateToString(calendar.getTime(),"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    rangeMap.put("lte", Pub.transform_DateToString(startDate,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    rangeList.add(rangeMap);
                    params.put("range",rangeList);

                    params.put("sort","fields.data_time");
                    params.put("size","50");


                    EsQueryBean esQuery = new EsQueryBean();
                    esQuery.setIndices(esQueryBean.getIndices());
                    esQuery.setTypes(esQueryBean.getTypes());
                    esQuery.setParameters(params);

//                    System.out.println(com.alibaba.fastjson.JSON.toJSONString(esQuery));
                    //查询 获取数据
                    mapObject = esQueryService.getData_new(esQuery);

                    List<Object> data = (List<Object>) mapObject.get("resultData");
                    //设置各类数据赋值的最大次数
                    Map<String,Integer> numMap = new HashMap<>();
                    numMap.put("雷达_采集",5);
                    numMap.put("雷达_分发",5);
                    numMap.put("云图_采集",2);
                    numMap.put("ReadFY2NC_加工",2);
                    numMap.put("云图_分发",2);
                    numMap.put("炎热指数_加工",2);
                    numMap.put("炎热指数_分发",2);


                    for (Object obj : data){
                        Map<String,Object> dataMap = (Map<String,Object>) obj;
                        String strType = dataMap.get("type").toString();
                        String agingStatus = dataMap.get("aging_status").toString();
                        Map<String,Object> filedsMap =(Map<String, Object>) dataMap.get("fields");
                        String strModule = filedsMap.get("module").toString();

                        String strType_key = strType+"_"+strModule;
                        int num = numMap.containsKey(strType_key) ? numMap.get(strType_key) : 0;
                        if(num >= 1){
                            if(!StringUtils.isEmpty(agingStatus) && !agingStatus.equals("未处理")){
                                num = 0;
                            }else{
                                num--;
                            }
                            typeMap.put(strType_key,dataMap);
                        }
                        numMap.put(strType_key,num);

                    }


                }else if("no_regular".equals(esQueryBean.getFindType())){   //没规律的数据
                    String[] subTypes = new String[]{"LAPS3KM","城市预报","台风","预警信号","突发事件","船舶","交通拥堵","LatLonQREFEnd"};
                    for (int i = 0; i < subTypes.length ; i++){
                        Map<String,Object> params = new HashMap<>();    //查询参数
                        String subType = subTypes[i];
                        Map<String,Object> mustMap = new HashMap<>();
                        mustMap.put("type",subType);
                        if(subType.equals("LatLonQREFEnd")){
                            mustMap.put("fields.ip_addr","10.30.16.223");
                        }else {
                            mustMap.put("fields.ip_addr","120.26.9.109");
                        }
                        params.put("must",mustMap);
                        params.put("sort","fields.data_time");
                        params.put("size","1");

                        EsQueryBean esQuery = new EsQueryBean();
                        esQuery.setIndices(esQueryBean.getIndices());
                        esQuery.setTypes(esQueryBean.getTypes());
                        esQuery.setParameters(params);

//                        System.out.println(com.alibaba.fastjson.JSON.toJSONString(esQuery));
                        //查询 获取数据
                        Map<String,Object>  mapObject = esQueryService.getData_new(esQuery);
                        List<Object> data = (List<Object>) mapObject.get("resultData");
                        if(data == null  || data.size() < 1){
                            continue;
                        }
                        Map<String,Object> dataMap = (Map<String,Object>) data.get(0);
                        String strType = dataMap.get("type").toString();
                        Map<String,Object> filedsMap =(Map<String, Object>) dataMap.get("fields");
                        String strModule = filedsMap.get("module").toString();
                        String strType_key = strType+"_"+strModule;

                        typeMap.put(strType_key,dataMap);
                    }

                }else if("T639".equals(esQueryBean.getFindType())){         //T639单独数据
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

                    //获取6天的index
                    List<Date> timeList = CronPub.getTimeBycron_Date("0 0 2/3 * * ? *",startDate,endDate);
                    List<String> indicesList = new ArrayList<>();
                    for (Date dt : timeList){
                        String indexKey = Pub.Index_Head+Pub.transform_DateToString(dt,Pub.Index_Food_Simpledataformat);
                        if(indicesList.contains(indexKey)){
                            continue;
                        }
                        indicesList.add(indexKey);
                    }
                    String[] indices = new String[indicesList.size()];
                    indicesList.toArray(indices);
                    esQueryBean.setIndices(indices);    //重新赋值indices

                    String[] subTypes = new String[]{"风流场","T639"};
                    Map<String,Object> params = new HashMap<>();    //查询参数
                    Map<String,Object> mustMap = new HashMap<>();
                    mustMap.put("type",subTypes);
                    mustMap.put("fields.ip_addr",new String[]{"10.30.16.223","10.0.74.226"});
                    params.put("must",mustMap);

                    params.put("sort","fields.data_time");
                    params.put("sortType","asc");
                    params.put("size","1");

                    List<Map> list = new ArrayList<>();
                    Map<String,String> map = new HashMap<>();
                    map.put("name","fields.data_time");
                    map.put("gt", Pub.transform_DateToString(startDate,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    map.put("lt", Pub.transform_DateToString(endDate,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    list.add(map);
                    params.put("range",list);

                    params.put("size","50");
                    EsQueryBean esQuery = new EsQueryBean();
                    esQuery.setIndices(esQueryBean.getIndices());
                    esQuery.setTypes(esQueryBean.getTypes());
                    esQuery.setParameters(params);

                    //查询 获取数据
                    Map<String,Object> mapObject = esQueryService.getData_new(esQuery);
                    List<Object> listMap = (List<Object>)mapObject.get("resultData");

                    boolean isJG_ok  = true , isFF_ok = true;
                    Map<String,Object> firstJG = null;
                    Map<String,Object> firstFF = null;
                    for (Object obj : listMap){
                        Map<String,Object> dataMap = (Map<String,Object>) obj;
                        String strType = dataMap.get("type").toString();
                        String agingStatus = dataMap.containsKey("aging_status") ? dataMap.get("aging_status").toString() : "";
                        Map<String,Object> filedsMap =(Map<String, Object>) dataMap.get("fields");
                        String strModule = filedsMap.get("module").toString();

                        String strType_key = strType+"_"+strModule;
                        if("加工".equals(strModule) && firstJG == null){
                            firstJG = dataMap;
                        }else if("分发".equals(strModule) && firstFF == null){
                            firstFF = dataMap;
                        }
                        if(typeMap.containsKey(strType_key)){
                            continue;
                        }

                        if("异常".equals(agingStatus)){
                            if("加工".equals(strModule)){
                                isJG_ok = false;
                                typeMap.put(strType_key,dataMap);
                            }else if("分发".equals(strModule)){
                                isFF_ok = false;
                                typeMap.put(strType_key,dataMap);
                            }
                        }

                    }

                    if (isJG_ok){
                        typeMap.put("风流场_加工",firstJG);
                    }
                    if (isFF_ok){
                        typeMap.put("T639_分发",firstFF);
                    }

                }


                //拼接返回的map
                outMap.put(KEY_RESULT,VAL_SUCCESS);
                outMap.put(KEY_MESSAGE,"成功");
                outMap.put(KEY_RESULTDATA,typeMap);
                outMap.put("server_"+KEY_SPEND,(System.currentTimeMillis() - start)+"ms");
                log.info(esQueryBean.getFindType()+"数据耗时："+(System.currentTimeMillis()-st));
            }
        } catch (Exception e) {
            outMap.put(KEY_RESULT,VAL_ERROR);
            outMap.put(KEY_RESULTDATA,null);
            outMap.put(KEY_MESSAGE,e.getMessage());
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            long spend = System.currentTimeMillis()-start;
            outMap.put(KEY_SPEND,spend+"ms");
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
            e.printStackTrace();
            log.error(e.getMessage());
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
