package com.cn.hitec.tools;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.text.SimpleDateFormat;
import java.util.*;

public class Pub {

    public static String cookie_rill = "";
    public static Map<String,String> IP_Map  = new HashMap<>();
    static {
        IP_Map.put("i-bp1fz0e0bz358yzpxx2k","120.26.9.109");
        IP_Map.put("i-bp1e6pq0gpyn5ludhb34","120.55.36.131");
    }

    public static String transform_DateToString(Date date , String simpleDataFormat) throws Exception{
        if(date == null){
            return "";
        }
        SimpleDateFormat sdf = null;
        if(StringUtils.isEmpty(simpleDataFormat)){
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }else {
            sdf = new SimpleDateFormat(simpleDataFormat);
        }
        return sdf.format(date);
    }

    public static Date transform_StringToDate(String strDate , String simpleDataFormat) throws  Exception{
        if(StringUtils.isEmpty(strDate)){
            return null;
        }
        SimpleDateFormat sdf = null;
        if(StringUtils.isEmpty(simpleDataFormat)){
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }else {
            sdf = new SimpleDateFormat(simpleDataFormat);
        }

        return sdf.parse(strDate);
    }


    /**
     * 获取随机字母数字组合
     *
     * @param length
     *            字符串长度
     * @return
     */
    public static String getRandomCharAndNumr(Integer length) {
        String str = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            boolean b = random.nextBoolean();
            if (b) { // 字符串
                // int choice = random.nextBoolean() ? 65 : 97; 取得65大写字母还是97小写字母
                str += (char) (97 + random.nextInt(26));// 取得小写字母
            } else { // 数字
                str += String.valueOf(random.nextInt(10));
            }
        }
        return str;
    }


    public static String login() {
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try {
            CookieStore cookieStore =new BasicCookieStore();
            httpClient = HttpClients.custom().setDefaultCookieStore((org.apache.http.client.CookieStore) cookieStore).build();
            httpPost = new HttpPost("http://10.30.17.182:80/adapter/login?username=ruijie&password=ruijie312&_openCLIENT=RIIL");
            List<NameValuePair> list = new ArrayList<org.apache.http.NameValuePair>();
//            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry<String, String> elem = (Map.Entry<String, String>) iterator.next();
//                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
//            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "");
                httpPost.setEntity(entity);
            }
            httpClient.execute(httpPost);
            String JSESSIONID = null;
            String sSOToken = null;
            List<Cookie> cookies = cookieStore.getCookies();
            for (int i = 0; i < cookies.size(); i++) {
                if (cookies.get(i).getName().equals("JSESSIONID")) {
                    JSESSIONID = cookies.get(i).getValue();
                }
                if (cookies.get(i).getName().equals("SSOToken")) {
                    sSOToken = cookies.get(i).getValue();
                }
            }
            if (sSOToken != null&&JSESSIONID !=null) {
                result = "JSESSIONID="+JSESSIONID+";"+"SSOToken="+sSOToken;

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

}
