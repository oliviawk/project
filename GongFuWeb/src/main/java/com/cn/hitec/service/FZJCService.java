package com.cn.hitec.service;

import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_web;
import com.cn.hitec.controller.BaseController;
import com.cn.hitec.controller.FZJCController;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.tools.CronPub;
import com.cn.hitec.tools.Pub;
import kafka.tools.ConsoleConsumer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.misc.resources.Messages_pt_BR;

import java.security.Key;
import java.util.*;
import java.util.logging.Logger;

/**
 * 
 * 
 * @description: TODO(这里用一句话描述这个类的作用) 
 * @author james
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
                map.put("name","fields.end_time.keyword");
                map.put("lte", Pub.transform_DateToString(date, "yyyy-MM-dd HH:mm:ss.SSSZ"));
                map.put("gte", Pub.transform_DateToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss.SSSZ"));
                list.add(map);
                params.put("range",list);

                Map<String,Object> mustMap = new HashMap<>();
//                mustMap.put("type.keyword",esQueryBean.getSubType());
                mustMap.put("fields.module.keyword",esQueryBean.getModule());

//                Map<String,Object> mustNotMap = new HashMap<>();
//                mustNotMap.put("aging_status.keyword","未处理");

                params.put("must",mustMap);
//                params.put("mustNot",mustNotMap);
                params.put("sort","fields.end_time.keyword");
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
                    String index = "log_"+Pub.transform_DateToString(new Date() , "yyyyMMdd");
                    esQueryBean.setIndices(new String[]{index});
                }

                Map<String,Object> params = new HashMap<>();    //查询参数

//                params.put("mustNot",mustNotMap);
                params.put("sort","time.keyword");
                params.put("size",esQueryBean.getSize());
                params.put("_id","true");


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
                    String index = "log_"+Pub.transform_DateToString(new Date() , "yyyyMMdd");
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
                if("ReadFY2NC".equals(esQueryBean.getSubType()) || "云图".equals(esQueryBean.getSubType())){
                    List<Map> list = new ArrayList<>();
                    Map<String,String> map = new HashMap<>();
                    map.put("name","last_time.keyword");
                    map.put("lt", CronPub.getOneTimeBycron_String(DIMap.get("time_interval").toString(),"yyyy-MM-dd HH:mm:ss.SSSZ",new Date()));
                    list.add(map);
                    params.put("range",list);
                    params.put("type.keyword",esQueryBean.getSubType());
                    params.put("fields.module.keyword",esQueryBean.getModule());
//                    params.put("fields.ip_addr.keyword",esQueryBean.getStrIp());
                    params.put("sort","last_time.keyword");
                    params.put("size","2");
                }else if("radarlatlon".equals(esQueryBean.getSubType()) || "雷达".equals(esQueryBean.getSubType())){        //判断是雷达系列数据
                    // 获取 24 分钟后的时间 （4个时次后的时间）
                    Calendar calendar = Calendar.getInstance();
                    Date date = new Date();
                    calendar.setTime(date);
                    calendar.add(Calendar.MINUTE,24);

                    List<Map> list = new ArrayList<>();
                    Map<String,String> map = new HashMap<>();
                    map.put("name","last_time.keyword");
                    map.put("lt", CronPub.getOneTimeBycron_String(DIMap.get("time_interval").toString(),"yyyy-MM-dd HH:mm:ss.SSSZ",calendar.getTime()));
                    list.add(map);
                    params.put("range",list);
                    params.put("type.keyword",esQueryBean.getSubType());
                    params.put("fields.module.keyword",esQueryBean.getModule());
//                    params.put("fields.ip_addr.keyword",esQueryBean.getStrIp());
                    params.put("sort","last_time.keyword");
                    params.put("size","5");
                }else /*if("LatLonQREFEnd".equals(esQueryBean.getSubType()))*/{
                    params.put("type.keyword",esQueryBean.getSubType());
                    params.put("fields.module.keyword",esQueryBean.getModule());
//                    params.put("fields.ip_addr.keyword",esQueryBean.getStrIp());
                    params.put("sort","fields.data_time.keyword");  //注意这里，加工雷达没有时次
                    params.put("size","1");
                }

//                System.out.println(params);
                EsQueryBean esQuery = new EsQueryBean();
                esQuery.setIndices(esQueryBean.getIndices());
                esQuery.setTypes(esQueryBean.getTypes());
                esQuery.setParameters(params);

                //查询 获取数据
                JSONObject j = JSONObject.fromObject(mapObject);
                String before = j.toString();

                mapObject = esQueryService.getData(esQuery);

                JSONObject jb = JSONObject.fromObject(esQueryBean);
                j = JSONObject.fromObject(mapObject);
                System.out.println(jb.toString()+"\r\n-" + before + "\r\n-" +j.toString() + "\r\n");

                if(mapObject.get("result").equals("success") && mapObject.get("resultData") != null){
                    List<Object>  returnList = new ArrayList<>();
                    List<Object> listMap = (List<Object>)mapObject.get("resultData");
                    for(int i = 0 ; i < listMap.size() ; i++){

                        Map<String,Object> mp = (Map<String, Object>) listMap.get(i);
                        Map<String ,Object> fieldMp = (Map<String ,Object>)mp.get("fields");
                        if(fieldMp.containsKey("file_name")){
                            returnList.add(mp);
                            break;
                        }
                        if(i == listMap.size()-1 && returnList.size() == 0){
                            returnList.add(listMap.get(i));
                            break;
                        }

                    }
                    mapObject.put("resultData",returnList);
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
                if( "ReadFY2NC".equals(esQueryBean.getSubType()) || "云图".equals(esQueryBean.getSubType()) || "雷达".equals(esQueryBean.getSubType())){
                    Map<String,Object> mustMap = new HashMap<>();
                    mustMap.put("type.keyword",esQueryBean.getSubType());
                    mustMap.put("fields.module.keyword",esQueryBean.getModule());

                    Map<String,Object> mustNotMap = new HashMap<>();
                    mustNotMap.put("aging_status.keyword","未处理");

                    params.put("must",mustMap);
                    params.put("mustNot",mustNotMap);
                    params.put("sort","last_time.keyword");
                    params.put("size",esQueryBean.getSize());

                }else /*if("LatLonQREFEnd".equals(esQueryBean.getSubType()))*/{
                    Map<String,Object> mustMap = new HashMap<>();
                    mustMap.put("type.keyword",esQueryBean.getSubType());
                    mustMap.put("fields.module.keyword",esQueryBean.getModule());

                    params.put("must",mustMap);
                    params.put("sort","fields.data_time.keyword");  //注意这里，加工雷达没有时次
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

    public Map<String,Object> find_agg_terms(EsQueryBean esQueryBean){
        long start = System.currentTimeMillis();
        Map<String,Object> mapObject = null;
        try {
            //判断参数是否正确
            if(esQueryBean == null){
                outMap.put(KEY_RESULT,VAL_ERROR);
                outMap.put(KEY_RESULTDATA,null);
                outMap.put(KEY_MESSAGE,"参数错误！");
            }else{
                mapObject = esQueryService.find_agg_term(esQueryBean);
                //这里做一些数据字段 转换、过滤
                Map<String,Object> mp  = (Map) mapObject.get("resultData");
                Map<String,Object> resMap = new HashMap<>();
                for (String strKey : mp.keySet()){
                    if(Pub.machingMap.containsKey(strKey)){
                        String name = Pub.machingMap.get(strKey);
                        Map<String , Object> childMap = (Map<String, Object>) mp.get(strKey);

                        if(!resMap.containsKey(name)){
                            Map<String , Integer> countMap = new HashMap<>();
                            countMap.put("count",0);
                            countMap.put("OK",0);
                            resMap.put(name,countMap);
                        }

                        Map<String,Integer> tempMap = (Map<String, Integer>) resMap.get(name);
                        tempMap.put("count",tempMap.get("count") + Integer.valueOf(childMap.get("count").toString()));
                        if(childMap.containsKey("OK")){
                            tempMap.put("OK",tempMap.get("OK") + Integer.valueOf(childMap.get("OK").toString()));
                        }
                    }
                }

                outMap.put(KEY_RESULT,mapObject.get(KEY_RESULT));
                outMap.put(KEY_MESSAGE,mapObject.get(KEY_MESSAGE));
                outMap.put(KEY_RESULTDATA,resMap);
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

    public List<Map> agg_realTime(String index ,String type, String module , Map<String,Object> alertMap){
        if(alertMap == null || alertMap.size() < 1  || StringUtils.isEmpty(module) || StringUtils.isEmpty(index) || StringUtils.isEmpty(type)){
            return null;
        }
        String cron = null ,  data_time = null , subType = null ;
        Map<String,Object> params = null;
        EsQueryBean esQueryBean = null;
        for (String strKey : alertMap.keySet()){
            params = new HashMap<>();

            subType = alertMap.get("DI_name").toString();
            cron = alertMap.get("time_interval").toString();
            data_time = CronPub.getLastTimeBycron_String(cron,"yyyy-MM-dd HH:mm:ss.SSSZ",new Date());

            params.put("type.keyword",subType);
            params.put("fields.module.keyword",module);
            params.put("fields.data_time.keyword",data_time);
            esQueryBean = new EsQueryBean();
            esQueryBean.setTypes(new String[]{type});
            esQueryBean.setIndices(new String[]{index});
            esQueryBean.setParameters(params);

        }

        return null;
    }

}
