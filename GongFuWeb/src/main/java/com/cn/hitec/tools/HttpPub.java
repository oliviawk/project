package com.cn.hitec.tools;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 这里是描述信息
 * @author: fukl
 * @data: 2017年10月01日 14:39
 */
public class HttpPub {


    /**
     * get请求
     * @param url
     * @return
     */
    public static  Map<String, Object> getData(String url){

        //post请求返回结果
//        String url = "http://10.16.41.126:9999/ws/sendwx/1/2";
        HttpClient httpClient = HttpClients.createDefault();
        Map<String,Object> resultMap = new HashMap<>();
        HttpGet method = new HttpGet(url);

        try {

            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    /**读取服务器返回过来的json字符串数据**/
                    str = EntityUtils.toString(result.getEntity());
                    /**把json字符串转换成json对象**/
                    resultMap = JSON.parseObject(str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultMap;
    }


    public static void main(String[] args){

        Map<String,Object> resultMap =  getData("http://10.14.83.52:9000/monitor/present/radarbase");
        System.out.println(JSON.toJSONString(resultMap));
        Map<String,Object> resultMap2 =  getData("http://10.14.83.52:9000/monitor/present/sevpsnwfd");
        System.out.println(JSON.toJSONString(resultMap2));
        Map<String,Object> resultMap3 =  getData("http://10.14.83.52:9000/monitor/present/sevpscon");
        System.out.println(JSON.toJSONString(resultMap3));
        Map<String,Object> resultMap5 =  getData("http://10.14.83.52:9000/monitor/present/surfchnhorn");
        System.out.println(JSON.toJSONString(resultMap5));

        Map<String,Object> resultMap4 =  getData("http://10.30.17.171:8786/basesource/bigscreen");
        System.out.println(JSON.toJSONString(resultMap4));

    }
}
