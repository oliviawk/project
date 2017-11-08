package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.DataBean;
import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_Exsit;
import com.cn.hitec.bean.EsWriteBean;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.util.CronPub;
import com.cn.hitec.util.Pub;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.event.ListDataEvent;
import java.util.*;

@Service
public class ConfigService {
    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);

    @Autowired
    EsQueryService esQueryService;
    @Autowired
    EsWriteService esWriteService;

    /**
     * 初始化  采集   DI信息表
     * @return
     * @throws Exception
     */
    public int initAlertConfigData_collect() throws Exception{

        List<String> jsonList = new ArrayList<>();
        /*-----------------采集---------------------------*/
        jsonList.add("{\"DI_name\":\"雷达\",\"time_interval\":\"0 0/6 * * * ? *\",\"should_time\":\"1200\",\"last_time\":\"1800\",\"data_type\":\"气象基本资料\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.220\",\"path\":\"/mnt/nmic2017/radar/latlon/\",\"file_name\":\"ACHN.QREF000.yyyymmdd.xxxxxx.latlon\",\"transfer_type\":\"ftp推送\"}");
        jsonList.add("{\"DI_name\":\"云图\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":3000,\"last_time\":3540,\"data_type\":\"气象基本资料\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.220\",\"path\":\"/home/data/satellite/HDF/\",\"file_name\":\"SEVP_NSMC_WXGN_FY2G_E99_ACHN_LNO_P9_*.HDF\",\"transfer_type\":\"ftp推送\"}");

        EsWriteBean esWriteBean = new EsWriteBean();
        esWriteBean.setIndex("config");
        esWriteBean.setType("collect");
        esWriteBean.setData(jsonList);
        Map<String,Object> map = esWriteService.add(esWriteBean);
        Map<String,Object> resultMap = (Map<String,Object>)map.get("resultData");
        int n = Integer.valueOf(resultMap.get("insert_number").toString());
        return n;
    }

    /**
     * 初始化  加工  DI信息表
     * @return
     * @throws Exception
     */
    public int initAlertConfigData_machining() throws Exception{

        List<String> jsonList = new ArrayList<>();
        /*-----------------加工---------------------------*/
        jsonList.add("{\"DI_name\":\"ReadFY2NC\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":3600,\"last_time\":4200,\"data_type\":\"气象基本资料\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.223\",\"path\":\"\",\"file_name\":\"SEVP_NSMC_WXGN_FY2G_E99_ACHN_LNO_P9_*.HDF\",\"transfer_type\":\"ftp推送\"}");
        jsonList.add("{\"DI_name\":\"风流场\",\"time_interval\":\"0 0 2/3 * * ? *\",\"should_time\":0,\"last_time\":0,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.223\",\"path\":\"Z:\\\\NoGeography\\\\forecast\\\\t639\\\\\",\"file_name\":\"T639_GMFS_WIND_2017102508.json\",\"transfer_type\":\"\"}");

        jsonList.add("{\"DI_name\":\"炎热指数\",\"name\":\"20\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":300,\"last_time\":360,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.223\",\"path\":\"Z://NoGeography//live//hotIndex//\",\"file_name\":\"hot*.txt\",\"transfer_type\":\"\"}");
        jsonList.add("{\"DI_name\":\"炎热指数\",\"name\":\"55\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":3600,\"last_time\":3660,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.30.16.223\",\"path\":\"Z://NoGeography//live//hotIndex//\",\"file_name\":\"hot*.txt\",\"transfer_type\":\"\"}");


        EsWriteBean esWriteBean = new EsWriteBean();
        esWriteBean.setIndex("config");
        esWriteBean.setType("machining");
        esWriteBean.setData(jsonList);
        Map<String,Object> map = esWriteService.add(esWriteBean);
        Map<String,Object> resultMap = (Map<String,Object>)map.get("resultData");
        int n = Integer.valueOf(resultMap.get("insert_number").toString());
        return n;
    }

    /**
     * 初始化  分发  DI信息表
     * @return
     * @throws Exception
     */
    public int initAlertConfigData_distribute() throws Exception{

        List<String> jsonList = new ArrayList<>();
        /*-----------------分发---------------------------*/
        jsonList.add("{\"DI_name\":\"云图\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":3600,\"last_time\":4500,\"data_type\":\"气象基本资料\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.0.74.226\",\"path\":\"/home/datamgr/xts_gwyyj/cloudmap/\",\"file_name\":\"SEVP_NSMC_WXGN_FY2G_E99_ACHN_LNO_P9_*.HDF\",\"transfer_type\":\"ftp推送\"}");
        jsonList.add("{\"DI_name\":\"雷达\",\"time_interval\":\"0 0/6 * * * ? *\",\"should_time\":\"1500\",\"last_time\":\"2100\",\"data_type\":\"气象基本资料\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.0.74.226\",\"path\":\"/home/datamgr/xts_gwyyj/Radar/\",\"file_name\":\"MSP3_PMSC_RADAR_BREF_L88_CHN_*.png\",\"transfer_type\":\"ftp推送\"}");
        jsonList.add("{\"DI_name\":\"T639\",\"time_interval\":\"0 0 2/3 * * ? *\",\"should_time\":0,\"last_time\":0,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.0.74.226\",\"path\":\"/home/datamgr/xts_gwyyj/T639/\",\"file_name\":\"T639_GMFS_WIND_*.json\",\"transfer_type\":\"\"}");
        jsonList.add("{\"DI_name\":\"炎热指数\",\"name\":\"20\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":900,\"last_time\":960,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.0.74.226\",\"path\":\"/home/datamgr/xts_gwyyj/hotIndex/\",\"file_name\":\"hot*.txt\",\"transfer_type\":\"\"}");
        jsonList.add("{\"DI_name\":\"炎热指数\",\"name\":\"55\",\"time_interval\":\"0 0 * * * ? *\",\"should_time\":4200,\"last_time\":4260,\"data_type\":\"\",\"data_source\":\"\",\"contacts\":\"\",\"IP\":\"10.0.74.226\",\"path\":\"/home/datamgr/xts_gwyyj/hotIndex/\",\"file_name\":\"hot*.txt\",\"transfer_type\":\"\"}");

        EsWriteBean esWriteBean = new EsWriteBean();
        esWriteBean.setIndex("config");
        esWriteBean.setType("distribute");
        esWriteBean.setData(jsonList);
        Map<String,Object> map = esWriteService.add(esWriteBean);
        Map<String,Object> resultMap = (Map<String,Object>)map.get("resultData");
        int n = Integer.valueOf(resultMap.get("insert_number").toString());
        return n;
    }


    /**
     * 查询
     * @return
     */
    public List<Map> getConfigAlert(String type){
        List<Map> resultList = null;

        if(StringUtils.isEmpty(type)){
            logger.error("请输入type");
            return null;
        }
        try {
            EsQueryBean esQueryBean = new EsQueryBean();
            esQueryBean.setIndices(new String[]{"config"});
            esQueryBean.setTypes(new String[]{type});

            Map<String,Object> resultMap = esQueryService.getAlertData(esQueryBean);
            if(resultMap == null ){
                logger.warn("getConfigAlert is null");
                return null;
            }else {
                if(resultMap.get("result").equals("success")){
                    resultList = (List<Map>) resultMap.get("resultData");
                }else{
                    logger.warn(resultMap.get("message").toString());
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            resultList = null;
        } finally {
            return resultList;
        }
    }

    public void initAlertMap(){
        /*---------------------采集------------------------*/
        List<Map> listMap_collect = getConfigAlert("collect");
        for (Map map : listMap_collect){
            String DI_name = map.get("DI_name").toString();
            if("T639".equals(DI_name) || "风流场".equals(DI_name)){
                Pub.DIMap_t639.put(DI_name,map);
            }else{
                Pub.DIMap_collect.put(DI_name,map);
            }

        }
        List<Map> listMap_machining = getConfigAlert("machining");
        for (Map map : listMap_machining){
            String DI_name = map.get("DI_name").toString();
            if("T639".equals(DI_name) || "风流场".equals(DI_name)){
                Pub.DIMap_t639.put(DI_name,map);
            }else{
                Pub.DIMap_machining.put(DI_name,map);
            }

        }
        List<Map> listMap_distribute = getConfigAlert("distribute");
        for (Map map : listMap_distribute){
            String DI_name = map.get("DI_name").toString();
            if("T639".equals(DI_name) || "风流场".equals(DI_name)){
                Pub.DIMap_t639.put(DI_name,map);
            }else{
                Pub.DIMap_distribute.put(DI_name,map);
            }

        }
    }

    /**
     * 生成 ID 数据表（节目表）
     * @param type
     * @param module
     */
    public void createAlertDI(String type,String module , Map<String , Object> DIMap){

        if(DIMap == null || DIMap.size() < 1){
            logger.warn("createAlertItem is fail ： alertMap is null or 0 in length");
            return ;
        }
        try {

            //生成日历插件， 计算出 第二天的开始时间和结束时间
            Calendar calendar = Calendar.getInstance();
            Date date = new Date();
            calendar.setTime(date);

            calendar.add(Calendar.DAY_OF_MONTH , 1);
            calendar.set(Calendar.HOUR_OF_DAY , 0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND , 0 );

            Date startDate = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date endDate = calendar.getTime();

            String strIndex = Pub.Index_Head+Pub.transform_DateToString(startDate,Pub.Index_Food_Simpledataformat);
            Map<String,Object> map = null;
            //循环 告警配置信息
            for (String key: DIMap.keySet()){
                map = (Map<String,Object>)DIMap.get(key);    //获取单条配置信息

                String cron = map.get("time_interval").toString();
                List<Date> timeList = CronPub.getTimeBycron_Date(cron,startDate,endDate);
                List<String> listDataBean = new ArrayList<>();
                String subType = map.get("DI_name").toString();
                String name = "";
                String IP = map.get("IP").toString();
                String path = map.get("path").toString();
                for (Date dt : timeList){
//                    String indexKey = Pub.Index_Head+Pub.transform_DateToString(dt,Pub.Index_Food_Simpledataformat);
//                    //判断是否预生成过，没有的话生成
//                    if(isExist_DI_Data(indexKey,type,key)){
//                        continue;
//                    }
                    DataBean dataBean = new DataBean();
                    dataBean.setName(name);
                    dataBean.setType(subType);
                    //这里需要封装一个方法，根据不同的 数据源、时次，生成不同的应到时间和最晚时间
                    int cron_shouldTime = Integer.valueOf(map.get("should_time").toString()) ;
                    int cron_lastTime = Integer.valueOf(map.get("last_time").toString()) ;
                    dataBean.setShould_time(Pub.transform_longDataToString(dt.getTime()/1000+cron_shouldTime,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    dataBean.setLast_time(Pub.transform_longDataToString(dt.getTime()/1000+cron_lastTime,"yyyy-MM-dd HH:mm:ss.SSSZ"));

                    dataBean.setAging_status("未处理");
                    Map<String,Object> fields = new HashMap<>();
                    fields.put("module",module);
                    if("炎热指数".equals(subType)){
                        dataBean.setName(map.get("name").toString());
                    }else{
                        fields.put("data_time",Pub.transform_DateToString(dt,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    }

                    fields.put("ip_addr", IP);
                    fields.put("file_name",path);
                    dataBean.setFields(fields);

                    listDataBean.add(JSON.toJSONString(dataBean));
                }
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                //分批次录入数据
                EsWriteBean esWriteBean = new EsWriteBean();
                esWriteBean.setIndex(strIndex);
                esWriteBean.setType(type);
                esWriteBean.setData(listDataBean);
                Map<String,Object> response= esWriteService.add(esWriteBean);
                Map<String,Object> responseData = (Map<String,Object>)response.get("resultData");
                if(response.get(Pub.KEY_RESULT).toString().equals(Pub.VAL_SUCCESS)){
                    logger.info(module+"->"+map.get("DI_name")+"->录入数据条数："+responseData.get("insert_number"));
                }else{
                   logger.error(module+"->"+map.get("DI_name")+"->"+response.get(Pub.KEY_MESSAGE));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            return ;
        }
    }


    /**
     * 生成T639 DI数据 只适用于T639
     * @param DIMapObj
     */
    public void createT639DI(String type,Map<String , Object> DIMapObj,int numDay){
        if(DIMapObj == null || DIMapObj.size() < 1){
            logger.warn("createT639DI is fail ： alertMap is null or 0 in length");
            return ;
        }

        try {
            //生成日历插件， 计算出 第二天的开始时间和结束时间
            Calendar calendar = Calendar.getInstance();
            Date date = new Date();
            calendar.setTime(date);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND , 0 );

            Date startDate = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH, numDay+1);
            calendar.set(Calendar.HOUR_OF_DAY,0);
            Date endDate = calendar.getTime();


//        String strIndex = Pub.Index_Head+Pub.transform_DateToString(startDate,Pub.Index_Food_Simpledataformat);
            Map<String,Object> map = null;
            //循环 告警配置信息
            for (String key: DIMapObj.keySet()){
                String module = "T639".equals(key) ? "分发":"加工";
                int addNum = 0;
                map = (Map<String,Object>)DIMapObj.get(key);    //获取单条配置信息

                String cron = map.get("time_interval").toString();
                List<Date> timeList = CronPub.getTimeBycron_Date(cron,startDate,endDate);
                String subType = map.get("DI_name").toString();
                String name = "";
                String IP = map.get("IP").toString();
                String path = map.get("path").toString();

                Map<String,Object> indexMap = new HashMap<>();
                for (Date dt : timeList){
                    String indexKey = Pub.Index_Head+Pub.transform_DateToString(dt,Pub.Index_Food_Simpledataformat);
                    //判断是否预生成过，没有的话生成
                    if(isExist_DI_Data(indexKey,type,key)){
                        continue;
                    }
                    if(!indexMap.containsKey(indexKey)){
                        indexMap.put(indexKey,new ArrayList<>());
                    }
                    DataBean dataBean = new DataBean();
                    dataBean.setName(name);
                    dataBean.setType(subType);

                    dataBean.setAging_status("未处理");
                    Map<String,Object> fields = new HashMap<>();
                    fields.put("module",module);
                    fields.put("data_time",Pub.transform_DateToString(dt,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    fields.put("ip_addr", IP);
                    fields.put("file_name", path);
                    fields.put("end_time", Pub.transform_DateToString(date,"yyyy-MM-dd HH:mm:ss.SSSZ"));
                    dataBean.setFields(fields);

                    ((List<String>)indexMap.get(indexKey)).add(JSON.toJSONString(dataBean));
                }
                logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

                for (String strIndex : indexMap.keySet()){
                    //分批次录入数据
                    EsWriteBean esWriteBean = new EsWriteBean();
                    esWriteBean.setIndex(strIndex);
                    esWriteBean.setType(type);
                    esWriteBean.setData((List<String>) indexMap.get(strIndex));
                    Map<String,Object> response= esWriteService.add(esWriteBean);
                    Map<String,Object> responseData = (Map<String,Object>)response.get("resultData");
                    if(response.get(Pub.KEY_RESULT).toString().equals(Pub.VAL_SUCCESS)){
                        addNum += (int)responseData.get("insert_number");
                    }else{
                        logger.error(module+"->"+subType+"->"+response.get(Pub.KEY_MESSAGE));
                    }
                }

                logger.info(module+"->"+subType+"->录入数据条数："+addNum);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
