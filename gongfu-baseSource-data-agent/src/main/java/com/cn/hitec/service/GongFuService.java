package com.cn.hitec.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import com.cn.hitec.tools.Pub;
import net.sf.json.JSON;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.hitec.domain.EsBean;
import com.cn.hitec.feign.client.GongFuWrite;
import com.cn.hitec.tools.IpList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class GongFuService {

    private static final Logger logger = LoggerFactory.getLogger(GongFuService.class);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    @Autowired
    private GongFuWrite gongFuWrite;

    public void writeData() {
        int totalPage = 1;
        for (int i = 0; i < totalPage; i++) {
            String url = "http://10.30.17.182:80/adapter/res/list.json?treeNodeId=00&pageIndex=" + i;
            String data = null;
            JSONObject jsonObject = null;
            try {
                //调用接口， 获取所有数据
                data = getData(url,"GET");
                //拿到总页数
                int t = JSONObject.fromObject(data).getInt("totalPage");
                if(totalPage==1){
                    totalPage = t;
                }
                List<String> list = IpList.getIpList();     //得到需要监控的IP列表
                try {
                    //转换数据开始
                    jsonObject = JSONObject.fromObject(data);
                    JSONArray jsonArray = jsonObject.getJSONArray("resList");
//                    logger.info("jsonArray大小:"+jsonArray.size());
                    for (Object object : jsonArray) {
                        JSONObject jsonObj = (JSONObject)object;
                        String ip = jsonObj.getString("ip");
//                        logger.info("ip:"+ip);
                        if(list.contains(ip)){
                            JSONObject fieldsCpu = new JSONObject();
                            JSONObject resultCpu = new JSONObject();
                            fieldsCpu.put("metric", "system.cpu.pct_usage");
                            fieldsCpu.put("ip", ip);
                            fieldsCpu.put("data_time", sdf2.format(System.currentTimeMillis()));
                            fieldsCpu.put("value", jsonObj.getString("cpuRate"));
                            resultCpu.put("type", "BaseResource");
                            resultCpu.put("occur_time", System.currentTimeMillis());
                            resultCpu.put("receive_time", System.currentTimeMillis());
                            resultCpu.put("fields", fieldsCpu);

                            JSONObject fieldsMem = new JSONObject();
                            JSONObject resultMem = new JSONObject();
                            fieldsMem.put("metric", "system.memory.pct_usage");
                            fieldsMem.put("ip", ip);
                            fieldsMem.put("data_time", sdf2.format(System.currentTimeMillis()));
                            fieldsMem.put("value", jsonObj.getString("memRate"));
                            resultMem.put("type", "BaseResource");
                            resultMem.put("occur_time", System.currentTimeMillis());
                            resultMem.put("receive_time", System.currentTimeMillis());
                            resultMem.put("fields", fieldsMem);

                            List l = new ArrayList();
                            l.add(resultCpu.toString());
                            l.add(resultMem.toString());
//                            l.add(resultNetState.toString());
                            String index = "data_"+sdf.format(System.currentTimeMillis());
                            EsBean e = new EsBean(index, "FZJC", null, l);
                            try {
//                                logger.info("数据:"+e.toString());
                                Map<String, Object> map = gongFuWrite.add(e);
                                logger.info("map:"+map);
                            } catch (Exception e1) {
                                logger.info("调用接口出现异常!");
                                e1.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.info("接口返回数据格式有误!");
                    e.printStackTrace();
                }

            } catch (IOException e) {
                logger.info("请求接口出现异常!");
                e.printStackTrace();
            }


        }
    }

    public void findDiskData(){
        try {
            String url_parent = "http://10.30.17.182:80/adapter/res/client/getSubResInstances4Json/00.01.05/RIIL_RMT_CHILD_FILESYSTEM.json";
            String responseParent = getData(url_parent, "GET");

            JSONObject jb=JSONObject.fromObject(responseParent);
            JSONObject jc=JSONObject.fromObject(jb.getString("getSubResInstances4JsonResult"));
            JSONArray ja=jc.getJSONArray("subResInstances");
            List<String> listIp = IpList.getIpList();     //得到需要监控的IP列表

            Map<String,JSONArray> dataMap = new HashMap<>();

            for(Object object:ja){
                JSONObject jsb=(JSONObject) object;
                String ip = jsb.getString("ip");
                if(listIp.contains(ip)){
                    JSONArray job = jsb.getJSONArray("subResInstVOList");
                    dataMap.put(ip,job);
                    //			System.out.println(job.toString());
                }
            }
            List<String> list = new ArrayList<>();
            for (String ipKey : dataMap.keySet()){
                for (Object obj : dataMap.get(ipKey)){
                    try {
                        JSONObject je=(JSONObject) obj;
                        String url_son=je.getString("subInstId");

                        String name = je.getString("name");
                        if(name.startsWith("/dev/sdb1")){
                            continue;
                        }

                        Map<String,Object> map = new HashMap<>();
                        Map<String,String> fields = new HashMap<>();

                        map.put("type","BaseResource");
                        map.put("occur_time",System.currentTimeMillis());
                        map.put("receive_time",System.currentTimeMillis());
                        fields.put("path",name);
                        fields.put("ip",ipKey);
                        fields.put("data_time", sdf2.format(System.currentTimeMillis()));
                        fields.put("metric", "system.disk_usage");

                        String url_child = "http://10.30.17.182:80/adapter/res/getSubResMetricList.json?resId="+url_son;
                        String responseChild = getData(url_child, "POST");
                        JSONObject jf=JSONObject.fromObject(responseChild);
                        JSONArray jg=jf.getJSONArray("resMetricList");
                        for(Object ob:jg){
                            JSONObject jk=(JSONObject) ob;
                            if(jk.getString("metricName").equals("总大小")){
                                String saveTotal=String.valueOf(Double.parseDouble(jk.getString("metricValue"))*1024);
                                fields.put("total", saveTotal);
                            }else if(jk.getString("metricName").equals("已用大小")){
                                fields.put("used",jk.getString("metricValue"));
                            }
                        }
                        map.put("fields",fields);
//                        System.out.println(JSONObject.fromObject(map).toString());
                        list.add(JSONObject.fromObject(map).toString());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }


            String index = "data_"+sdf.format(System.currentTimeMillis());
            EsBean e = new EsBean(index, "FZJC", null, list);
            Map<String, Object> map = gongFuWrite.add(e);
            logger.info("map:"+map);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    public static void main(String[] args){
        GongFuService gongFuService = new GongFuService();
        gongFuService.findDiskData();
    }

    public void findNetdate(){
        try {
            String url_parent = "http://10.30.17.182:80/adapter/res/client/getSubResInstances4Json/00.01.05/RIIL_RMT_CHILD_NIC.json";
            String responseParent = getData(url_parent, "GET");
            JSONObject jb=JSONObject.fromObject(responseParent);
            JSONObject jc=JSONObject.fromObject(jb.getString("getSubResInstances4JsonResult"));
            JSONArray ja=jc.getJSONArray("subResInstances");
            List<String> listIp = IpList.getIpList();  //获取IP列表

            Map<String,JSONArray> dataMap = new HashMap<>();

            for(Object object:ja){
                JSONObject jsb=(JSONObject) object;
                String ip = jsb.getString("ip");
                if(listIp.contains(ip)){
                    JSONArray job = jsb.getJSONArray("subResInstVOList");
                    dataMap.put(ip,job);
                }
            }
            List<String> list = new ArrayList<>();
            for (String ipKey : dataMap.keySet()){
                for (Object obj : dataMap.get(ipKey)){
                    try {
                        JSONObject je=(JSONObject) obj;

                        //			System.out.println(je.toString());
                        String url_son=je.getString("subInstId");

                        Map<String,Object> map = new HashMap<>();
                        Map<String,String> fields = new HashMap<>();

                        map.put("type","BaseResource");
                        map.put("occur_time",System.currentTimeMillis());
                        map.put("receive_time",System.currentTimeMillis());
                        fields.put("device",je.getString("name"));
                        fields.put("ip",ipKey);
                        fields.put("data_time", sdf2.format(System.currentTimeMillis()));
                        fields.put("metric", "system.net_state");
                        fields.put("metricunit","Kbps");

                        String url_child = "http://10.30.17.182:80/adapter/res/getSubResMetricList.json?resId="+url_son;
                        String responseChild = getData(url_child, "POST");
                        JSONObject jf=JSONObject.fromObject(responseChild);
                        JSONArray jg=jf.getJSONArray("resMetricList");
                        for(Object ob:jg){
                            JSONObject jk=(JSONObject) ob;
                            if(jk.getString("metricName").equals("接收速率")){
                                fields.put("in",jk.getString("metricValue"));
                            }else if(jk.getString("metricName").equals("发送速率")){
                                fields.put("out",jk.getString("metricValue"));
                            }
                        }
                        map.put("fields",fields);
//                        System.out.println(JSONObject.fromObject(map).toString());
                        list.add(JSONObject.fromObject(map).toString());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

//            logger.info("网络接口数据：list.size:"+list.size());
//            logger.info(JSONArray.fromObject(list).toString());
            String index = "data_"+sdf.format(System.currentTimeMillis());
            EsBean e = new EsBean(index, "FZJC", null, list);
            Map<String, Object> map = gongFuWrite.add(e);
            logger.info("map:"+map);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }



    public static String getData(String Url,String method) throws IOException {
        URL url = new URL(Url);
        String param = "";
        // 创建http链接对象
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // 设置请求方式
        con.setRequestMethod(method);
        // 添加请求参数
        con.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.addRequestProperty("Cookie", Pub.cookie_rill);

        // 打开链接
//        con.connect();
        //获取状态码返回数据
        StringBuffer sb=new StringBuffer();

        int resultCode=con.getResponseCode();
        if(HttpURLConnection.HTTP_OK==resultCode){

            String readLine="";
            BufferedReader responseReader=new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
            while((readLine=responseReader.readLine())!=null){
                sb.append(readLine);
            }

            //关闭流
            responseReader.close();
        }else {
            logger.error(resultCode+"-"+con.getResponseMessage());
        }

        return sb.toString();
    }

}


