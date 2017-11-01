package com.cn.hitec.service;

import com.cn.hitec.tools.Pub;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @Description: 这里是描述信息
 * @author: fukl
 * @data: 2017年09月28日 15:39
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BoolTermQuery_ITest {
    @Autowired
    BoolTermQuery_I boolTermQueryI;
    @Test
    public void query() throws Exception {

        Map<String,Object> params = new HashMap<>();
        List<Map> list = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        map.put("name","last_time.keyword");
        map.put("lt", "2017-09-29 22:00:00.000+0800");
        list.add(map);
        params.put("range",list);
        params.put("fields.module.keyword","采集");
        params.put("type.keyword","satellite");
        params.put("sort","last_time.keyword");
        params.put("size","2");
        boolTermQueryI.query(new String[]{"log_20170929"},new String[]{"FZJC"},params);
    }

}