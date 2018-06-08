package com.hitec.repository.jpa;

import com.alibaba.fastjson.JSON;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args){
        List list = new ArrayList();
        String str = "{select4: \"-1\", select3: \"-1\", select2: \"3001\", select1: \"3\"}";
        Map<String,Integer> map = JSON.parseObject(str,Map.class);

        int select4 = 0,select3 = 0,select2 = 0,select1 = 0;

        if (map.containsKey("select1") ){
            select1 = map.get("select1");
        }

        if (map.containsKey("select2")){
            select2 = map.get("select2");
        }
        if (map.containsKey("select3")){
            select3 = map.get("select3");
        }
        if (map.containsKey("select4")){
            select4 = map.get("select4");
        }


        String sql = "select * from data_info where is_data = 1 ";
        if(select1 < 0){
            sql += " ";
        }else if(select2 < 0 ){

        }else if(select3 < 0){
            sql += " and parent_id = "+ select2;
        }
        System.out.println(sql);

    }
}
