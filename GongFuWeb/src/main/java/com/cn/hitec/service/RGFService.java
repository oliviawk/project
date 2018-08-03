package com.cn.hitec.service;

import com.cn.hitec.bean.EsQueryBean_web;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RGFService extends BaseService {


    public Map<String, Object> aggQuery(){
        return aggQuery(new String[]{"RGF"},"H8_NC,ELEH");
    }


    public Map<String, Object> getHistorys(EsQueryBean_web esQueryBean){
        return getHistory(esQueryBean);
    }

}
