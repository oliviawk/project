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
                if("ReadFY2NC".equals(esQueryBean.getSubType()) || "云图".equals(esQueryBean.getSubType()) || "炎热指数".equals(esQueryBean.getSubType())){
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
                    map.put("name","fields.data_time.keyword");
                    map.put("gte", Pub.transform_DateToString(startDate,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    map.put("lt", Pub.transform_DateToString(endDate,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    list.add(map);
                    params.put("range",list);
                    params.put("type.keyword",esQueryBean.getSubType());
                    params.put("fields.module.keyword",esQueryBean.getModule());
                    params.put("size","100");
                }else /*if("LatLonQREFEnd".equals(esQueryBean.getSubType()))*/{
                    params.put("type.keyword",esQueryBean.getSubType());
                    params.put("fields.module.keyword",esQueryBean.getModule());
                    params.put("fields.ip_addr.keyword",esQueryBean.getStrIp());
                    params.put("sort","fields.data_time.keyword");  //注意这里，加工雷达没有时次
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
                    mustMap.put("type.keyword",esQueryBean.getSubType());
                    mustMap.put("fields.module.keyword",esQueryBean.getModule());

                    Map<String,Object> mustNotMap = new HashMap<>();
                    mustNotMap.put("aging_status.keyword","未处理");

                    params.put("must",mustMap);
                    params.put("mustNot",mustNotMap);
                    params.put("sort","last_time.keyword");
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
                    map.put("name","fields.data_time.keyword");
                    map.put("gte", Pub.transform_DateToString(startDate,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    map.put("lt", Pub.transform_DateToString(endDate,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    list.add(map);

                    Map<String,Object> mustMap = new HashMap<>();
                    mustMap.put("type.keyword",esQueryBean.getSubType());
                    mustMap.put("fields.module.keyword",esQueryBean.getModule());

                    params.put("must",mustMap);
                    params.put("range",list);
                    params.put("sort","fields.data_time.keyword");
                    params.put("size","100");
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





    public Object getCpuData(String ip){
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        com.alibaba.fastjson.JSONObject resultData = new com.alibaba.fastjson.JSONObject();
        List<Object> controlsData = new ArrayList<Object>();
        List<Object> tableData = new ArrayList<Object>();
        String titleTime = null;
        List list = new ArrayList();
        String data = getBaseSourceData(ip);
        logger.info("data:"+data);
        com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(data);
        com.alibaba.fastjson.JSONArray jsonArr = jsonObj.getJSONArray("resultData");
        for (Object object : jsonArr) {
            com.alibaba.fastjson.JSONObject obj = (com.alibaba.fastjson.JSONObject)object;
            String m = obj.getJSONObject("fields").getString("metric");
            if(m.contains("cpu")){
                com.alibaba.fastjson.JSONObject jsonData = new com.alibaba.fastjson.JSONObject();
                String str = obj.getJSONObject("fields").getString("value");
                String string = str.split("%")[0];
                String time = obj.getJSONObject("fields").getString("data_time");
                jsonData.put("used", Double.parseDouble(string)*10);
                jsonData.put("free", 100-Double.parseDouble(string)*10);
                try {
                    Date parse = sdf2.parse(time);
                    jsonData.put("time", sdf2.format(parse));
                    titleTime=time;
                    list.add(string);
                    controlsData.add(jsonData);
                } catch (ParseException e) {
                    logger.info("时间格式解析错误!");
                    e.printStackTrace();
                }

            }
        }

        double max = 0;
        double min = 100;
        double total = 0;
        double current = 0;
        for (Object object : list) {
            double d = Double.parseDouble(object.toString()) ;
            total += d;
            current = d;
        }
        Collections.sort(list);
        logger.info("list:"+list);
        max = Double.parseDouble(list.get(list.size()-1).toString());
        min = Double.parseDouble(list.get(0).toString());
        double n = total / list.size();
        double avg = Double.parseDouble(String.format("%.1f", n));
        Map<String, Object> t = new LinkedHashMap<String, Object>();
        Map<String, Object> t2 = new LinkedHashMap<String, Object>();
        t.put("max",max);
        t.put("min",min);
        t.put("avg",avg);
        t.put("current",current);
        t2.put("max",100-min);
        t2.put("min",100-max);
        t2.put("avg",100-avg);
        t2.put("current",100-current);
        tableData.add(t);
        tableData.add(t2);
        resultData.put("tableData", tableData);
        resultData.put("controlsData", controlsData);
        Map<String, Object> outMap = new HashMap<String, Object>();
        outMap.put("result", "success");
        outMap.put("resultData", resultData);
        outMap.put("titleTime", titleTime);
        outMap.put("message", "数据加载成功！");
        return outMap;
    }


    /**
     * 基础资源数据查询
     *
     * @return
     */
    public  String getBaseSourceData(String ip) {
        EsQueryBean es = new EsQueryBean();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String s1 = "log_"+ sdf.format(System.currentTimeMillis());
        String s2 = "log_"+ sdf.format(System.currentTimeMillis()-(3600*24*1000));

        String[] indice = new String[] { s2 ,s1 };
        es.setIndices(indice);
        String[] types = { "FZJC" };
        es.setTypes(types);
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> mustMap = new HashMap<>();
        mustMap.put("fields.ip.keyword", ip);
//		mustMap.put("fields.metric", "system.cpu.pct_usage");

        params.put("must", mustMap);
        params.put("sort", "fields.data_time.keyword");
        params.put("resultAll", true);


        List<Map> rangeList = new ArrayList<>();
        Map<String, String> rangeMap = new HashMap<>();
        rangeMap.put("name", "fields.data_time.keyword");
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            rangeMap.put("gt", "2017-11-10 11:00");
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        rangeList.add(rangeMap);
        params.put("range", rangeList);
        es.setParameters(params);
        logger.info("es:"+ com.alibaba.fastjson.JSON.toJSONString(es));
        try {
            Map<String, Object> data_new = esQueryService.getData_new(es);
            logger.info("data_new:"+ com.alibaba.fastjson.JSON.toJSONString(data_new));
            return com.alibaba.fastjson.JSON.toJSONString(data_new);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /*
     * cpu
     */
    public Object getCpuDataSham() {
        List<Object> controlsData = new ArrayList<Object>();
        List<Object> tableData = new ArrayList<Object>();
        Map<String, Object> resultData = new LinkedHashMap<String, Object>();
        List<Integer> list = new ArrayList<Integer>();
        int hTime = 10;
        int mTime = 0;
        int dTime = 18;
        int total = 0;
        int max;
        int min;
        int avg;
        int current = 0;
        for (int i = 0; i < 144; i++) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("time", "2017-09-" + dTime + " " + hTime + ":" + mTime);
            int random1 = (int) (Math.random() * 10);
            data.put("user", random1);
            current = random1;
            total += random1;
            data.put("system", random1 + 10);
            data.put("idle", 100 - (random1 * 2 + 10));
            controlsData.add(data);
            list.add(random1);
            if (mTime == 50) {
                if (hTime == 23) {
                    dTime++;
                    hTime = 0;
                } else {
                    hTime++;
                }
                mTime = 0;
            } else {
                mTime += 10;
            }
        }
        Collections.sort(list);
        min = list.get(0);
        max = list.get(list.size() - 1);
        avg = Math.round(total / list.size());
        Map<String, Object> t = new LinkedHashMap<String, Object>();
        Map<String, Object> t2 = new LinkedHashMap<String, Object>();
        Map<String, Object> t3 = new LinkedHashMap<String, Object>();
        t.put("max", 100 - (min * 2 + 10));
        t.put("min", 100 - (max * 2 + 10));
        t.put("avg", 100 - (avg * 2 + 10));
        t.put("current", 100 - (current * 2 + 10));
        t2.put("min", min + 10);
        t2.put("max", max + 10);
        t2.put("avg", avg + 10);
        t2.put("current", current + 10);
        t3.put("min", min);
        t3.put("max", max);
        t3.put("avg", avg);
        t3.put("current", current);

        tableData.add(t);
        tableData.add(t2);
        tableData.add(t3);

        resultData.put("tableData", tableData);
        resultData.put("controlsData", controlsData);
        Map<String, Object> outMap = new HashMap<String, Object>();
        outMap.put("result", "success");
        outMap.put("resultData", resultData);
        String dateStr = DateTool.dateToString(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm");
        outMap.put("titleTime", dateStr);
        outMap.put("message", "数据加载成功！");
        return outMap;
    }

    /*
     * 内存memory
     *
     */
    public Object getMemoryDataSham() {
        List<Object> controlsData = new ArrayList<Object>();
        List<Object> tableData = new ArrayList<Object>();
        Map<String, Object> resultData = new LinkedHashMap<String, Object>();
        List<Integer> list = new ArrayList<Integer>();
        int hTime = 10;
        int mTime = 0;
        int dTime = 18;
        int total = 0;
        int max;
        int min;
        int avg;
        int current = 0;

        for (int i = 0; i < 144; i++) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("time", "2017-09-" + dTime + " " + hTime + ":" + mTime);
            int random = (int) (Math.random() * 10);
            data.put("used", random + 10);
            data.put("free", 100 - (random + 10));
            current = random;
            total += random;
            controlsData.add(data);
            list.add(random);
            if (mTime == 50) {
                if (hTime == 23) {
                    dTime++;
                    hTime = 0;
                } else {
                    hTime++;
                }
                mTime = 0;
            } else {
                mTime += 10;
            }
        }
        Collections.sort(list);
        min = list.get(0);
        max = list.get(list.size() - 1);
        avg = Math.round(total / list.size());
        Map<String, Object> t = new LinkedHashMap<String, Object>();
        Map<String, Object> t2 = new LinkedHashMap<String, Object>();
        t.put("max", max + 10);
        t.put("min", min + 10);
        t.put("avg", avg + 10);
        t.put("current", current + 10);
        t2.put("min", 90 - min);
        t2.put("max", 90 - max);
        t2.put("avg", 90 - avg);
        t2.put("current", 90 - current);
        tableData.add(t);
        tableData.add(t2);

        resultData.put("tableData", tableData);
        resultData.put("controlsData", controlsData);

        Map<String, Object> outMap = new HashMap<String, Object>();
        outMap.put("result", "success");
        outMap.put("resultData", resultData);
        String dateStr = DateTool.dateToString(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm");
        outMap.put("titleTime", dateStr);
        outMap.put("message", "数据加载成功！");
        return outMap;
    }

    /*
     * 网络net
     */
    public Object getNetDataSham() {
        Map<String, Object> resultData = new HashMap<String, Object>();

        List<D3NetBean> controlsData = new ArrayList<D3NetBean>();
        int hTime = 10;
        int mTime = 0;
        int dTime = 18;
        int total = 0;
        double current = 0;
        int total2 = 0;
        double current2 = 0;
        List<Double> list = new ArrayList<Double>();
        List<Double> list2 = new ArrayList<Double>();
        for (int i = 0; i < 144; i++) {
            D3NetBean data = new D3NetBean();
            data.setTime("2017-09-" + dTime + " " + hTime + ":" + mTime);
            double random1 = (Math.random() * 1000);
            list.add(random1);
            total += random1;
            current = random1;
            double random2 = (Math.random() * 1000);
            list2.add(random2);
            total2 += random2;
            current2 = random2;
            data.setUpload(Double.parseDouble((String.format("%.2f", -random1))));
            data.setDown(Double.parseDouble((String.format("%.2f", random2))));
            controlsData.add(data);
            if (mTime == 50) {
                if (hTime == 23) {
                    dTime++;
                    hTime = 0;
                } else {
                    hTime++;
                }
                mTime = 0;
            } else {
                mTime += 10;
            }
        }

        Collections.sort(list);
        Collections.sort(list2);

        List tableData = new ArrayList();
        com.alibaba.fastjson.JSONObject data = new com.alibaba.fastjson.JSONObject();
        com.alibaba.fastjson.JSONObject data2 = new com.alibaba.fastjson.JSONObject();
        data2.put("min", Double.parseDouble((String.format("%.2f", list2.get(0)))) + "MB");
        data2.put("max", Double.parseDouble((String.format("%.2f", list2.get(list2.size() - 1)))) + "MB");
        data2.put("avg", total2 / list2.size() + "MB");
        data2.put("current", Double.parseDouble((String.format("%.2f", current2))) + "MB");
        data.put("min", Double.parseDouble((String.format("%.2f", list.get(0)))) + "MB");
        data.put("max", Double.parseDouble((String.format("%.2f", list.get(list.size() - 1)))) + "MB");
        data.put("avg", total / list.size() + "MB");
        data.put("current", Double.parseDouble((String.format("%.2f", current))) + "MB");

        tableData.add(data2);
        tableData.add(data);
        resultData.put("controlsData", controlsData); // 控件数据
        resultData.put("tableData", tableData); // 表格数据

        Map<String, Object> outMap = new HashMap<String, Object>();
        outMap.put("result", "success");
        outMap.put("resultData", resultData);
        String dateStr = DateTool.dateToString(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm");
        outMap.put("titleTime", dateStr);
        outMap.put("message", "数据加载成功！");
        return outMap;
    }

	/*
	 * 磁盘directory
	 */

    public Object getDirectoryUsedDataSham() {

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name", "10.28.102.22");

        List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
        for (int j = 0; j < 3; j++) {
            Map<String, Object> value = new HashMap<String, Object>();
            value.put("path", "/data" + j);
            value.put("free", (int) (Math.random() * 800 + 100));
            value.put("unit", "GB");
            value.put("total", 1000);
            values.add(value);
        }
        data.put("values", values);

        Map<String, Object> outMap = new HashMap<String, Object>();
        outMap.put("result", "success");
        outMap.put("resultData", data);
        String dateStr = DateTool.dateToString(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm");
        outMap.put("titleTime", dateStr);
        outMap.put("message", "数据加载成功！");
        return outMap;
    }
}
