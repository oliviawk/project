package com.hitec.repository.jpa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hitec.domain.DataInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataInfoRepositoryTest {

    @Autowired
    DataInfoRepository dataInfoRepository;

    @Test
    public void initData() {
    }

    @Test
    public void initSelected() {
        List<Object> list = dataInfoRepository.initSelected(0);
        for (Object obj : list){
//            Map<String,String> map = (Map<String, String>) obj;
//            JSONArray jsonArray = JSONObject.parseArray(JSON.toJSONString(obj));

            System.out.println(JSON.toJSONString(obj));
        }
    }

    @Test
    public void searchPz(){
        List<DataInfo> list = new ArrayList<>();

        for (Object obj : list){
            System.out.println(JSON.toJSONString(obj));
        }

    }


    @Test
    public void searchPz2(){
        List<DataInfo> list = new ArrayList<>();
        DataInfo di = dataInfoRepository.findAllById(30010001);

        if(di == null || di.getIs_data() == 0){
            dataInfoRepository.updateWhereIds(2,""+2400,30010001001L,30010001002L);
        }else{
            list.add(di);
        }

        for (Object obj : list){
            System.out.println(JSON.toJSONString(obj));
        }

    }
}