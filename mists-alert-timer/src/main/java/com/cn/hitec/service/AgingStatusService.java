package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_Exsit;
import com.cn.hitec.bean.EsWriteBean;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.util.CronPub;
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

    public int  collect_task(Date nowDate) throws Exception{
        int up_number = 0;
        EsQueryBean esQueryBean = new EsQueryBean();
        String index = Pub.Index_Head+ Pub.transform_DateToString(nowDate,Pub.Index_Food_Simpledataformat);
        esQueryBean.setIndices(new String[]{index});
        esQueryBean.setTypes(new String[]{"FZJC"});

        Map<String,Object> params = new HashMap<>();
//            params.put("type","satellite");
//            params.put("fields.module","采集");
        params.put("aging_status","未处理");
        params.put("size","50");
//            params.put("sort","fields.data_time");
        List<Map> list = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("name","last_time");
        map.put("lt", Pub.transform_DateToString(nowDate, "yyyy-MM-dd HH:mm:ss.SSSZ"));
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
        return up_number;
    }

    public int task_T639() throws Exception{
        int up_number = 0;
        //生成日历插件， 计算出 第二天的开始时间和结束时间
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
            //判断是否有index
            if(isExist_DI_Data(indexKey,"FZJC",null)){
                indicesList.add(indexKey);
            }
            temp.add(indexKey);
        }
        String[] indices = new String[indicesList.size()];
        indicesList.toArray(indices);
        if(indices.length < 1){
            logger.error("---获取到的index为空");
            return -1;
        }

        EsQueryBean esQueryBean = new EsQueryBean();
        esQueryBean.setIndices(indices);
        esQueryBean.setTypes(new String[]{"FZJC"});

        Map<String,Object> params = new HashMap<>();

        List<Map> rangeList = new ArrayList<>();
        Map<String,Object> rangeMap_1 = new HashMap<>();
        rangeMap_1.put("name","fields.data_time");
        rangeMap_1.put("gte", Pub.transform_DateToString(startDate, "yyyy-MM-dd HH:mm:ss.SSSZ"));
        rangeMap_1.put("lt", Pub.transform_DateToString(endDate, "yyyy-MM-dd HH:mm:ss.SSSZ"));
        rangeList.add(rangeMap_1);

        params.put("aging_status","未处理");
        params.put("type","T639,风流场");
        params.put("range",rangeList);
        params.put("size","50");

        esQueryBean.setParameters(params);

//            System.out.println(Pub.transform_DateToString(nowDate,"yyyy-MM-dd HH:mm:ss.SSSZ"));
        //查询到 所有未处理状态的数据
        Map<String , Object> responseMap = esQueryService.getData_resultId(esQueryBean);

        if(!responseMap.get(Pub.KEY_RESULT).equals(Pub.VAL_SUCCESS)){
            return -1;
        }

        //得到结果集
        Map<String,Object> tempMap  = (Map<String,Object>) responseMap.get(Pub.KEY_RESULTDATA);

        Map<String,Object> resMap = null;
        EsWriteBean esWriteBean = null;
        long overTime = 24*60*60*1000;
        for (String uid : tempMap.keySet()){
            try {
                resMap = (Map<String, Object>) tempMap.get(uid);
                Map<String,Object> fields = (Map<String, Object>) resMap.get("fields");
                Date endTime = Pub.transform_StringToDate(fields.get("data_time").toString(),"yyyy-MM-dd HH:mm:ss.SSSZ");
                if(date.getTime() - endTime.getTime() > overTime){
                    Date dt = Pub.transform_StringToDate(fields.get("data_time").toString(),"yyyy-MM-dd HH:mm:ss.SSSZ");
                    String index = Pub.Index_Head+Pub.transform_DateToString(dt,Pub.Index_Food_Simpledataformat);
                    Map<String,Object> pam = new HashMap<>();
                    esWriteBean = new EsWriteBean();
                    esWriteBean.setIndex(index);
                    esWriteBean.setType("FZJC");
                    esWriteBean.setId(uid);
                    pam.put("aging_status","超时");
                    esWriteBean.setParams(pam);
                    esWriteService.update_field(esWriteBean);
                    up_number ++;

                    sendMessage.sendAlert(index,"alert",resMap);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        logger.info("---查询出: "+tempMap.size() +" 条数据，修改了："+up_number+" 条");
        return up_number;
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
        logger.info(JSON.toJSONString(resultMap));
        flag = (Boolean) resultMap.get("resultData");

        return flag;
    }

}
