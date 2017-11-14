package com.cn.hitec.aly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.util.Pub;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @Description: 阿里云签名算法
 * @author: fukl
 * @data: 2017年11月07日 16:35
 */
public class SignatureUtils {
    private static Logger logger = Logger.getLogger(SignatureUtils.class);
    private final static String CHARSET_UTF8 = "utf8";
    private final static String ALGORITHM = "UTF-8";
    private final static String SEPARATOR = "&";

    public static Map<String, String> splitQueryString(String url)
            throws URISyntaxException, UnsupportedEncodingException {
        URI uri = new URI(url);
        String query = uri.getQuery();
        final String[] pairs = query.split("&");
        TreeMap<String, String> queryMap = new TreeMap<String, String>();
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? pair.substring(0, idx) : pair;
            if (!queryMap.containsKey(key)) {
                queryMap.put(key, URLDecoder.decode(pair.substring(idx + 1), CHARSET_UTF8));
            }
        }
        return queryMap;
    }
    public static String generate(String method, Map<String, String> parameter,
                                  String accessKeySecret) throws Exception {
        String signString = generateSignString(method, parameter);
        System.out.println("signString---"+signString);
        byte[] signBytes = hmacSHA1Signature(accessKeySecret + "&", signString);
        String signature = newStringByBase64(signBytes);
        System.out.println("signature---"+signature);
        if ("POST".equals(method))
            return signature;
        return URLEncoder.encode(signature, "UTF-8");
    }


    public static String generateSignString(String httpMethod, Map<String, String> parameter)
            throws IOException {
        TreeMap<String, String> sortParameter = new TreeMap<String, String>();
        sortParameter.putAll(parameter);
        String canonicalizedQueryString = generateQueryString(sortParameter, true);
        if (null == httpMethod) {
            throw new RuntimeException("httpMethod can not be empty");
        }
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append(httpMethod).append(SEPARATOR);
        stringToSign.append(percentEncode("/")).append(SEPARATOR);
        stringToSign.append(percentEncode(canonicalizedQueryString));
        return stringToSign.toString();
    }
    public static String percentEncode(String value) {
        try {
            return value == null ? null : URLEncoder.encode(value, CHARSET_UTF8)
                    .replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
        } catch (Exception e) {
        }
        return "";
    }
    public static byte[] hmacSHA1Signature(String secret, String baseString)
            throws Exception {
        if (StringUtils.isEmpty(secret)) {
            throw new IOException("secret can not be empty");
        }
        if (StringUtils.isEmpty(baseString)) {
            return null;
        }
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(CHARSET_UTF8), ALGORITHM);
        mac.init(keySpec);
        return mac.doFinal(baseString.getBytes(CHARSET_UTF8));
    }
    public static String newStringByBase64(byte[] bytes)
            throws UnsupportedEncodingException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return new String(Base64.encodeBase64(bytes, false), CHARSET_UTF8);
    }



    public static String encode(String value) {
        if (!StringUtils.isEmpty(value)) {
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (Exception e) {
                logger.warn("Url encode error:" + e.getMessage());
            }
        }
        return value;
    }
    public static String generateQueryString(Map<String, String> params, boolean isEncodeKV) {
        StringBuilder canonicalizedQueryString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (isEncodeKV)
                canonicalizedQueryString.append(percentEncode(entry.getKey())).append("=")
                        .append(percentEncode(entry.getValue())).append("&");
            else
                canonicalizedQueryString.append(entry.getKey()).append("=")
                        .append(entry.getValue()).append("&");
        }
        if (canonicalizedQueryString.length() > 1) {
            canonicalizedQueryString.setLength(canonicalizedQueryString.length() - 1);
        }
        return canonicalizedQueryString.toString();
    }


    public static String getData(String Url) throws IOException {
        URL url = new URL(Url);
        // 创建http链接对象
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // 设置请求方式
        con.setRequestMethod("GET");
        // 添加请求参数
//        con.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
//        con.addRequestProperty("Cookie",
//                "JSESSIONID=2qd6f2lxw8eiihr0dkvo6ow9;SSOToken=uKtZKmB1Iyl+6gRPJ52mTFdnelTXiHHBtiOr/D0zQUReGnAWBuAv0Q==");
        // 打开链接,上一步和该步骤作用相同，可以省略
        con.connect();

        // 获取请求返回内容并设置编码为UTF-8
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        // 将返回数据拼接为字符串
        StringBuffer sb = new StringBuffer();
        // 临时字符串
        String temp = null;
        // 获取数据
        while ((temp = reader.readLine()) != null) {
            sb.append(temp);
        }
        // 关闭流
        reader.close();
        return sb.toString();
    }


    public static void main(String[] args) {
//        //            GET /   AccessKeyId = CdwKFNmXeHJuMOrT & Action = DescribeInstances & Format = JSON & RegionId = cn-hangzhou & SignatureMethod = HMAC-SHA1 & SignatureNonce = 9fdf20f2-9a32-4872-bcd4-c6036082ebef & SignatureVersion = 1.0 & Timestamp = 2015-12-21T09 :   05 : 44Z   & Version = 2014-05-26
//        String str = "GET&%2F&AccessKeyId%3DCdwKFNmXeHJuMOrT%26Action%3DDescribeInstances%26Format%3DJSON%26RegionId%3Dcn-hangzhou%26SignatureMethod%3DHMAC-SHA1%26SignatureNonce%3D9fdf20f2-9a32-4872-bcd4-c6036082ebef%26SignatureVersion%3D1.0%26Timestamp%3D2015-12-21T09%253A05%253A44Z%26Version%3D2014-05-26";
//        byte[] signBytes;
//        try {
//            signBytes = SignatureUtils.hmacSHA1Signature("byczfpx4PKBzUNjjL4261cE3s6HQmH" + "&", str.toString());
//            String signature = SignatureUtils.newStringByBase64(signBytes);
//            System.out.print(signature);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        metrics.cn-beijing.aliyuncs.com

        //生成日历插件， 计算出 GMT 时间
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY,-8);
        Date timestamp = calendar.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar.setTime(date);
        calendar2.add(Calendar.HOUR_OF_DAY,-9);
        Date stratTime = calendar2.getTime();

        //下面两个不能分享给别人，我的账号会被攻击的
        String accessKeyId = "kEhKT1SrhTBpCzmO";
        String accessKeySecret = "PERiVIpbepuCQ6mDdydJWVP8Yr1uxR";

        try {
            String dimensions = "[{instanceId:'i-bp1e6pq0gpyn5ludhb34'},{instanceId:'i-bp1e47qt4g9ysdj9hsro'}]";
            String url = "http://metrics.cn-beijing.aliyuncs.com/" +
                    "?Action=QueryMetricList" +
                    "&Project=acs_ecs_dashboard" +
                    "&Metric=CPUUtilization" +
                    "&Period=60" +

                    "&Dimensions=" + percentEncode(dimensions) +
                    "&StartTime="+Pub.transform_DateToString(stratTime,"YYYY-MM-dd'T'hh:mm:ss'Z'")+
                    "&Timestamp=" + Pub.transform_DateToString(timestamp,"YYYY-MM-dd'T'hh:mm:ss'Z'")+
                    "&SignatureVersion=1.0" +
//                    beq55861-611f-43c6-9c07-t352efd3hfrw
                    "&SignatureNonce=1w243" +
                    "&Format=JSON" +
                    "&Version=2017-03-01" +
                    "&AccessKeyId=" +accessKeyId+
                    "&SignatureMethod=HMAC-SHA1";
            Map<String,String> queryMap = SignatureUtils.splitQueryString(url);
            String stringToSign = SignatureUtils.generate("GET",queryMap,accessKeySecret);
            System.out.println(stringToSign);

            String url_new = url + "&Signature=" + stringToSign;

            System.out.println("调用地址："+url_new);

            String data  =  getData(url_new);
            Map<String,Object> mapData = JSON.parseObject(data);
            for (String key : mapData.keySet()){
                System.out.println(mapData.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}