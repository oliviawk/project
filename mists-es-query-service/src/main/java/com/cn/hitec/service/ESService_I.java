package com.cn.hitec.service;

import java.util.Map;

public interface ESService_I {

    /**
     * 普通查询方法
     * @param indices
     * @param types
     * @param moduleName
     * @param params
     * @throws Exception
     */
    public void  find_2(String[] indices, String[] types ,String moduleName,Map<String,Object> params) throws Exception;

}
