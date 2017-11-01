package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.repository.ESRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ESWebServiceTest {

    @Autowired
    ESRepository esRepository;
    @Autowired
    ESWebService esWebService;

    @Test
    public void test1() throws Exception{
        String[] indices = new String[]{""};
        indices[0] = "log_20170911";
        String[] types = new String[]{""};
        types[0] = "FZJC";
        Map<String,Object> map = new HashMap<>();
        map.put("fields.module.keyword","加工");
        Map<String,Object> resultMap = esWebService.find_AggTerms(indices,types,map);
        System.out.println(JSON.toJSONString(resultMap));
        esRepository.closeClient();
    }
}