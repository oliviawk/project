package com.cn.hitec.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.bean.D3NetBean;
import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.tools.DateTool;
import com.cn.hitec.tools.Pub;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 这里是描述信息
 * @author: fukl
 * @data: 2017年11月21日 15:08
 */
@Service
public class BasicResource {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BasicResource.class);

    @Autowired
    EsQueryService esQueryService;


    /**
     * 获取CPU信息
     *
     * @param ip
     * @return
     */
    public Object getCpuData(String ip, int minute) {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<Object> controlsData = new ArrayList<Object>();
        List list = new ArrayList();
        String data = getBaseSourceData(ip, "system.cpu.pct_usage", minute);
//        logger.info("data:"+data);
        JSONObject jsonObj = JSONObject.parseObject(data);
        JSONArray jsonArr = jsonObj.getJSONArray("resultData");
        for (Object object : jsonArr) {
            try {
                JSONObject obj = (JSONObject) object;

                JSONObject jsonData = new JSONObject();
                String str = obj.getJSONObject("fields").getString("value");
                //当锐捷监控取不到服务器指标时,value字段的值为"-",需要特殊处理
                if ("-".equals(str)) {
                    str = "0.01%";
                }
                String string = str.split("%")[0];
                String time = obj.getJSONObject("fields").getString("data_time");
                jsonData.put("used", Double.parseDouble(string));
                jsonData.put("free", 100 - Double.parseDouble(string));
                Date parse = sdf2.parse(time);
                jsonData.put("time", sdf2.format(parse));
                list.add(string);
                controlsData.add(jsonData);

            } catch (ParseException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
        return returnDataTransFormat(list, controlsData);

    }


    public Object getMemoryData(String host, int minute) {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        List<Object> controlsData = new ArrayList<Object>();

        List list = new ArrayList();

        List<String> ipListIn = Pub.getIpList_in();
        List<String> ipListOut = Pub.getIpList_out();
        if (ipListIn.contains(host)) {
            String data = getBaseSourceData(host, "system.memory.pct_usage", minute);
            JSONObject jsonObj = JSONObject.parseObject(data);
            JSONArray jsonArr = jsonObj.getJSONArray("resultData");
            for (Object object : jsonArr) {
                JSONObject obj = (JSONObject) object;
                String m = obj.getJSONObject("fields").getString("metric");
                if (m.contains("memory")) {
                    JSONObject jsonData = new JSONObject();
                    String str = obj.getJSONObject("fields").getString("value");
                    //当锐捷监控取不到服务器指标时,value字段的值为"-",需要特殊处理
                    if ("-".equals(str)) {
                        str = "0%";
                    }
                    String string = str.split("%")[0];
                    String time = obj.getJSONObject("fields").getString("data_time");
                    jsonData.put("used", Double.parseDouble(string));
                    jsonData.put("free", 100 - Double.parseDouble(string));
                    try {
                        Date parse = sdf2.parse(time);
                        jsonData.put("time", sdf2.format(parse));
                        list.add(string);
                        controlsData.add(jsonData);
                    } catch (ParseException e) {
                        logger.info("时间格式解析错误!");
                        e.printStackTrace();
                    }

                }
            }
        } else if (ipListOut.contains(host)) {
            String data = getBaseSourceData(host, "system.memory_usage", minute);
            JSONObject jsonObj = JSONObject.parseObject(data);
            JSONArray jsonArr = jsonObj.getJSONArray("resultData");
            for (Object object : jsonArr) {
                try {
                    JSONObject obj = (JSONObject) object;
                    String m = obj.getJSONObject("fields").getString("metric");
                    if (m.contains("memory")) {
                        JSONObject jsonData = new JSONObject();
                        double free = obj.getJSONObject("fields").getDouble("free");
                        double total = obj.getJSONObject("fields").getDouble("total");
                        String time = obj.getJSONObject("fields").getString("data_time");
                        String string = decimalFormat.format(100 - (free / total * 100));
                        jsonData.put("used", 100 - Double.parseDouble(string));
                        jsonData.put("free", Double.parseDouble(string));
                        Date parse = sdf2.parse(time);
                        jsonData.put("time", sdf2.format(parse));
                        list.add(string);
                        controlsData.add(jsonData);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return returnDataTransFormat(list, controlsData);
    }

    /**
     * 查询磁盘使用情况
     *
     * @param host
     * @return
     */
    public Object getDirectoryUsedData(String host) {

        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("name", host);

        List<String> ipListIn = Pub.getIpList_in();
        String data = getBaseSourceData(host, "system.disk_usage", 0);

        DecimalFormat decimalFormat = new DecimalFormat("#.0");
        JSONObject jsonObj = JSONObject.parseObject(data);
        JSONArray jsonArr = jsonObj.getJSONArray("resultData");

        Map<String, Object> valuesMap = new HashMap<>();
        List<JSONObject> values = new ArrayList<JSONObject>();
        List<Map<String, Object>> sortedValues = new ArrayList<Map<String, Object>>();

        for (Object object : jsonArr) {
            JSONObject obj = (JSONObject) object;
            JSONObject jsonData = new JSONObject();
            if (ipListIn.contains(host)) {
                String path = obj.getJSONObject("fields").getString("path");
                if (valuesMap.containsKey(path)) {
                    continue;
                }
                double total = obj.getJSONObject("fields").getDouble("total");
                double free = total - obj.getJSONObject("fields").getDouble("used");

                jsonData.put("path", path);
                jsonData.put("free", decimalFormat.format(free / 1024));
                jsonData.put("unit", "GB");
                jsonData.put("total", decimalFormat.format(total / 1024));
                //perc是磁盘剩余空间百分比,用于排序
                jsonData.put("perc", free / total);

                valuesMap.put(path, jsonData);
            } else {
                String path = obj.getJSONObject("fields").getString("device") + obj.getJSONObject("fields").getString("path");
                if (valuesMap.containsKey(path)) {
                    continue;
                }

                double total = obj.getJSONObject("fields").getDouble("total");
                double free = obj.getJSONObject("fields").getDouble("free");

                jsonData.put("path", path);
                jsonData.put("free", decimalFormat.format(free / 1024));
                jsonData.put("unit", "GB");
                jsonData.put("total", decimalFormat.format(total / 1024));

                valuesMap.put(path, jsonData);
            }
            values.add(jsonData);

        }

        //把磁盘空间排序
        Collections.sort(values, new Comparator<JSONObject>() {
            //You can change "Name" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "perc";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                double valA = 0;
                double valB = 0;

                try {
                    valA = a.getDouble(KEY_NAME);
                    valB = b.getDouble(KEY_NAME);
                } catch (JSONException e) {
                    //do something
                }

                if (valA < valB) {
                    return -1;
                } else return 1;
                //if you want to change the sort order, simply use the following:
                //return -valA.compareTo(valB);
            }
        });

        for (int i = 0; i < values.size(); i++) {
            sortedValues.add(values.get(i));
        }

        dataMap.put("values", sortedValues);

        Map<String, Object> outMap = new HashMap<String, Object>();
        outMap.put("result", "success");
        outMap.put("resultData", dataMap);
        String dateStr = DateTool.dateToString(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm");
        outMap.put("titleTime", dateStr);
        outMap.put("message", "数据加载成功！");
        return outMap;
    }


    /**
     * 网络net
     */
    public Object getNetData(String host, int minute) {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Map<String, Object> resultData = new HashMap<String, Object>();
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        List<Object> controlsData = new ArrayList<Object>();

        List<Double> listUpload = new ArrayList<Double>();
        List<Double> listDown = new ArrayList<Double>();
        double totalUpload = 0;
        double currentUpload = 0;
        double totalDown = 0;
        double currentDown = 0;
        String data = getBaseSourceData(host, "system.net_state", minute);
        JSONObject jsonObj = JSONObject.parseObject(data);
        JSONArray jsonArr = jsonObj.getJSONArray("resultData");
        for (Object object : jsonArr) {

            try {
                JSONObject obj = (JSONObject) object;
                JSONObject jsonData = new JSONObject();

                String device = obj.getJSONObject("fields").getString("device");
                if (!device.equals("eth0") && device.indexOf("Intel(R) I350 Gigabit") < 0) {     // 添加224服务器网卡
                    continue;
                }
                double upload = Double.valueOf(decimalFormat.format(obj.getJSONObject("fields").getDouble("in") / (8 * 1024)));
                double down = Double.valueOf(decimalFormat.format(obj.getJSONObject("fields").getDouble("out") / (8 * 1024)));
                listUpload.add(upload);
                listDown.add(down);
                totalUpload += upload;
                totalDown += down;
                currentUpload = upload;
                currentDown = down;
                String time = obj.getJSONObject("fields").getString("data_time");
                jsonData.put("upload", -upload);
                jsonData.put("down", down);
                Date parse = sdf2.parse(time);
                jsonData.put("time", sdf2.format(parse));
                controlsData.add(jsonData);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        if (listUpload.size() < 1 || listDown.size() < 1) {
            Map<String, Object> outMap = new HashMap<String, Object>();
            outMap.put("result", "fail");
            outMap.put("resultData", new ArrayList<>());
            String dateStr = DateTool.dateToString(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm");
            outMap.put("titleTime", dateStr);
            outMap.put("message", "获取数据失败！");
            return outMap;
        }
        Collections.sort(listUpload);
        Collections.sort(listDown);

        List tableData = new ArrayList();
        JSONObject data1 = new JSONObject();
        data1.put("min", Double.parseDouble((String.format("%.2f", listUpload.get(0)))) + "MB");
        data1.put("max", Double.parseDouble((String.format("%.2f", listUpload.get(listUpload.size() - 1)))) + "MB");
        data1.put("avg", decimalFormat.format(totalUpload / listUpload.size()) + "MB");
        data1.put("current", Double.parseDouble((String.format("%.2f", currentUpload))) + "MB");

        JSONObject data2 = new JSONObject();
        data2.put("min", Double.parseDouble((String.format("%.2f", listDown.get(0)))) + "MB");
        data2.put("max", Double.parseDouble((String.format("%.2f", listDown.get(listDown.size() - 1)))) + "MB");
        data2.put("avg", decimalFormat.format(totalDown / listDown.size()) + "MB");
        data2.put("current", Double.parseDouble((String.format("%.2f", currentDown))) + "MB");

        tableData.add(data2);
        tableData.add(data1);
        resultData.put("controlsData", controlsData); // 控件数据
        resultData.put("tableData", tableData); // 表格数据

        Map<String, Object> outMap = new HashMap<String, Object>();
        outMap.put("result", "success");
        outMap.put("resultData", resultData);
        String dateStr = DateTool.dateToString(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm");
        outMap.put("titleTime", dateStr);
        outMap.put("message", "数据加载成功！");

        return outMap;
//        return returnDataTransFormat(list,controlsData);
    }

    /**
     * 拼接返回参数
     *
     * @param list
     * @param controlsData
     * @return
     */
    private Map<String, Object> returnDataTransFormat(List list, List<Object> controlsData) {
        JSONObject resultData = new JSONObject();
        List<Object> tableData = new ArrayList<Object>();
        if (list == null || list.size() < 1) {
            Map<String, Object> outMap = new HashMap<String, Object>();
            outMap.put("result", "fail");
            outMap.put("resultData", new ArrayList<>());
            String dateStr = DateTool.dateToString(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm");
            outMap.put("titleTime", dateStr);
            outMap.put("message", "数据加载失败！");
            return outMap;
        }
        double max = 0;
        double min = 100;
        double total = 0;
        double current = 0;
        for (Object object : list) {
            double d = Double.parseDouble(object.toString());
            total += d;
            current = d;
        }
        Collections.sort(list);
        max = Double.parseDouble(list.get(list.size() - 1).toString());
        min = Double.parseDouble(list.get(0).toString());
        double n = total / list.size();
        double avg = Double.parseDouble(String.format("%.1f", n));
        Map<String, Object> t = new LinkedHashMap<String, Object>();
        Map<String, Object> t2 = new LinkedHashMap<String, Object>();
        t.put("max", max);
        t.put("min", min);
        t.put("avg", avg);
        t.put("current", current);
        t2.put("min", 100 - min);
        t2.put("max", 100 - max);
        t2.put("avg", 100 - avg);
        t2.put("current", 100 - current);
        tableData.add(t);
        tableData.add(t2);

        resultData.put("tableData", tableData);
        resultData.put("controlsData", controlsData);

        Map<String, Object> outMap = new HashMap<String, Object>();
        outMap.put("result", "success");
        outMap.put("resultData", resultData);
        String dateStr = DateTool.dateToString(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm");
        outMap.put("titleTime", dateStr);
        outMap.put("message", "数据加载成功！");
        return outMap;
    }

    /**
     * 假数据
     * cpu
     */
    public Object getCpuDataSham() {
        List<Object> controlsData = new ArrayList<Object>();
        List<Object> tableData = new ArrayList<Object>();
        Map<String, Object> resultData = new LinkedHashMap<String, Object>();
        List<Integer> list = new ArrayList<Integer>();
        int hTime = 10;
        int mTime = 0;
        int dTime = 18;
        int total = 0;
        int max;
        int min;
        int avg;
        int current = 0;
        for (int i = 0; i < 144; i++) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("time", "2017-09-" + dTime + " " + hTime + ":" + mTime);
            int random1 = (int) (Math.random() * 10);
            data.put("user", random1);
            current = random1;
            total += random1;
            data.put("system", random1 + 10);
            data.put("idle", 100 - (random1 * 2 + 10));
            controlsData.add(data);
            list.add(random1);
            if (mTime == 50) {
                if (hTime == 23) {
                    dTime++;
                    hTime = 0;
                } else {
                    hTime++;
                }
                mTime = 0;
            } else {
                mTime += 10;
            }
        }
        Collections.sort(list);
        min = list.get(0);
        max = list.get(list.size() - 1);
        avg = Math.round(total / list.size());
        Map<String, Object> t = new LinkedHashMap<String, Object>();
        Map<String, Object> t2 = new LinkedHashMap<String, Object>();
        Map<String, Object> t3 = new LinkedHashMap<String, Object>();
        t.put("max", 100 - (min * 2 + 10));
        t.put("min", 100 - (max * 2 + 10));
        t.put("avg", 100 - (avg * 2 + 10));
        t.put("current", 100 - (current * 2 + 10));
        t2.put("min", min + 10);
        t2.put("max", max + 10);
        t2.put("avg", avg + 10);
        t2.put("current", current + 10);
        t3.put("min", min);
        t3.put("max", max);
        t3.put("avg", avg);
        t3.put("current", current);

        tableData.add(t);
        tableData.add(t2);
        tableData.add(t3);

        resultData.put("tableData", tableData);
        resultData.put("controlsData", controlsData);
        Map<String, Object> outMap = new HashMap<String, Object>();
        outMap.put("result", "success");
        outMap.put("resultData", resultData);
        String dateStr = DateTool.dateToString(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm");
        outMap.put("titleTime", dateStr);
        outMap.put("message", "数据加载成功！");
        return outMap;
    }

    /**
     * 假数据
     * 内存memory
     */
    public Object getMemoryDataSham() {
        List<Object> controlsData = new ArrayList<Object>();
        List<Object> tableData = new ArrayList<Object>();
        Map<String, Object> resultData = new LinkedHashMap<String, Object>();
        List<Integer> list = new ArrayList<Integer>();
        int hTime = 10;
        int mTime = 0;
        int dTime = 18;
        int total = 0;
        int max;
        int min;
        int avg;
        int current = 0;

        for (int i = 0; i < 144; i++) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("time", "2017-09-" + dTime + " " + hTime + ":" + mTime);
            int random = (int) (Math.random() * 10);
            data.put("used", random + 10);
            data.put("free", 100 - (random + 10));
            current = random;
            total += random;
            controlsData.add(data);
            list.add(random);
            if (mTime == 50) {
                if (hTime == 23) {
                    dTime++;
                    hTime = 0;
                } else {
                    hTime++;
                }
                mTime = 0;
            } else {
                mTime += 10;
            }
        }
        Collections.sort(list);
        min = list.get(0);
        max = list.get(list.size() - 1);
        avg = Math.round(total / list.size());
        Map<String, Object> t = new LinkedHashMap<String, Object>();
        Map<String, Object> t2 = new LinkedHashMap<String, Object>();
        t.put("max", max + 10);
        t.put("min", min + 10);
        t.put("avg", avg + 10);
        t.put("current", current + 10);
        t2.put("min", 90 - min);
        t2.put("max", 90 - max);
        t2.put("avg", 90 - avg);
        t2.put("current", 90 - current);
        tableData.add(t);
        tableData.add(t2);

        resultData.put("tableData", tableData);
        resultData.put("controlsData", controlsData);

        Map<String, Object> outMap = new HashMap<String, Object>();
        outMap.put("result", "success");
        outMap.put("resultData", resultData);
        String dateStr = DateTool.dateToString(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm");
        outMap.put("titleTime", dateStr);
        outMap.put("message", "数据加载成功！");
        return outMap;
    }

    /**
     * 假数据
     * 网络net
     */
    public Object getNetDataSham() {
        Map<String, Object> resultData = new HashMap<String, Object>();

        List<D3NetBean> controlsData = new ArrayList<D3NetBean>();
        int hTime = 10;
        int mTime = 0;
        int dTime = 18;
        int total = 0;
        double current = 0;
        int total2 = 0;
        double current2 = 0;
        List<Double> list = new ArrayList<Double>();
        List<Double> list2 = new ArrayList<Double>();
        for (int i = 0; i < 144; i++) {
            D3NetBean data = new D3NetBean();
            data.setTime("2017-09-" + dTime + " " + hTime + ":" + mTime);
            double random1 = (Math.random() * 1000);
            list.add(random1);
            total += random1;
            current = random1;
            double random2 = (Math.random() * 1000);
            list2.add(random2);
            total2 += random2;
            current2 = random2;
            data.setUpload(Double.parseDouble((String.format("%.2f", -random1))));
            data.setDown(Double.parseDouble((String.format("%.2f", random2))));
            controlsData.add(data);
            if (mTime == 50) {
                if (hTime == 23) {
                    dTime++;
                    hTime = 0;
                } else {
                    hTime++;
                }
                mTime = 0;
            } else {
                mTime += 10;
            }
        }

        Collections.sort(list);
        Collections.sort(list2);

        List tableData = new ArrayList();
        JSONObject data = new JSONObject();
        JSONObject data2 = new JSONObject();
        data2.put("min", Double.parseDouble((String.format("%.2f", list2.get(0)))) + "MB");
        data2.put("max", Double.parseDouble((String.format("%.2f", list2.get(list2.size() - 1)))) + "MB");
        data2.put("avg", total2 / list2.size() + "MB");
        data2.put("current", Double.parseDouble((String.format("%.2f", current2))) + "MB");
        data.put("min", Double.parseDouble((String.format("%.2f", list.get(0)))) + "MB");
        data.put("max", Double.parseDouble((String.format("%.2f", list.get(list.size() - 1)))) + "MB");
        data.put("avg", total / list.size() + "MB");
        data.put("current", Double.parseDouble((String.format("%.2f", current))) + "MB");

        tableData.add(data2);
        tableData.add(data);
        resultData.put("controlsData", controlsData); // 控件数据
        resultData.put("tableData", tableData); // 表格数据

        Map<String, Object> outMap = new HashMap<String, Object>();
        outMap.put("result", "success");
        outMap.put("resultData", resultData);
        String dateStr = DateTool.dateToString(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm");
        outMap.put("titleTime", dateStr);
        outMap.put("message", "数据加载成功！");
        return outMap;
    }

    /*
     * 磁盘directory
     */

    public Object getDirectoryUsedDataSham(String ip) {

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name", ip);

        List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
        for (int j = 0; j < 3; j++) {
            Map<String, Object> value = new HashMap<String, Object>();
            value.put("path", "/data" + j);
            value.put("free", (int) (Math.random() * 800 + 100));
            value.put("unit", "GB");
            value.put("total", 1000);
            values.add(value);
        }
        data.put("values", values);

        Map<String, Object> outMap = new HashMap<String, Object>();
        outMap.put("result", "success");
        outMap.put("resultData", data);
        String dateStr = DateTool.dateToString(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm");
        outMap.put("titleTime", dateStr);
        outMap.put("message", "数据加载成功！");
        return outMap;
    }


    /**
     * 基础资源数据查询
     * 统一查询ES
     *
     * @return
     */
    public String getBaseSourceData(String ip, String metric, int minute) {
        EsQueryBean es = new EsQueryBean();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Pub.Index_Food_Simpledataformat);
            String s1 = Pub.Index_Head + sdf.format(System.currentTimeMillis());
            String s2 = Pub.Index_Head + sdf.format(System.currentTimeMillis() - (3600 * 24 * 1000));

            String[] indice = new String[]{s2, s1};
            es.setIndices(indice);
            String[] types = {"FZJC"};
            es.setTypes(types);
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> mustMap = new HashMap<>();
            mustMap.put("fields.ip", ip);
            mustMap.put("fields.metric", metric);
//		mustMap.put("fields.metric", "system.cpu.pct_usage");

            params.put("must", mustMap);
            params.put("sort", "fields.data_time");


            if (metric.indexOf("disk") > -1) {
                params.put("size", 10);
            } else {
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                calendar.setTime(date);
                calendar.add(Calendar.MINUTE, -minute);
                Date startTime = calendar.getTime();

                List<Map> rangeList = new ArrayList<>();
                Map<String, Object> rangeMap = new HashMap<>();
                rangeMap.put("name", "fields.data_time");
                rangeMap.put("gt", Pub.transform_DateToString(startTime, "yyyy-MM-dd HH:mm"));
                rangeMap.put("lte", Pub.transform_DateToString(date, "yyyy-MM-dd HH:mm"));
                rangeList.add(rangeMap);
                params.put("range", rangeList);
                params.put("resultAll", true);  //返回范围内的所有数据
            }


            es.setParameters(params);

            logger.info("es:" + com.alibaba.fastjson.JSON.toJSONString(es));
//            long start = System.currentTimeMillis();
            Map<String, Object> data_new = esQueryService.getData_new(es);
//            logger.info(metric+" 查询耗时："+(System.currentTimeMillis() - start) +" ms");
            return com.alibaba.fastjson.JSON.toJSONString(data_new);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
