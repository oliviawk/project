package com.cn.hitec.service;

import com.cn.hitec.repository.ESRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description: bool Term 查询类
 * @author: fukl
 * @data: 2017年09月21日 17:02
 */
public interface BoolTermQuery_I {

    public List<Map> query_new(String[] indices, String[] types , Map<String,Object> params) throws Exception;

    public List<Map> query(String[] indices, String[] types , Map<String,Object> params) throws Exception;

    public Map query_resultId(String[] indices, String[] types, Map<String, Object> params) throws Exception;
}
