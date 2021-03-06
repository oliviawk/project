package com.cn.hitec.tools;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
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
@Component
public class HttpPub {
    @Value("${wechat.url}")
    private String url;

    /**
     * post请求
     *
     * @param users     发送的联系人 ， 多人用 | 隔开， 所有用 @all
     * @param jsonParam 告警内容
     * @param agentid   发送对象
     * @return
     */
    public static Map<String, Object> httpPost(String users, String jsonParam, String agentid) {
        // post请求返回结果
        // String url = "http://10.16.41.126:9999/ws/sendwx/1/2";
        String url = "http://10.14.82.102:8999/message/send/1/2";
        HttpClient httpClient = HttpClients.createDefault();
        Map<String, Object> resultMap = new HashMap<>();
        HttpPost method = new HttpPost(url);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("safe", "0");
            params.put("totag", "");
            params.put("msgtype", "text");
            params.put("touser", users);
            // params.put("agentid", "1000004");
            params.put("agentid", agentid);
            params.put("toparty", "");
            Map<String, String> alertMessage = new HashMap<>();
            alertMessage.put("content", jsonParam);
            params.put("text", alertMessage);

            if (null != jsonParam) {
                // 解决中文乱码问题
                StringEntity entity = new StringEntity(JSON.toJSONString(params), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /** 请求发送成功，并得到响应 **/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    /** 读取服务器返回过来的json字符串数据 **/
                    str = EntityUtils.toString(result.getEntity());
                    /** 把json字符串转换成json对象 **/
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

    /**
     * 发送短信
     *
     * @param users     发送的联系人 ， 多人用 | 隔开， 所有用 @all
     * @param jsonParam 告警内容
     * @param agentid   发送对象
     * @return
     */
    public static Map<String, Object> sendSms(String users, String jsonParam, String agentid) {
        // post请求返回结果
        // String url = "http://10.16.41.126:9999/ws/sendwx/1/2";
        String url = "http://10.14.82.102:8787/sendsms/1/1";
        HttpClient httpClient = HttpClients.createDefault();
        Map<String, Object> resultMap = new HashMap<>();
        HttpPost method = new HttpPost(url);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("username", "jiankong");
            params.put("password", "jiankong123");
            //params.put("safe", "0");
            //params.put("totag", "");
            //params.put("msgtype", "text");
            params.put("touser", users);
            // params.put("agentid", "1000004");
            params.put("agentid", agentid);
            //params.put("toparty", "");
            Map<String, String> alertMessage = new HashMap<>();
            alertMessage.put("content", jsonParam);
            params.put("text", alertMessage);

            if (null != jsonParam) {
                // 解决中文乱码问题
                StringEntity entity = new StringEntity(JSON.toJSONString(params), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /** 请求发送成功，并得到响应 **/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    /** 读取服务器返回过来的json字符串数据 **/
                    str = EntityUtils.toString(result.getEntity());
                    /** 把json字符串转换成json对象 **/
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

    public static void main(String[] args) {
        HttpPub httpPub = new HttpPub();
        //httpPub.httpPost("caoyuzhao", "LAPS质检消息测试-LAPS质检组发送", "1000004");
        httpPub.sendSms("13810933845|18210780238|13810168659|18600063404", "[这条是测试告警,请忽略]基础设施告警: 10.16.8.52 (网络接口)不可用(操作状态Down)\n" +
                "发生时间：2018-06-01 20:58:09\n" +
                "资源类型：网络接口状态\n" +
                "资源名称：5层机房13网段_G1/0/9", "");
    }
}
