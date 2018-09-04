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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({ "prod" })
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


    @Test
    public void test2(){
        try {
            String strDate = "2018-08-22 12:20:00";
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sd.parse(strDate));
            esWebService.aggAll_fileSizeCount(calendar.getTime(),-10, DiskUnit.UNIT_MB,1,"DS","采集");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}