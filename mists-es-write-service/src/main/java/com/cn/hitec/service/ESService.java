package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.AlertBean;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.tools.Pub;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @ClassName:
 * @Description:
 * @author: fukl
 * @data: 2017年08月3日 下午1:14
 */
@Service
public class ESService {
    private static final Logger logger = LoggerFactory.getLogger(ESService.class);
    @Autowired
    private ESRepository es;
    @Autowired
    ESClientAdminService esClientAdminService;
    @Autowired
    AlertService alertService;
    /**
     * 添加数据 自动生成id
     * @param index
     * @param type
     */
    public int add(String index,String type,List<String> listJson) {
        int error_num = 0;
        int listSize = 0;
        try {
            if (listJson == null || listJson.size() < 1) {
                return 0;
            }
            listSize = listJson.size();
            for (String json : listJson) {
                if (StringUtils.isEmpty(json)) {
                    error_num++;
                    continue;
                }
                System.out.println(json);
                es.bulkProcessor.add(new IndexRequest(index, type)
                        .source(json, XContentType.JSON));
            }
//            System.out.println("清理缓存！");
//            es.bulkProcessor.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return listSize - error_num ;
        }

    }


    /**
     * 添加数据
     * @param index
     * @param type
     * @param listJson
     * @return
     */
    public int insert(String index,String type,List<String> listJson) {
        int error_num = 0;
        int listSize = 0;
        try {
            if (listJson == null || listJson.size() < 1) {
                return 0;
            }
            listSize = listJson.size();
            for (String json : listJson) {
                if (StringUtils.isEmpty(json)) {
                    error_num++;
                    continue;
                }
                es.client.prepareIndex(index,type).setSource(json,XContentType.JSON).get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return listSize - error_num ;
        }

    }

    /**
     * 添加数据 指定id
     * @param index
     * @param type
     * @param id
     * @param json
     */
    public void add(String index,String type, String id ,String json){

        try {
            es.bulkProcessor.add(new IndexRequest(index,type ,id)
                    .source(json, XContentType.JSON));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加数据 返回IndexResponse
     * @param index
     * @param type
     * @param json
     */
    public String add_resultId(String index, String type, String json){
        String strId = null;
        IndexResponse response = null;
        try {
            if(StringUtils.isEmpty(json)){
                return null;
            }
            response = es.client.prepareIndex(index,type).setSource(json,XContentType.JSON).get();
            strId = response.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strId;
    }



    public int update_field(String index,String type,String id,Map<String,Object> params){
        int res = -1;
        try {
           XContentBuilder xContentBuilder =  XContentFactory.jsonBuilder();
           for (String strKey : params.keySet()){
               xContentBuilder.startObject()
                       .field("aging_status",params.get(strKey))
                       .endObject();
           }

            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index(index);
            updateRequest.type(type);
            updateRequest.id(id);
            updateRequest.doc(xContentBuilder);
            UpdateResponse updateResponse = es.client.update(updateRequest).get();
            String resStatus = updateResponse.status().toString();
            if("OK".equals(resStatus)){
                res = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /** 过滤重复数据（相同类型和时次的数据）  目前的主要数据录入类
     * @param index
     * @param type
     */
    public int update(String index,String type,List<String> listJson) {
        int error_num = 0;
        int listSize = 0;
        try {
            if (listJson == null || listJson.size() < 1) {
                logger.error("参数为空");
                return 0;
            }
            listSize = listJson.size();
            Map<String,Object> map = null;
            Map<String,Object> fields = null;
            Map<String,Object> DIMap = null;
            for (String json : listJson) {
                try {
                    if (StringUtils.isEmpty(json)) {
                        logger.error("数据为空");
                        error_num++;
                        continue;

                    }
//
                    map = JSON.parseObject(json);
                    fields = (Map<String,Object>)map.get("fields");

                    String subType = map.get("type").toString();        //数据名称

                    Date endTime = Pub.transform_StringToDate(fields.get("end_time").toString(),"yyyy-MM-dd HH:mm:ss.SSSZ");
                    index = Pub.Index_Head+Pub.transform_DateToString(endTime,Pub.Index_Food_Simpledataformat);

                    //如果是定时 有规律的数据， 需要查询后入库
                    Map<String,Object> resultMap = new HashMap<>();
                    if(Pub.alert_time_map.containsKey(subType) ){
                        String[] indices = null;
                        if("T639".equals(subType) || "风流场".equals(subType)){        //风流场数据，不需要判断时效性
                            Date tempDate = Pub.transform_StringToDate(fields.get("data_time").toString(),"yyyy-MM-dd HH:mm:ss.SSSZ");
                            indices = new String[]{Pub.Index_Head+Pub.transform_DateToString(tempDate,Pub.Index_Food_Simpledataformat)};
                        }else{
                            indices = Pub.getIndices(endTime,1);   //获取今天和昨天的 index
                        }
                        resultMap = getDocumentId(indices,type,subType,fields);
                    }else{
                        map.put("aging_status","正常");
                        logger.info("这是一条非定时数据,类型为：{}, 时次为：{}",subType,fields.get("data_time"));
                        es.bulkProcessor.add(new IndexRequest(index, type)
                                .source(json, XContentType.JSON));
                        continue;
                    }
                    if(resultMap.containsKey("_id")){       //如果查询到id
                        logger.info("这是预生成数据,类型为：{}, 时次为：{}",subType,fields.get("data_time"));
//                        if("雷达".equals(subType)){
//                            logger.info(json);
//                        }
                        AlertBean alertBean = null;
                        String alertType = "alert";
                        index = resultMap.get("_index").toString();
                        type = resultMap.get("_type").toString();
                        String strId = resultMap.get("_id").toString();
                        Map<String,Object> hitsSource_fields = (Map<String,Object>) resultMap.get("fields");

                        //过滤掉不应该有的数据
                        if( "21".equals(fields.get("event_status"))){
                            continue;
                        }
                        //当  数据库里的数据 和 当前数据   一样时（目前是按照数据状态来判断），放弃掉该条数据
                        if(hitsSource_fields.containsKey("event_status")  && fields.get("event_status").toString().equals(hitsSource_fields.get("event_status"))){
                            logger.warn("--舍弃掉 相同的数据："+json);
                            continue;
                        }
                        if("T639".equals(subType) || "风流场".equals(subType)){        //如果是风场数据 ， 要与其他定时数据分开分析
                            //确定是否进行 数据状态 告警
                            if(fields.get("event_status").toString().toUpperCase().equals("OK") || fields.get("event_status").toString().equals("0")){
                                map.put("aging_status","正常");
                                fields.put("event_info","正常");
                            }else{
                                //判断如果原数据是正确的， 新数据是错误的， 舍弃新数据
                                if(hitsSource_fields.containsKey("event_status")  && (hitsSource_fields.get("event_status").toString().toUpperCase().equals("OK") || hitsSource_fields.get("event_status").toString().equals("0"))){
                                    logger.warn("--舍弃掉 预修改错误的数据："+json);
                                    continue;
                                }
                                map.put("aging_status","异常");
                                String alertTitle = subType+"--"+fields.get("module")+"--"+fields.get("data_time")+" 时次产品 ，发生错误："+fields.get("event_info").toString();
                                //初始化告警实体类
                                alertBean = alertService.getAlertBean("异常",alertTitle,subType,fields);
                            }
                        }else{
                            //确定是否进行 数据状态 告警
                            if(fields.get("event_status").toString().toUpperCase().equals("OK") || fields.get("event_status").toString().equals("0")){
                                Date nowDate = Pub.transform_StringToDate(fields.get("end_time").toString(),"yyyy-MM-dd HH:mm:ss.SSSZ");
                                Date lastDate = Pub.transform_StringToDate(resultMap.get("last_time").toString(),"yyyy-MM-dd HH:mm:ss.SSSZ");
                                //确定是否 时效告警 ,修改时效状态
                                if (nowDate.getTime() - lastDate.getTime() >= 1000) {
                                    Date shuldDate = Pub.transform_StringToDate(resultMap.get("should_time").toString(),"yyyy-MM-dd HH:mm:ss.SSSZ");
                                    map.put("aging_status","迟到");
                                    String temp = transform_time((int)(nowDate.getTime()  - shuldDate.getTime()));
                                    String alertTitle = subType+"--"+fields.get("module")+"--"+fields.get("data_time")+" 时次产品 ，延迟"+temp+"到达";

                                    fields.put("event_info","延迟"+temp+"到达");
                                    //生成告警类
                                    alertBean = alertService.getAlertBean("迟到",alertTitle,subType,fields);

                                }else{
                                    map.put("aging_status","正常");
                                    fields.put("event_info","正常");
                                }
                            }else{
                                //判断如果原数据是正确的， 新数据是错误的， 舍弃新数据
                                if(hitsSource_fields.containsKey("event_status")  && (hitsSource_fields.get("event_status").toString().toUpperCase().equals("OK") || hitsSource_fields.get("event_status").toString().equals("0"))){
                                    logger.warn("--舍弃掉 预修改错误的数据："+json);
                                    continue;
                                }
                                map.put("aging_status","异常");
                                String alertTitle = subType+"--"+fields.get("module")+"--"+fields.get("data_time")+" 时次产品 ，发生错误："+fields.get("event_info").toString();
                                //初始化告警实体类
                                alertBean = alertService.getAlertBean("异常",alertTitle,subType,fields);
                            }
                            //将 应到时间 和 最晚到达时间，添加的数据中
                            map.put("should_time", resultMap.containsKey("should_time") ?  resultMap.get("should_time") : "");
                            map.put("last_time", resultMap.containsKey("last_time") ?  resultMap.get("last_time") : "");

                            if("采集".equals(fields.get("module"))){
                                DIMap = (Map<String, Object>) Pub.alertMap_collect.get(subType);
                            }else if("加工".equals(fields.get("module"))){
                                DIMap = (Map<String, Object>) Pub.alertMap_machining.get(subType);
                            }else if("分发".equals(fields.get("module"))){
                                DIMap = (Map<String, Object>) Pub.alertMap_distribute.get(subType);
                            }
                            //添加路径
                            if(DIMap != null){
                                String fileName = fields.containsKey("file_name") ? fields.get("file_name").toString():"";
                                if(StringUtils.isEmpty(fileName) || fileName.indexOf("/") == -1){
                                    fields.put("file_name",DIMap.get("path")+fileName);
                                }
                            }
                        }

                        if(alertBean != null){
                            alertService.alert(index,alertType,alertBean);  //生成告警
                            alertBean = null;
                        }
                        //数据入库
                        es.bulkProcessor.add(new IndexRequest(index,type ,strId).source(map));
                        DIMap = null;
                    }else{
                        logger.info("这是一条未查询到的数据,类型为：{}, 时次为：{}",subType,fields.get("data_time"));
                        es.bulkProcessor.add(new IndexRequest(index, type)
                                .source(json, XContentType.JSON));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                    error_num++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return listSize - error_num ;
        }

    }


    /**
     * 查询单条数据ID
     * @param indexs
     * @param type
     * @param sbuType
     * @param fields
     * @return
     */
    public Map<String,Object> getDocumentId(String[] indexs,String type,String sbuType , Map<String,Object> fields){
        Map<String,Object> resultMap = new HashMap<>();
        try {

            String[] indices = esClientAdminService.indexExists(indexs);
            if(indices == null || indices.length < 1){
                return resultMap;
            }
            //创建查询类
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(QueryBuilders.termQuery("type.keyword",sbuType));
            queryBuilder.must(QueryBuilders.termQuery("fields.module.keyword",fields.get("module").toString()));
            queryBuilder.must(QueryBuilders.termQuery("fields.ip_addr.keyword",fields.get("ip_addr").toString()));
            queryBuilder.must(QueryBuilders.termQuery("fields.data_time.keyword",fields.get("data_time").toString()));
            //返回查询结果
            SearchResponse response = es.client.prepareSearch(indices)
                    .setTypes(type)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(queryBuilder)
                    .setExplain(true).get();

            SearchHit[] searchHits = response.getHits().getHits();
            logger.info("searchHits.dataLength :"+response.getHits().getTotalHits());
            if(response.getHits().getTotalHits() != 1){
                logger.error("预生成数据有误，请查询ES，查询条件为：indexs:{} , type:{} , module:{}, fields:{}",indexs,type,sbuType,fields);
            }
            for (SearchHit hits:searchHits) {
                resultMap = hits.getSource();
                resultMap.put("_id",hits.getId());
                resultMap.put("_type",hits.getType());
                resultMap.put("_index",hits.getIndex());
                break;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
//            resultMap = new HashMap<>();
        } finally {
            return resultMap;
        }

    }


    public String transform_time (int time){
        if(time <= 0){
            return "";
        }
        time = time/1000;
        int min = time / 60 ;
        int secend = time % 60;

        return min+"分"+secend+"秒";
    }

    public static void main(String[] args){
        try {
            Date date = new Date(1509505500304L);
            String str = Pub.transform_DateToString(date,"yyyy-MM-dd HH:mm:ss.SSSZ") ;
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
