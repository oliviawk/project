package com.cn.hitec.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.service.BigScreenWebService;
import com.cn.hitec.service.ESWebService;
import com.cn.hitec.tools.DiskUnit;
import com.cn.hitec.tools.Pub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 大屏展示接口提供
 * @author: fukl
 * @data: 2018年09月25日 上午9:55
 */
@RestController
@RequestMapping("/bigscreen")
public class BigScreenShowApi {
    private static final Logger logger = LoggerFactory.getLogger(BigScreenShowApi.class);

    @Autowired
    private BigScreenWebService webService;


    @RequestMapping(value = "/getFileSizeCount", method = RequestMethod.POST, consumes = "application/json")
    public List<Object> getFileSizeCount(@RequestBody String str){
        List<Object>  resultList= new ArrayList<>();
        try {
            JSONObject params = JSON.parseObject(str);
            Date dt = null ;
            String unit = DiskUnit.getUnit(params.getString("unit"));
            int timeGranularity = params.getInteger("timeGranularity");
            int t =  params.getInteger("t");
            int scale = params.getInteger("scale");
            String callBack_dateFormat = params.getString("callBackDateFormat");
            String dataType = params.getString("dataType");

            String strDate = params.getString("dateStr");
            String strFormat = params.getString("dateFormat");
            SimpleDateFormat sd = new SimpleDateFormat(strFormat);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sd.parse(strDate));
            int minute = calendar.get(Calendar.MINUTE);
            int n = minute / Math.abs(timeGranularity);
            calendar.set(Calendar.MINUTE,n*Math.abs(timeGranularity));
            dt = calendar.getTime();

//			System.out.println("1:"+ Pub.transform_DateToString(dt,callBack_dateFormat));
            resultList  = webService.getFileSize(dt,timeGranularity,t, unit,scale,callBack_dateFormat,dataType);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList ;
    }

    @RequestMapping(value = "/lctdata" , method = RequestMethod.GET)
    public Map<String, Object> getLCTData(){
        SimpleDateFormat sd = new SimpleDateFormat(Pub.Index_Food_Simpledataformat);
        String strDt = Pub.Index_Head+sd.format(new Date());

        Map<String,Object> resultMap = new HashMap<>();
        for (String strKey : Pub.dataMachiningMap.keySet()){

            Map<String,Object> tempMap_server = new HashMap<>();
            Pub.mapCopy(Pub.dataMachiningMap.get(strKey),tempMap_server);

//            logger.info("key:{} , 业务名称:{}",strKey, Pub.dataMachiningMap.get(strKey).get("serverName"));
            Map<String,Object> lctMap_caiji = webService.queryData_lct(new String[]{strDt} , strKey , "采集");

            Map<String,Object> lctMap_chuli = webService.queryData_lct_handle(new String[]{strDt} , strKey);

            Map<String,Object> lctMap_fenfa = webService.queryData_lct(new String[]{strDt} , strKey , "分发");

            if(strKey.equals("OCF")){
                lctMap_caiji.put("AGLB_OBS",lctMap_fenfa.get("AGLB_OBS"));
                lctMap_caiji.put("CH_OBS",lctMap_fenfa.get("CH_OBS"));
                lctMap_fenfa.remove("AGLB_OBS");
                lctMap_fenfa.remove("CH_OBS");
            }

            tempMap_server.put("caiji",lctMap_caiji);
            tempMap_server.put("chuli",lctMap_chuli);
            tempMap_server.put("fenfa",lctMap_fenfa);

            resultMap.put(strKey,tempMap_server);
        }
        logger.info("resultMap:"+JSON.toJSONString(resultMap));
        logger.info("\t->:"+JSON.toJSONString(Pub.dataMachiningMap));

        return resultMap;
    }



}
