package com.cn.hitec.service;

import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.domain.EsBean;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.feign.client.GongFuWrite;
import com.cn.hitec.tools.HttpPub;
import com.cn.hitec.tools.IpList;
import com.cn.hitec.tools.Pub;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GongFuService {

    private static final Logger logger = LoggerFactory.getLogger(GongFuService.class);

    @Autowired
    GongFuWrite gongFuWrite;
    @Autowired
    SendAlertMessage sendMessage;
    @Autowired
    EsQueryService esQueryService;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * @param
     * @return
     * @Description:CPU和内存信息入ES库
     */
    public void writeData() {
        int totalPage = 1;
        for (int i = 1; i <= totalPage; i++) {
            String url = "http://10.30.17.182:80/adapter/res/list.json?treeNodeId=00&pageIndex=" + i;
            String data = null;
            JSONObject jsonObject = null;
            try {
                // 调用接口， 获取所有数据
                data = getData(url, "GET", null, null);
                // 拿到总页数
                int t = JSONObject.fromObject(data).getInt("totalPage");
                if (totalPage == 1) {
                    totalPage = t;
                }
                List<String> list = IpList.getIpList(); // 得到需要监控的IP列表
                try {
                    // 转换数据开始
                    jsonObject = JSONObject.fromObject(data);
                    JSONArray jsonArray = jsonObject.getJSONArray("resList");
                    // logger.info("jsonArray大小:"+jsonArray.size());
                    for (Object object : jsonArray) {
                        JSONObject jsonObj = (JSONObject) object;
                        // 当前资源没有CPU或内存占用信息则跳过
                        // 有的IP在锐捷监控里有多个监控(比如PING监控和服务器监控),跳过可防止错误数据入库
                        if ("-".equals(jsonObj.getString("cpuRate")) || "-".equals(jsonObj.getString("memRate"))) {
                            continue;
                        }
                        String ip = jsonObj.getString("ip");
                        // logger.info("ip:"+ip);
                        if (list.contains(ip)) {
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
                            // l.add(resultNetState.toString());
                            String index = "data_" + sdf.format(System.currentTimeMillis());
                            EsBean e = new EsBean(index, "FZJC", null, l);
                            try {
                                // logger.info("数据:"+e.toString());
                                Map<String, Object> map = gongFuWrite.add(e);
                                logger.info("Cpu Mem map:" + map);
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

    public void findDiskData() {
        try {
            // 00.01.01是Windows主机
            // 01.01.05是Linux主机
            // 这个ID是通过/adapter/res/client/getResTypes4Json.json接口获取的
            List<String> url_parent_list = new ArrayList<String>();
            url_parent_list.add(
                    "http://10.30.17.182:80/adapter/res/client/getSubResInstances4Json/00.01.01/RIIL_RMT_CHILD_FILESYSTEM.json");
            url_parent_list.add(
                    "http://10.30.17.182:80/adapter/res/client/getSubResInstances4Json/00.01.05/RIIL_RMT_CHILD_FILESYSTEM.json");
            Iterator<String> itUrlParent = url_parent_list.iterator();
            while (itUrlParent.hasNext()) {
                String url_parent = itUrlParent.next();
                String responseParent = getData(url_parent, "GET", null, null);

                // 完整的返回结果
                JSONObject jb = JSONObject.fromObject(responseParent);
                // JSON里边的一层
                JSONObject jc = JSONObject.fromObject(jb.getString("getSubResInstances4JsonResult"));
                // JSON数组的每一个元素为一台服务器的全部磁盘信息
                JSONArray ja = jc.getJSONArray("subResInstances");
                List<String> listIp = IpList.getIpList(); // 得到需要监控的IP列表

                Map<String, JSONArray> dataMap = new HashMap<>();

                for (Object object : ja) {
                    JSONObject jsb = (JSONObject) object;
                    String ip = jsb.getString("ip");
                    if (listIp.contains(ip)) {
                        // JSON数组的每一个元素为一个磁盘的信息
                        JSONArray job = jsb.getJSONArray("subResInstVOList");
                        dataMap.put(ip, job);
                        // System.out.println(job.toString());
                    }
                }
                List<String> list = new ArrayList<>();
                for (String ipKey : dataMap.keySet()) {
                    for (Object obj : dataMap.get(ipKey)) {
                        try {
                            JSONObject je = (JSONObject) obj;
                            String url_son = je.getString("subInstId");

                            String name = je.getString("name");
                            if (name.startsWith("/dev/sdb1")) {
                                continue;
                            }

                            Map<String, Object> map = new HashMap<>();
                            Map<String, String> fields = new HashMap<>();

                            map.put("type", "BaseResource");
                            map.put("occur_time", System.currentTimeMillis());
                            map.put("receive_time", System.currentTimeMillis());
                            fields.put("path", name);
                            fields.put("ip", ipKey);
                            fields.put("data_time", sdf2.format(System.currentTimeMillis()));
                            fields.put("metric", "system.disk_usage");

                            String url_child = "http://10.30.17.182:80/adapter/res/getSubResMetricList.json?resId="
                                    + url_son;
                            String responseChild = getData(url_child, "POST", null, null);
                            // logger.info(url_child);
                            JSONObject jf = JSONObject.fromObject(responseChild);
                            JSONArray jg = jf.getJSONArray("resMetricList");
                            for (Object ob : jg) {
                                JSONObject jk = (JSONObject) ob;
                                // System.out.println(jk.toString());
                                if (jk.getString("metricName").equals("总大小")) {
                                    if (jk.containsKey("metricValue")) {
                                        String saveTotal = String
                                                .valueOf(Double.parseDouble(jk.getString("metricValue")) * 1024);
                                        fields.put("total", saveTotal);
                                    } else {
                                        logger.error("获取不到" + ipKey + "的磁盘信息，请检查锐捷监控");
                                        fields.put("total", "0");
                                    }
                                } else if (jk.getString("metricName").equals("已用大小")) {
                                    fields.put("used", jk.getString("metricValue"));
                                }
                            }
                            map.put("fields", fields);
                            // System.out.println(JSONObject.fromObject(map).toString());
                            list.add(JSONObject.fromObject(map).toString());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                String index = "data_" + sdf.format(System.currentTimeMillis());
                EsBean e = new EsBean(index, "FZJC", null, list);
                Map<String, Object> map = gongFuWrite.add(e);
                logger.info("Disk map:" + map);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    // 获取(告警)事件列表
    public void findEventData() {
        try {
            String url_parent = "http://10.30.17.182:80/adapter/event/client/getEventList.json";

            // List<String> listIp = IpList.getEventIpList(); // 得到需要监控的IP列表
            List<String> listIp = IpList.getIpList(); // 得到需要监控的IP列表
            Map<String, JSONObject> dataMap = new HashMap<>();

            //Iterator<String> itIp = listIp.iterator();
            //String requestStr = "viewType=unaccepted_event_view&resIp=" + itIp.next();
            int totalPage = 1;
            int totalCount = 0;
            int eventCount = 0;
            int unsentDataCount = 0;
            int processEventCount = 0;
            for (int i = 1; i <= totalPage; i++) {
                //System.out.println("###############" + eventCount);
                String requestStr = "viewType=unaccepted_event_view&isPageing=1&pageSize=100&pageIndex" + i;
                String responseParent = getData(url_parent, "POST", requestStr, "form");
                // 获取完整的json
                JSONObject jb = JSONObject.fromObject(responseParent);
                // 获取子eventList,里边是该IP返回的所有事件
                if (totalPage == 1) {
                    totalPage = jb.getInt("totalPage");
                    totalCount = jb.getInt("totalCount");
                    //System.out.println(jb.getInt("totalPage") + "----------" + jb.getInt("totalCount"));
                }
                JSONArray ja = jb.getJSONArray("eventList");
                for (Object object : ja) {
                    eventCount++;
                    JSONObject jsb = (JSONObject) object;
                    // 20180326 锐捷现在不监控进程了,跳过
                    if ("进程".equals(jsb.get("resType"))) {
                        processEventCount++;
                        continue;
                    }
                    String ip = jsb.getString("ip");
                    dataMap.put(ip, jsb);

                    List<String> list = new ArrayList<>();

                    // 把事件信息里的时间提取出来
                    String index = "data_" + sdf.format(jsb.getLong("time"));
                    // 查询这条告警在库中有没有
                    EsQueryBean esQueryBean = new EsQueryBean();
                    esQueryBean.setIndices(new String[]{index});
                    esQueryBean.setTypes(new String[]{"iaasAlert"});
                    Map<String, Object> queryParams = new HashMap<>();
                    Map<String, Object> queryMap = new HashMap<>();
                    queryMap.put("eventId", jsb.getString("eventId"));
                    queryParams.put("eventId", jsb.getString("eventId"));
                    esQueryBean.setParameters(queryParams);
                    Map<String, Object> responseMap = esQueryService.getData(esQueryBean);
                    // 如果查询返回结果里的resultData是空的,就入库
                    if (JSONArray.fromObject(responseMap.get("resultData")).isEmpty()) {
                        list.add(jsb.toString());
                        EsBean e = new EsBean(index, "iaasAlert", null, list);
                        Map<String, Object> map = gongFuWrite.add(e);
                        logger.info("map:" + map);
                        // 微信告警
                        StringBuffer alertTitle = new StringBuffer();
                        //alertTitle.append("[这条是测试告警,请忽略]");
                        alertTitle.append("基础设施告警: ");
                        alertTitle.append(jsb.get("ip"));
                        alertTitle.append(" ");
                        alertTitle.append(jsb.get("name"));
                        alertTitle.append(System.getProperty("line.separator"));
                        alertTitle.append("发生时间：");
                        alertTitle.append(jsb.get("createTime"));
                        alertTitle.append(System.getProperty("line.separator"));
                        alertTitle.append("资源类型：");
                        alertTitle.append(jsb.get("resType"));
                        alertTitle.append(jsb.get("metricName"));
                        alertTitle.append(System.getProperty("line.separator"));
                        alertTitle.append("资源名称：");
                        alertTitle.append(jsb.get("resName"));

                        //如果是监控流程图中的IP,则额外发送告警到全流程监控组
                        if (listIp.contains(ip)) {
                            logger.info("发送告警:" + alertTitle.toString());
                            sendMessage.sendAlert(alertTitle.toString(), "@all");
                        }
                        // 自己调用微信接口和短信接口发送全部告警
                        unsentDataCount++;
                        HttpPub httpPub = new HttpPub();
                        httpPub.httpPost("@all", alertTitle.toString(), "1000009");
                        httpPub.sendSms("13810933845|18210780238|13810168659|18600063404", alertTitle.toString(), "");
                    }
                }
            }
            logger.info("findEventData result: totalCount:" + totalCount +
                    " eventCount:" + eventCount +
                    " unsentDataCount:" + unsentDataCount +
                    " processEventCount:" + processEventCount);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        //Pub.cookie_rill = Pub.login();
        //GongFuService gongFuService = new GongFuService();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        System.out.println(sdf.format(Long.parseLong("1528176377000")));
        //gongFuService.findDiskData();
        // String url_parent =
        // "http://10.30.17.182:80/adapter/event/client/getEventList.json";
        // String responseParent = getData(url_parent, "POST",
        // "viewType=unaccepted_event_view&resIp=10.30.16.223","form");
        // String responseParent = getData(url_parent, "POST", null);
        // logger.info(responseParent);
        // GongFuService gongFuService = new GongFuService();
        // gongFuService.findEventData();
    }

    public void findNetdate() {
        try {
            // 00.01.01是Windows主机
            // 01.01.05是Linux主机
            // 这个ID是通过/adapter/res/client/getResTypes4Json.json接口获取的
            List<String> url_parent_list = new ArrayList<String>();
            url_parent_list.add(
                    "http://10.30.17.182:80/adapter/res/client/getSubResInstances4Json/00.01.01/RIIL_RMT_CHILD_NIC.json");
            url_parent_list.add(
                    "http://10.30.17.182:80/adapter/res/client/getSubResInstances4Json/00.01.05/RIIL_RMT_CHILD_NIC.json");
            Iterator<String> itUrlParent = url_parent_list.iterator();
            while (itUrlParent.hasNext()) {
                String url_parent = itUrlParent.next();
                String responseParent = getData(url_parent, "GET", null, null);
                JSONObject jb = JSONObject.fromObject(responseParent);
                JSONObject jc = JSONObject.fromObject(jb.getString("getSubResInstances4JsonResult"));
                JSONArray ja = jc.getJSONArray("subResInstances");
                List<String> listIp = IpList.getIpList(); // 获取IP列表

                Map<String, JSONArray> dataMap = new HashMap<>();

                for (Object object : ja) {
                    JSONObject jsb = (JSONObject) object;
                    String ip = jsb.getString("ip");
                    if (listIp.contains(ip)) {
                        JSONArray job = jsb.getJSONArray("subResInstVOList");
                        dataMap.put(ip, job);
                    }
                }
                List<String> list = new ArrayList<>();
                for (String ipKey : dataMap.keySet()) {
                    for (Object obj : dataMap.get(ipKey)) {
                        try {
                            JSONObject je = (JSONObject) obj;

                            // System.out.println(je.toString());
                            String url_son = je.getString("subInstId");

                            Map<String, Object> map = new HashMap<>();
                            Map<String, String> fields = new HashMap<>();

                            map.put("type", "BaseResource");
                            map.put("occur_time", System.currentTimeMillis());
                            map.put("receive_time", System.currentTimeMillis());
                            fields.put("device", je.getString("name"));
                            fields.put("ip", ipKey);
                            fields.put("data_time", sdf2.format(System.currentTimeMillis()));
                            fields.put("metric", "system.net_state");
                            fields.put("metricunit", "Kbps");

                            String url_child = "http://10.30.17.182:80/adapter/res/getSubResMetricList.json?resId="
                                    + url_son;
                            String responseChild = getData(url_child, "POST", null, null);
                            JSONObject jf = JSONObject.fromObject(responseChild);
                            JSONArray jg = jf.getJSONArray("resMetricList");
                            for (Object ob : jg) {
                                JSONObject jk = (JSONObject) ob;
                                if (jk.getString("metricName").equals("接收速率")) {
                                    fields.put("in", jk.getString("metricValue"));
                                } else if (jk.getString("metricName").equals("发送速率")) {
                                    fields.put("out", jk.getString("metricValue"));
                                }
                            }
                            map.put("fields", fields);
                            // System.out.println(JSONObject.fromObject(map).toString());
                            list.add(JSONObject.fromObject(map).toString());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                // logger.info("网络接口数据：list.size:"+list.size());
                // logger.info(JSONArray.fromObject(list).toString());
                String index = "data_" + sdf.format(System.currentTimeMillis());
                EsBean e = new EsBean(index, "FZJC", null, list);
                Map<String, Object> map = gongFuWrite.add(e);
                logger.info("Net map:" + map);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    public static String getData(String Url, String method, String body, String contentType) throws IOException {
        // 统一资源
        URL url = new URL(Url);

        // 判断是否有http正文提交
        boolean isDoInput = false;
        if (body != null && body.length() > 0)
            isDoInput = true;
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader responseReader = null;

        // 获取状态码返回数据
        StringBuffer resultBuffer = new StringBuffer();

        try {
            // 创建http链接对象
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // 设置请求方式
            con.setRequestMethod(method);
            // 添加请求参数
            if ("json".equals(contentType)) {
                con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            } else if ("form".equals(contentType)) {
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }
            con.setRequestProperty("Cookie", Pub.cookie_rill);
            // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true,
            // 默认情况下是false;
            if (isDoInput) {
                // 设置是否向httpURLConnection写入内容
                // post请求必须设置为true,因为post请求参数写在http正文中
                con.setDoOutput(true);
                // 设置是否使用缓存，post请求不使用缓存
                con.setUseCaches(false);
                con.setRequestProperty("Content-Length", String.valueOf(body.length()));
            }
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            // 如果在已打开连接（此时 connected 字段的值为 true）的情况下调用 connect 方法，则忽略该调用。
            // con.connect();
            if (isDoInput) {
                outputStream = con.getOutputStream();
                outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(body);
                outputStreamWriter.flush();// 刷新
            }
            int resultCode = con.getResponseCode();
            if (HttpURLConnection.HTTP_OK == resultCode) {
                String readLine = "";
                responseReader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                while ((readLine = responseReader.readLine()) != null) {
                    resultBuffer.append(readLine);
                }
            } else {
                logger.error(resultCode + "-" + con.getResponseMessage());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {// 关闭流
            try {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (responseReader != null) {
                    responseReader.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return resultBuffer.toString();
    }


}
