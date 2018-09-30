package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.tools.DiskUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({ "dev" })
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


//    @Test
//    public void test2(){
//        try {
//            String strDate = "2018-09-14 15:20:00";
//            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(sd.parse(strDate));
//            int min = calendar.get(Calendar.MINUTE);          //获取当前分钟
//            System.out.println(min);
//            int n = min/Math.abs(-10);
//            calendar.set(Calendar.MINUTE, n*10);
//
//            String unit = "m";
//            unit = DiskUnit.getUnit(unit);
//            System.out.println(unit);
//            List<Object> result  = esWebService.getFileSize(calendar.getTime(),-10,12, unit,1,"yyyy-MM-dd HH:mm","1");
//            System.out.println(JSON.toJSONString(result));
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            esRepository.closeClient();
//        }
//    }
}