package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.tools.DiskUnit;
import com.cn.hitec.tools.Pub;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.sort.SortOrder;
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
@Slf4j
@Service
public class ESWebService {
    private static final Logger logger = LoggerFactory.getLogger(ESWebService.class);
    @Autowired
    private ESRepository es;


    /**
     * 聚合查询
     * @param indices
     * @param types
     * @param params
     * @return
     * @throws Exception
     */
    public  Map<String,Object> find_AggTerms(String[] indices, String[] types , Map<String,Object> params) throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        try {
            Calendar calendar = Calendar.getInstance();
            Date date = new Date();
            calendar.setTime(date);

            calendar.set(Calendar.MINUTE,-50);

            Date endDate = calendar.getTime();
//
//            logger.info("------> 查询时间间隔:" + (endDate.getTime() - date.getTime()));

//            Map<String,String> queryMap = (Map<String, String>) params.get("query");
//            Map<String,String> aggTermsMap = (Map<String, String>) params.get("agg_terms");
            //创建查询类
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            for (String strKey : params.keySet()){
                queryBuilder.must(QueryBuilders.termQuery(strKey,params.get(strKey)));
            }
            logger.info(Pub.transform_DateToString(endDate,"yyyy-MM-dd HH:mm:ss.sssZ"));
            queryBuilder.must(QueryBuilders.rangeQuery("fields.end_time").gte(Pub.transform_DateToString(endDate,"yyyy-MM-dd HH:mm:ss.sssZ")));


            //创建 聚合 条件
            TermsAggregationBuilder aggbuilder = AggregationBuilders.terms("subtype").field("type.keyword").size(50)
                    .subAggregation(
                            AggregationBuilders.terms("status").field("fields.event_status.keyword").size(5)
                    );


            //返回查询结果
            SearchResponse response = es.client.prepareSearch(indices)
                    .setTypes(types)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(queryBuilder).setSize(0)
                    .addAggregation(aggbuilder)
                    .setExplain(true).get();

            //结果转换
            Terms terms = response.getAggregations().get("subtype");
            for (Terms.Bucket bucket : terms.getBuckets()){

                Terms childTerms = bucket.getAggregations().get("status");
                Map<String,Object> tempMap = new HashMap<>();
                int count = 0;
                for (Terms.Bucket childBucket : childTerms.getBuckets()){
                    if(childBucket.getKey().equals("OK")){
                        tempMap.put(childBucket.getKey().toString(),childBucket.getDocCount());
                    }
                    count += childBucket.getDocCount();
                }
                tempMap.put("count",count);
                resultMap.put(bucket.getKey().toString(),tempMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            resultMap = new HashMap<>();
        } finally {
            return resultMap;
        }

    }


    /**
     * 页面流程图显示聚合方法
     * @return
     * @throws Exception
     */
    public  Map<String,Object> lct_AggTerms(String[] indices, String[] types , String[] subTypes) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        //创建查询类
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termsQuery("type", subTypes));
        queryBuilder.mustNot(QueryBuilders.termQuery("aging_status", "未处理"));
        //创建 聚合 条件
        TermsAggregationBuilder aggbuilder = AggregationBuilders.terms("groupByType").field("type").size(100)
                .subAggregation(
                        AggregationBuilders.terms("groupByModule").field("fields.module").size(10)
                                .subAggregation(
                                        AggregationBuilders.terms("groupByIp").field("fields.ip_addr").size(10)
                                                .subAggregation(
                                                        AggregationBuilders.topHits("groupByDataTime").sort("fields.data_time", SortOrder.DESC).size(1)
                                                )
                                )
                );



        //返回查询结果
        SearchResponse response = es.client.prepareSearch(indices)
                .setTypes(types)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder).setSize(0)
                .addAggregation(aggbuilder)
                .setExplain(true).get();

        //结果转换
        Terms groupByTypeResponse = response.getAggregations().get("groupByType");

        for (Terms.Bucket groupType : groupByTypeResponse.getBuckets()) {
//            log.info("groupType key:{} , groupType value:{}", groupType.getKey(), groupType.getDocCount());

            Terms groupByModuleResponse = groupType.getAggregations().get("groupByModule");

            for (Terms.Bucket groupByModule : groupByModuleResponse.getBuckets()) {
//                log.info("\tgroupByModule key:{} , groupByModule value:{}", groupByModule.getKey(), groupByModule.getDocCount());

                Terms groupByIpResponse = groupByModule.getAggregations().get("groupByIp");
                for (Terms.Bucket groupByIp : groupByIpResponse.getBuckets()) {

//                    log.info("\t\tgroupByIp key:{} , groupByIp value:{}", groupByIp.getKey(), groupByIp.getDocCount());

                    TopHits groupByDataTime = groupByIp.getAggregations().get("groupByDataTime");

                    for (SearchHit hit : groupByDataTime.getHits().getHits()) {
                        String key = groupType.getKey() + "_" + groupByModule.getKey() + "_" + groupByIp.getKey();
                        resultMap.put(key, hit.getSource());
//                        log.info("\t\t\t" + JSON.toJSONString(hit.getSource()));
                    }
                }
            }

        }
        return resultMap;
    }


    /**
     * 查询数据来源，返回数据量大小
     * @param endDate 数据参照物
     * @param timeGranularity 间隔时间  正数 10 代表 参照物时间往后每隔10分钟， 负数 -10 代表参照时间往前每隔10分钟
     * @param t               获取多少条数据
     * @param diskUnit 需要转换成的单位
     * @param scale 保留几位小数
     * @param timeFormat 返回的时间格式
     * @return
     */
    public List<Object> getFileSize(Date endDate , int timeGranularity,int t ,String diskUnit,int scale,String timeFormat,String getDataType){

        List<Object> resultList = new ArrayList<>();
        try {
            BoolQueryBuilder queryBuilder_1 = QueryBuilders.boolQuery();
            BoolQueryBuilder queryBuilder_2 = QueryBuilders.boolQuery();
            BoolQueryBuilder queryBuilder_3 = QueryBuilders.boolQuery();
            BoolQueryBuilder queryBuilder_4 = QueryBuilders.boolQuery();
            BoolQueryBuilder queryBuilder_5 = QueryBuilders.boolQuery();
            BoolQueryBuilder queryBuilder_6 = QueryBuilders.boolQuery();

            Map<String,Double>  params_1 = null;
            Map<String,Double>  params_2 = null;
            Map<String,Double>  params_3 = null;
            Map<String,Double>  params_4 = null;
            Map<String,Double>  params_5 = null;
            Map<String,Double>  params_6 = null;

            queryBuilder_1.must(QueryBuilders.termsQuery("aging_status","正常","异常"));
            queryBuilder_2.must(QueryBuilders.termsQuery("aging_status","正常","异常"));
            queryBuilder_3.must(QueryBuilders.termsQuery("aging_status","正常","异常"));
            queryBuilder_4.must(QueryBuilders.termsQuery("aging_status","正常","异常"));
            queryBuilder_5.must(QueryBuilders.termsQuery("aging_status","正常","异常"));
            queryBuilder_6.must(QueryBuilders.termsQuery("aging_status","正常","异常"));

            if ("1".equals(getDataType)){
                //公服中心
                queryBuilder_1.must(QueryBuilders.termsQuery("fields.module", "采集"));
                //气象中心
                queryBuilder_2.must(QueryBuilders.termsQuery("fields.module", "DS"));

                //获取结果集
                params_1 =  aggAll_fileSizeCount(endDate,timeGranularity,t, diskUnit,scale,timeFormat,queryBuilder_1,"FZJC","LAPS","RGF","OCF","MQPF");
                params_2 =  aggAll_fileSizeCount(endDate,timeGranularity,t, diskUnit,scale,timeFormat,queryBuilder_2,"DATASOURCE");


            }else if ("2".equals(getDataType)){
                //产品加工
                queryBuilder_1.must(QueryBuilders.termQuery("fields.module", "DS"));
                queryBuilder_1.must(QueryBuilders.termsQuery("type","国外天气预报","国内常规城镇预报","国内精细化城镇预报","国内常规城镇预报-06","国内常规城镇预报-16"));
                //智慧云
                queryBuilder_2.must(QueryBuilders.termQuery("fields.module", "DS"));
                queryBuilder_2.must(QueryBuilders.termsQuery("type", "未来三天全国天气预报","未来十天天气趋势预报","全国24小时交通预报","全国地质灾害预报","全球海事公报","24小时近海海区预报"
                        ,"渍涝风险气象预报","国外天气公报","沙尘预报","全国降温大风预报"
                        ,"全国24小时空气污染气象条件预报图","全国48小时空气污染气象条件预报图","全国72小时空气污染气象条件预报图"
                        ,"全国24小时最低气温预报图","全国48小时最低气温预报图","全国72小时最低气温预报图"
                        ,"山洪灾害气象预警","森林火险气象警报","能见度实况图","全国雾24小时预报","全国雾24小时实况图","空间天气72小时预报","短波指数","信鸽指数"
                        ,"海平面海洋天气预报","海平面气压场分析","500hPa高度场分析","每日综述预报"
                        ,"FY2G卫星中国区域数据产品","FY2卫星中国区域彩色图像产品","FY2卫星中国区域红外图像产品","FY2G卫星中国区域红外圆盘图像产品","FY2G卫星中国区域彩色圆盘图像产品","FY2G卫星中国区域水汽圆盘图像产品","FY2G卫星中国区域可见光圆盘图像产品","FY2卫星中国及西太海区红外图片"
                        ,"全国逐小时降水量实况图","全国近10天降水量实况","全国近20天降水量实况","全国近30天降水量实况"
                        ,"全国10天降水距平","全国20天降水距平","全国30天降水距平"
                        ,"全国逐小时气温实况图","全国10天平均温度距平","全国20天平均温度距平","全国30天平均温度距平"
                        ,"全国10天最低气温分布","全国20天最低气温分布","全国30天最低气温分布"
                        ,"全国10天最高气温分布","全国20天最高气温分布","全国30天最高气温分布"
                        ,"气象干旱监测","北京地闪监测","全国地闪监测"));

                //专业服务
                queryBuilder_3.must(QueryBuilders.termQuery("fields.module", "DS"));
                queryBuilder_3.must(QueryBuilders.termsQuery("type","全国24小时交通预报","全球海事公报","能见度实况图"));

                //影视
                queryBuilder_4.must(QueryBuilders.termQuery("fields.module", "DS"));
                queryBuilder_4.must(QueryBuilders.termsQuery("type","国内常规城镇预报","国内大城市6小时精细化预报","国外天气预报","国内常规城镇预报-06","国内常规城镇预报-16"));

                //决策服务
                queryBuilder_5.must(QueryBuilders.termQuery("fields.module", "DS"));
                queryBuilder_5.must(QueryBuilders.termsQuery("type","每日天气摘报","全国地质灾害预报","渍涝风险气象预报","国外天气公报","山洪灾害气象预警","森林火险气象警报"));

                //子公司
                //null

                //获取结果集
                params_1 =  aggAll_fileSizeCount(endDate,timeGranularity,t, diskUnit,scale,timeFormat,queryBuilder_1,"DATASOURCE");
                params_2 =  aggAll_fileSizeCount(endDate,timeGranularity,t, diskUnit,scale,timeFormat,queryBuilder_2,"DATASOURCE");
                params_3 =  aggAll_fileSizeCount(endDate,timeGranularity,t, diskUnit,scale,timeFormat,queryBuilder_3,"DATASOURCE");
                params_4 =  aggAll_fileSizeCount(endDate,timeGranularity,t, diskUnit,scale,timeFormat,queryBuilder_4,"DATASOURCE");
                params_5 =  aggAll_fileSizeCount(endDate,timeGranularity,t, diskUnit,scale,timeFormat,queryBuilder_5,"DATASOURCE");

            }

            for (String time : params_1.keySet()){
                JSONObject tempObj = new JSONObject();

                if ("1".equals(getDataType)){
                    tempObj.put("time",time);
                    tempObj.put("公服中心",params_1.get(time));
                    tempObj.put("气象中心",params_2.get(time));
                    //剩余部门默认为0 ，讨论后再编写查询条件
                    tempObj.put("信息中心",0);
                    tempObj.put("气候中心",0);
                    tempObj.put("探测中心",0);
                    tempObj.put("其他部门",0);

                }else if ("2".equals(getDataType)){
                    tempObj.put("time",time);
                    tempObj.put("产品加工",params_1.get(time));
                    tempObj.put("智慧云",params_2.get(time));
                    //剩余部门默认为0 ，讨论后再编写查询条件
                    tempObj.put("专业服务",params_3.get(time));
                    tempObj.put("影视",params_4.get(time));
                    tempObj.put("决策服务",params_5.get(time));
                    tempObj.put("子公司",0);
                }


                resultList.add(tempObj);
            }

            Collections.sort(resultList, new Comparator<Object>(){
                /*
                 * int compare(Object ob1, Object ob2) 返回一个基本类型的整型，
                 * 返回负数表示：p1 小于p2，
                 * 返回0 表示：p1和p2相等，
                 * 返回正数表示：p1大于p2
                 */
                public int compare(Object ob1, Object ob2) {
                    JSONObject jb1 = (JSONObject) ob1;
                    JSONObject jb2 = (JSONObject) ob2;
                    //按照时间顺序
                    return jb1.get("time").toString().compareTo(jb2.get("time").toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }


    /**
     * 聚合数据，返回数据量大小，默认单位 M
     * @param endDate 数据参照物
     * @param timeGranularity 间隔时间  正数 10 代表 参照物时间往后每隔10分钟， 负数 -10 代表参照时间往前每隔10分钟
     * @param t               获取多少条数据
     * @param diskUnit 需要转换成的单位
     * @param scale 保留几位小数
     * @param timeFormat 返回的时间格式
     * @param queryBuilde es查询类
     * @param types types
     * @return
     */
    private Map<String,Double>  aggAll_fileSizeCount(Date endDate , int timeGranularity ,int t ,String diskUnit,int scale,
                                                     String timeFormat, BoolQueryBuilder queryBuilde ,String... types) throws Exception{
        Map<String,Double> fileSizeCountMap = new HashMap<>();

        if (endDate == null){
            endDate = new Date();
        }
        if (StringUtils.isEmpty(timeFormat)){
            timeFormat = "yyyy-MM-dd HH:mm";
        }
        if (StringUtils.isEmpty(diskUnit)){
            diskUnit = DiskUnit.UNIT_MB;
        }
        if (scale < 0){
            scale = 0;
        }
        if (t <= 1){
            t = 10;
        }

        List<Date> listDate = Pub.getDateList(endDate,timeGranularity,t);

        Date lteDate = null;
        Date gtDate = null;

        for (int i = 0; i < listDate.size() - 1  ; i++){

            lteDate = listDate.get(i);
            gtDate = listDate.get(i+1);
//            System.out.println("\t startDate:"+Pub.transform_DateToString(lteDate,"yyyy-MM-dd HH:mm") +
//                    " \t endDate:"+ Pub.transform_DateToString(gtDate,"yyyy-MM-dd HH:mm"));
            //获取到对应的index
            List<String> indexList = Pub.getIndexList(lteDate,gtDate);
            if (indexList == null || indexList.size() < 1 || listDate.size() != t ){
                continue;
            }
            String[] indexs = indexList.toArray(new String[indexList.size()]);

            //创建查询类
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            SearchRequestBuilder searchRequestBuilder = es.client.prepareSearch(indexs);

            if (types != null || types.length > 1){
                    searchRequestBuilder.setTypes(types);
//                    queryBuilder.must(QueryBuilders.termsQuery("fields.module", modules));
            }
            if (queryBuilde !=  null ){
                queryBuilder = queryBuilde;
            }
            //防止重复添加 range 过滤条件
            List<QueryBuilder> queryBuilderList = queryBuilder.must();
            for (int j = 0 ; j < queryBuilderList.size() ; j++ ){
                QueryBuilder q = queryBuilderList.get(j);
                if("range".equals(q.getName())){
                    queryBuilderList.remove(j); //去除已有的range查询条件
                }
            }

            queryBuilder.must(QueryBuilders.rangeQuery("occur_time")
                    .gt(gtDate.getTime())
                    .lte(lteDate.getTime()));


            //创建 聚合 条件
            SumAggregationBuilder sumAggbuilder = AggregationBuilders.sum("fileSizeSum").field("fields.file_size");

//            System.out.println(queryBuilde.toString());
            //返回查询结果
            SearchResponse response = searchRequestBuilder
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(queryBuilder).setSize(0)
                    .addAggregation(sumAggbuilder)
                    .setExplain(true).get();

            //结果转换
            Sum groupByTypeResponse = response.getAggregations().get("fileSizeSum");
            double count = DiskUnit.transforDiskSize(diskUnit,groupByTypeResponse.getValue(),scale);
            fileSizeCountMap.put(Pub.transform_DateToString(lteDate,timeFormat),count);

        }

        return fileSizeCountMap;
    }


    public List<Map> findDataByQuery(String[] indices, String[] types, Map<String, Object> params) throws Exception {
        System.out.println(JSON.toJSONString(params));
        List<Map> resultList = new ArrayList<>();
        int fromInt = 0;
        int sizeInt = 30;
        String strSort = "";
        String strSortType = "desc";
        boolean isRange = false;
        boolean is_ID = false;
        boolean is_Type = false;
        boolean is_Index = false;
        boolean resultAll = false;
        //值查询
        List<QueryBuilder> rangeBuilderList = null;
        List<QueryBuilder> rangeBuilderListNot = null;
        //创建查询类
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        if(params != null){
            if(params.containsKey("_id") && "true".equals(params.get("_id"))){
                is_ID = true;
                params.remove("_id");
            }
            if(params.containsKey("_type") && "true".equals(params.get("_type"))){
                is_Type = true;
                params.remove("_type");
            }
            if(params.containsKey("_index") && "true".equals(params.get("_index"))){
                is_Index = true;
                params.remove("_index");
            }
            if(params.containsKey("from") && !StringUtils.isEmpty(params.get("from"))){
                fromInt = Integer.valueOf(params.get("from").toString());
                params.remove("from");
            }
            if(params.containsKey("size") && !StringUtils.isEmpty(params.get("size"))){
                sizeInt = Integer.valueOf(params.get("size").toString());
                params.remove("size");
            }
            if(params.containsKey("sort") && !StringUtils.isEmpty(params.get("sort"))){
                strSort = params.get("sort").toString();
                params.remove("sort");
            }
            if(params.containsKey("sortType") && !StringUtils.isEmpty(params.get("sortType"))){
                strSortType = params.get("sortType").toString();
                params.remove("sortType");
            }
            if(params.containsKey("resultAll") && !StringUtils.isEmpty(params.get("resultAll"))){
                resultAll = (Boolean) params.get("resultAll");
                params.remove("resultAll");
            }
            if(params.containsKey("range")){
                isRange = true;
                rangeBuilderList = new ArrayList<>();
                List<Map> rangeList = (List<Map>)params.get("range");
                for(Map<String,Object>  map : rangeList){
                    String name = map.get("name").toString();
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(name);
                    for (String key:map.keySet()){
                        if(key.equals("name")){
                            continue;
                        }
                        rangeQueryBuilder = Pub.RangeChoiceTest(key,rangeQueryBuilder,map.get(key));
                    }
                    rangeBuilderList.add(rangeQueryBuilder);

                }
            }

            if(params.containsKey("rangeNot")){
                isRange = true;
                rangeBuilderListNot = new ArrayList<>();
                List<Map> rangeList = (List<Map>)params.get("range");
                for(Map<String,Object>  map : rangeList){
                    String name = map.get("name").toString();
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(name);
                    for (String key:map.keySet()){
                        if(key.equals("name")){
                            continue;
                        }
                        rangeQueryBuilder = Pub.RangeChoiceTest(key,rangeQueryBuilder,map.get(key));
                    }
                    rangeBuilderListNot.add(rangeQueryBuilder);

                }
            }

            if(params.containsKey("wildcard")){
                Map<String,Object> wildcardMap = (Map<String,Object>)params.get("wildcard");
                for (String strKey : wildcardMap.keySet()){
                    queryBuilder.must(QueryBuilders.wildcardQuery(strKey,wildcardMap.get(strKey).toString()));
                }
            }
            if(params.containsKey("wildcardNot")){
                Map<String,Object> wildcardNotMap = (Map<String,Object>)params.get("wildcardNot");
                for (String strKey : wildcardNotMap.keySet()){
                    queryBuilder.mustNot(QueryBuilders.wildcardQuery(strKey,wildcardNotMap.get(strKey).toString()));
                }
            }

            if(params.containsKey("must")){
                Map<String,Object> mustMap = (Map<String,Object>)params.get("must");
                for (String strKey : mustMap.keySet()){
                    if(mustMap.get(strKey) instanceof String){
                        queryBuilder.must(QueryBuilders.termQuery(strKey,mustMap.get(strKey)));
                    }else if(mustMap.get(strKey) instanceof List){
                        List<Object> list =(List<Object>)mustMap.get(strKey);
                        queryBuilder.must(QueryBuilders.termsQuery(strKey,list));
                    }

                }
            }
            if(params.containsKey("mustNot")){
                Map<String,Object> mustNotMap = (Map<String,Object>)params.get("mustNot");
                for (String strKey : mustNotMap.keySet()){
                    if(mustNotMap.get(strKey) instanceof String){
                        queryBuilder.mustNot(QueryBuilders.termQuery(strKey,mustNotMap.get(strKey)));
                    }else if(mustNotMap.get(strKey) instanceof List){
                        List<Object> list =(List<Object>)mustNotMap.get(strKey);
                        queryBuilder.mustNot(QueryBuilders.termsQuery(strKey,list));
                    }
                }
            }

            if(isRange){
                if (rangeBuilderList != null){
                    for (QueryBuilder qb:rangeBuilderList){
                        queryBuilder.must(qb);
                    }
                }
                if (rangeBuilderListNot != null){
                    for (QueryBuilder qb:rangeBuilderListNot){
                        queryBuilder.mustNot(qb);
                    }
                }
            }
        }

//        log.info(queryBuilder.toString());
        //验证去除没有的index
        List<String> listIndice = new ArrayList<>();
        if(indices != null){
            for (String str : indices){
                if(es.exists(str)){
                    listIndice.add(str);
                }
            }
            if(listIndice.size() > 0){
                indices = listIndice.toArray(new String [listIndice.size()]);
            }else{
                throw new Exception("index not be null");
            }
        }else {
            throw new Exception("index not be null");
        }
        log.info("indices:"+JSON.toJSONString(indices));
        SearchRequestBuilder requestBuilder = es.client.prepareSearch(indices)
                .setTypes(types)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder);

        if(!StringUtils.isEmpty(strSort)){
//            SortBuilder sortBuilder = SortBuilders.fieldSort("last_time");
            requestBuilder.addSort(strSort, strSortType.equals("desc")? SortOrder.DESC : SortOrder.ASC);
        }

        long start = System.currentTimeMillis();
        if(resultAll){
            requestBuilder.setSize(1000);
            //创建查询
            SearchResponse response = requestBuilder.setScroll(new TimeValue(2000)).get();
            do {
                for (SearchHit hits : response.getHits().getHits()) {
                    try {
                        if(is_ID){
                            hits.getSource().put("_id",hits.getId());
                        }
                        if(is_Type){
                            hits.getSource().put("_type",hits.getType());
                        }
                        if(is_Index){
                            hits.getSource().put("_index",hits.getIndex());
                        }
                        resultList.add(hits.getSource());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                response = es.client.prepareSearchScroll(response.getScrollId())
                        .setScroll(new TimeValue(2000))
                        .execute().actionGet();
            } while(response.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.

        }else{
            if(fromInt > 0){
                requestBuilder.setFrom(fromInt);
            }
            if(sizeInt > 0){
                requestBuilder.setSize(sizeInt);
            }
//            log.info(JSON.toJSONString(requestBuilder));

            //创建查询
            SearchResponse response = requestBuilder
                    .setExplain(false).get();

            SearchHit[] searchHits = response.getHits().getHits();
            for (SearchHit hits:searchHits) {
                try {
                    if(is_ID){
                        hits.getSource().put("_id",hits.getId());
                    }
                    if(is_Type){
                        hits.getSource().put("_type",hits.getType());
                    }
                    if(is_Index){
                        hits.getSource().put("_index",hits.getIndex());
                    }
                    resultList.add(hits.getSource());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("resultList.size = " + resultList.size());
        log.info("查询ES耗时："+(System.currentTimeMillis() - start));
        return resultList;
    }

}
