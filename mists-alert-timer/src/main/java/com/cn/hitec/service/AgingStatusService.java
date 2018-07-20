package com.cn.hitec.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_Exsit;
import com.cn.hitec.bean.EsWriteBean;
import com.cn.hitec.feign.client.EsQueryService;
import com.cn.hitec.feign.client.EsWriteService;
import com.cn.hitec.util.CronPub;
import com.cn.hitec.util.Pub;

/**
 * @Description: 这里是描述信息
 * @author: fukl
 * @data: 2017年09月30日 21:56
 */
@Service
public class AgingStatusService {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AgingStatusService.class);
	@Autowired
	EsQueryService esQueryService;
	@Autowired
	SendAlertMessage sendMessage;
	@Autowired
	EsWriteService esWriteService;

	/**
	 * 定时处理超时未到的数据，将状态致为超时
	 * @return
	 * @throws Exception
	 */
	public void collect_task() throws Exception{

        int up_number = 0;
        int all_number = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, -1);   //将时间往前推一分钟，简单避免入库延迟导致的报警

        EsQueryBean esQueryBean = new EsQueryBean();
        String[] str_indexs = Pub.getIndices(new Date(), 1);
        esQueryBean.setIndices(str_indexs);
        esQueryBean.setTypes(new String[]{"FZJC", "LAPS","DATASOURCE","MQPF"});

        Map<String, Object> mustMap = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        mustMap.put("aging_status", "未处理");

        List<Map> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "last_time");
        map.put("lt", Pub.transform_DateToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss.SSSZ"));
        list.add(map);
        params.put("range", list);
        params.put("must", mustMap);
        params.put("sort", "fields.data_time");
        params.put("sortType", "asc");
        params.put("_index", "true");
        params.put("_type", "true");
        params.put("_id", "true");
        params.put("resultAll", true);

        esQueryBean.setParameters(params);

        // 查询到 所有未处理状态的数据，按照资料时间排序
        Map<String, Object> responseMap = esQueryService.getData_new(esQueryBean);
        if (responseMap != null && Pub.VAL_SUCCESS.equals(responseMap.get(Pub.KEY_RESULT))) {
            List dataList = (List) responseMap.get(Pub.KEY_RESULTDATA);
            all_number = dataList.size();
            for (Object object : dataList) {
                try {
                    Map<String, Object> objMap = (Map<String, Object>) object;

                    String str_index = objMap.get("_index").toString();
                    String str_type = objMap.get("_type").toString();
                    String str_id = objMap.get("_id").toString();

                    Map<String, Object> pam = new HashMap<>();
                    EsWriteBean esWriteBean = new EsWriteBean();
                    esWriteBean.setIndex(str_index);
                    esWriteBean.setType(str_type);
                    esWriteBean.setId(str_id);
                    pam.put("aging_status", "超时");
                    esWriteBean.setParams(pam);
                    esWriteService.update_field(esWriteBean);
                    up_number++;

                    if ("yes".equals(objMap.get("startMoniter").toString())) {
                        //修改发送消息代码，先入es，再定时执行发送， 这样不会妨碍入库程序的速度
                        sendMessage.sendAlert(str_index, "alert", objMap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            logger.error(JSON.toJSONString(responseMap));
        }

        logger.info("---查询出: " + all_number + " 条超时数据，修改了：" + up_number + " 条");
	}


    /**
     * 定时处理超时未到的数据，将状态致为超时
     * @return
     * @throws Exception
     */
    public void collectDataSource_task() throws Exception{

        int up_number = 0;
        int all_number = 0;
        Date nowDate = new Date();
        EsQueryBean esQueryBean = new EsQueryBean();
        String[] str_indexs = Pub.getIndices(new Date(), 1);
        esQueryBean.setIndices(str_indexs);
        esQueryBean.setTypes(new String[]{"DATASOURCE"});

        Map<String, Object> mustMap = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        mustMap.put("aging_status", "未处理");

        List<Map> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "last_time");
        map.put("lte", Pub.transform_DateToString(nowDate, "yyyy-MM-dd HH:mm:ss"));
        list.add(map);
        params.put("range", list);
        params.put("must", mustMap);
        params.put("sort", "fields.data_time");
        params.put("sortType", "asc");
        params.put("_index", "true");
        params.put("_type", "true");
        params.put("_id", "true");
        params.put("resultAll", true);

        esQueryBean.setParameters(params);

        // 查询到 所有未处理状态的数据，按照资料时间排序
        Map<String, Object> responseMap = esQueryService.getData_new(esQueryBean);
        if (responseMap != null && Pub.VAL_SUCCESS.equals(responseMap.get(Pub.KEY_RESULT))) {
            List dataList = (List) responseMap.get(Pub.KEY_RESULTDATA);
            all_number = dataList.size();
            for (Object object : dataList) {
                try {
                    Map<String, Object> objMap = (Map<String, Object>) object;

                    String str_index = objMap.get("_index").toString();
                    String str_type = objMap.get("_type").toString();
                    String str_id = objMap.get("_id").toString();

                    Map<String, Object> pam = new HashMap<>();
                    EsWriteBean esWriteBean = new EsWriteBean();
                    esWriteBean.setIndex(str_index);
                    esWriteBean.setType(str_type);
                    esWriteBean.setId(str_id);
                    pam.put("aging_status", "超时");
                    esWriteBean.setParams(pam);
                    esWriteService.update_field(esWriteBean);
                    up_number++;

                    if ("yes".equals(objMap.get("startMoniter").toString())) {
                        //修改发送消息代码，先入es，再定时执行发送， 这样不会妨碍入库程序的速度
                        sendMessage.sendAlert(str_index, "alert", objMap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            logger.error(JSON.toJSONString(responseMap));
        }

        logger.info("---查询出: " + all_number + " 条数据源 超时 数据，修改了：" + up_number + " 条");
    }


    /**
     * 定时处理超时未到的数据，将状态致为超时
     * @return
     * @throws Exception
     */
	public int task_T639() throws Exception {
		int up_number = 0;
		int all_number = 0;
		// 生成日历插件， 计算出 今天和第六天后的0点 的时间
		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		calendar.setTime(date);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date startDate = calendar.getTime();

		calendar.add(Calendar.DAY_OF_MONTH, 6);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		Date endDate = calendar.getTime();

		List<Date> timeList = CronPub.getTimeBycron_Date("0 0 2/3 * * ? *", startDate, endDate);

		List<String> indicesList = new ArrayList<>();
		List<String> temp = new ArrayList<>();
		//计算出需要查询的index
		for (Date dt : timeList) {
			String indexKey = Pub.Index_Head + Pub.transform_DateToString(dt, Pub.Index_Food_Simpledataformat);
			if (temp.contains(indexKey)) {
				continue;
			}
			// 判断是否有index
			if (isExist_DI_Data(indexKey, "FZJC", null)) {
				indicesList.add(indexKey);
			}
			temp.add(indexKey);
		}
		String[] indices = new String[indicesList.size()];
		indicesList.toArray(indices);
		if (indices.length < 1) {
			logger.error("---获取到的index为空");
			return -1;
		}

		//拼接查询条件
		EsQueryBean esQueryBean = new EsQueryBean();
		esQueryBean.setIndices(indices);
		esQueryBean.setTypes(new String[] { "FZJC" });

        Map<String, Object> mustMap = new HashMap<>();
        mustMap.put("aging_status", "未处理");
        mustMap.put("type", "T639,风流场");

		Map<String, Object> params = new HashMap<>();
        params.put("must", mustMap);

		List<Map> rangeList = new ArrayList<>();
		Map<String, Object> rangeMap_1 = new HashMap<>();
		rangeMap_1.put("name", "fields.data_time");
		rangeMap_1.put("gte", Pub.transform_DateToString(startDate, "yyyy-MM-dd HH:mm:ss.SSSZ"));
		rangeMap_1.put("lt", Pub.transform_DateToString(endDate, "yyyy-MM-dd HH:mm:ss.SSSZ"));
		rangeList.add(rangeMap_1);
		params.put("range", rangeList);

        params.put("_index", "true");
        params.put("_type", "true");
        params.put("_id", "true");
        params.put("resultAll", true);

		esQueryBean.setParameters(params);

		// 查询到 所有未处理状态的数据
		Map<String, Object> responseMap = esQueryService.getData_new(esQueryBean);

        if (responseMap != null && Pub.VAL_SUCCESS.equals(responseMap.get(Pub.KEY_RESULT))) {
            List dataList = (List) responseMap.get(Pub.KEY_RESULTDATA);
            all_number = dataList.size();
            long overTime = 24 * 60 * 60 * 1000;
            for (Object object : dataList) {
                try {
                    Map<String, Object> resMap = (Map<String, Object>) object;

                    String str_index = resMap.get("_index").toString();
                    String str_type = resMap.get("_type").toString();
                    String str_id = resMap.get("_id").toString();

                    Map<String, Object> fields = (Map<String, Object>) resMap.get("fields");
                    Date endTime = Pub.transform_StringToDate(fields.get("data_time").toString(),
                            "yyyy-MM-dd HH:mm:ss.SSSZ");
                    if (date.getTime() - endTime.getTime() > overTime) {    //判断是否超过阈值
                        Map<String, Object> pam = new HashMap<>();
                        EsWriteBean esWriteBean = new EsWriteBean();
                        esWriteBean.setIndex(str_index);
                        esWriteBean.setType(str_type);
                        esWriteBean.setId(str_id);
                        pam.put("aging_status", "超时");
                        esWriteBean.setParams(pam);
                        esWriteService.update_field(esWriteBean);   //修改超时数据
                        up_number++;

                        if ("yes".equals(resMap.get("startMoniter").toString())) {
                            //修改发送消息代码，先入es，再定时执行发送， 这样不会妨碍入库程序的速度
                            sendMessage.sendAlert(str_index, "alert", resMap);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }else {
            logger.error(JSON.toJSONString(responseMap));
        }

		logger.info("---查询出: " + all_number + " 条T639超时数据，修改了：" + up_number + " 条");
		return up_number;
	}

	/**
	 * 判断是否有 预生成数据
	 * 
	 * @param index
	 * @param type
	 * @param subType
	 * @return
	 * @throws Exception
	 */
	public boolean isExist_DI_Data(String index, String type, String subType) throws Exception {
		boolean flag = false;

		EsQueryBean_Exsit esQueryBean_exsit = new EsQueryBean_Exsit();
		esQueryBean_exsit.setIndex(index);
		esQueryBean_exsit.setType(type);
		esQueryBean_exsit.setSubType(subType);
		Map<String, Object> resultMap = esQueryService.indexIsExist(esQueryBean_exsit);
		if (!"success".equals(resultMap.get("result"))) {
			throw new Exception("查询发生了错误,错误信息:" + resultMap.get("message"));
		}
		flag = (Boolean) resultMap.get("resultData");

		return flag;
	}

}
