package com.cn.hitec.service;

import com.cn.hitec.bean.EsQueryBean_web;

import java.util.Map;

/**
 * @description: 描述信息
 * @author: fukl
 * @data: 2018年07月23日 上午11:37
 */
public interface BaseServiceInterface {

    /**
     * 页面按照固定元素聚合查询
     * @param types
     * @param subType   "a,b,c"
     * @return
     */
    Map<String,Object> aggQuery(String [] types, String subType);


//    /**
//     * 按照参数，查询历史
//     * @param esQueryBean
//     * @return
//     */
//    Map<String, Object> getHistory(EsQueryBean_web esQueryBean);
}
