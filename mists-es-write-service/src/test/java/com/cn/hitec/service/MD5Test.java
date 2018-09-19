package com.cn.hitec.service;

import com.cn.hitec.tools.Pub;

/**
 * @description: 描述信息
 * @author: fukl
 * @data: 2018年08月15日 上午9:30
 */
public class MD5Test {
    public static void main(String[] args ){
        String type  = "DATASOURCE";
        String subType = "海平面海洋天气预报"; // 数据名称
        String subModule = "DS";
        String subIp = "10.0.122.155";
        String subKey = type+","+subType+","+subModule+","+subIp;
        String str_id = Pub.MD5(subKey + "," + "2018-08-15 00:00:00.000+0800");
        System.out.println(str_id);
    }
}
