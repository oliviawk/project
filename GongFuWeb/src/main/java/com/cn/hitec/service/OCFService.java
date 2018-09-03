package com.cn.hitec.service;

import com.cn.hitec.bean.EsQueryBean_web;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OCFService extends BaseService {


    public Map<String, Object> aggQuery(){
        return aggQuery(new String[]{"OCF"},"CH_MERGE_1H,AGLB_MERGE_1H,AGLB_1H,CH_1H,CH_MERGE_3H,AGLB_MERGE_3H,AGLB_3H,CH_3H,CH_MERGE_12H,AGLB_MERGE_12H,AGLB_12H,CH_12H,T639_NEW,NCEP_NEW,RJTD_NEW,ECMWF_NEW,OCF_FINAL,AGLB_MERGE_1H_DEBACKUP,AGLB_1H_DEBACKUP,CH_1H_DEBACKUP,CH_MERGE_1H_DEBACKUP,CH_MERGE_3H_DEBACKUP,AGLB_MERGE_3H_DEBACKUP,AGLB_3H_DEBACKUP,CH_3H_DEBACKUP,CH_MERGE_12H_DEBACKUP,AGLB_MERGE_12H_DEBACKUP,AGLB_12H_DEBACKUP,CH_12H_DEBACKUP,OCF_3H_UPDATE,OCF_3H_UPDATE,OCF_12H_UPDATE,OCF_JIANGJI,OCF1H_ME_L88_GLB,OCF3H_ME_L88_GLB,OCF12H_ME_L88_GLB,BHFK");
    }


    public Map<String, Object> getHistorys(EsQueryBean_web esQueryBean){

        return getHistory(esQueryBean);

    }
}
