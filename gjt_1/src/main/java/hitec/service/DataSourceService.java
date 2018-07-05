package hitec.service;

import hitec.repository.ESRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service("DataSourceService")
public class DataSourceService {

	private static final Logger logger = LoggerFactory.getLogger(DataSourceService.class);
	
	@Autowired
	ESRepository es;
	
	@Value("${es.indexHeader}")
	public String indexHeader;
	
	public Object getAllTableData(HttpServletRequest request){
		String sort = request.getParameter("sort");
		String order = request.getParameter("order");
		String limitStr = request.getParameter("limit");
		String offsetStr = request.getParameter("offset");
		String alertLevel = request.getParameter("alertLevel");
		String queryType = request.getParameter("queryType");
		
		int limit = 10;
		int offset = 0;
		try {
			offset = Integer.parseInt(offsetStr);
			limit = Integer.parseInt(limitStr);
		} catch (Exception e) {
			logger.error("页数和显示条数转int报错"+ e.toString());
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		long total = 0;
		List<Map<String, Object>> rows = new ArrayList<Map<String,Object>>();
		// 查询一天的数据，跨天数据，所以需要两个index
    	String[] queryIndex = new String[2];
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	for (int i = 0; i < queryIndex.length; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -i);
			String index = indexHeader + sdf.format(cal.getTime());
			queryIndex[i] = index;
		}
    	
    	BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    	boolQuery.must(QueryBuilders.termsQuery("aging_status", "正常", "超时", "迟到","异常"));
    	if ("告警".equals(queryType)){	// 查询全部或告警
    		boolQuery.mustNot(QueryBuilders.termQuery("aging_status", "正常"));
    	}
    	
    	if (StringUtils.isNotEmpty(alertLevel)){ // 查询告警类型
    		boolQuery.must(QueryBuilders.termQuery("aging_status", alertLevel));
    	}
//    	if ("正常".equals(alertLevel)){	// 查询告警等级
//    		boolQuery.must(QueryBuilders.termQuery("aging_status", "正常"));
//    	}else if("严重".equals(alertLevel)){
//    		boolQuery.mustNot(QueryBuilders.termQuery("aging_status", "正常"));
//    		boolQuery.must(QueryBuilders.termsQuery("name", new String[]{"国内精细化城镇预报"}));
//    	}else if("紧急".equals(alertLevel)){
//    		boolQuery.mustNot(QueryBuilders.termQuery("aging_status", "正常"));
//    		boolQuery.must(QueryBuilders.termsQuery("name", new String[]{}));
//    	}else if("警告".equals(alertLevel)){
//    		boolQuery.mustNot(QueryBuilders.termQuery("aging_status", "正常"));
//    		boolQuery.must(QueryBuilders.termsQuery("name", new String[]{}));
//    	}
    	
    	SearchRequestBuilder requestBuilder = es.client.prepareSearch(queryIndex)
    		.setTypes(new String[]{"DATASOURCE"})
    		.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
    		.setQuery(boolQuery)
    		.setFrom(offset)
    		.setSize(limit);
    	
    	if (!StringUtils.isEmpty(sort)){	// 排序
    		if ("asc".equals(order)){
    			requestBuilder.addSort(sort, SortOrder.ASC);
    		}else{
    			requestBuilder.addSort(sort, SortOrder.DESC);
    		}
    	}else{
    		requestBuilder.addSort("should_time", SortOrder.DESC);
    	}
    	
    	SearchResponse response = requestBuilder.setExplain(false).get();
    	total = response.getHits().getTotalHits();
    	
    	
        for (SearchHit hits : response.getHits().getHits()) {
            try {
            	rows.add(hits.getSource());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
//        for (int j = 0; j < rows.size(); j++) {
//        	Map<String, Object> map = rows.get(j);
//        	map.put("aging_status", "正常");
//		}

//        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
//        if ("正常".equals(alertLevel) || "所有".equals(queryType) || queryType == null){
//        	list.add(makeFalseData("信息中心", "中国地面分钟级观测资料", "地面资料", "10.10.31.98", 6,"天气网"));
//        	list.add(makeFalseData("开放系统实验室", "格点实况3公里产品", "自有产品", "10.10.31.87", 6,""));
//        	list.add(makeFalseData("开放系统实验室", "闪电预报", "自有产品", "10.10.31.98", 5,""));
//        	list.add(makeFalseData("气象中心", "国内精细化城镇预报", "预报产品", "10.10.31.98", 5,"全媒体气象产品室"));
//        	list.add(makeFalseData("气象中心", "国外天气预报", "预报产品", "10.10.31.98", 4,"影视中心"));
//        	list.add(makeFalseData("气象中心", "国内大城市6小时精细化预报", "预报产品", "10.10.31.98", 4,"影视中心"));
//        	list.add(makeFalseData("气象中心", "国内常规城镇预报", "预报产品", "10.10.31.98", 4,"全媒体气象产品室，影视中心"));
//        	list.add(makeFalseData("开放系统实验室", "强对流数据", "自有产品", "10.10.31.98", 3,"开放系统实验室"));
//        	list.add(makeFalseData("开放系统实验室", "公路交通精细化预报", "自有产品", "10.10.31.98", 3,""));
//        	list.add(makeFalseData("开放系统实验室", "公路交通精细化预报", "自有产品", "10.10.31.98", 3,""));
//        	list.add(makeFalseData("气象中心", "国内精细化城镇预报", "预报产品", "10.10.31.98", 2,"全媒体气象产品室"));
//        	list.add(makeFalseData("气象中心", "国外天气预报", "预报产品", "10.10.31.98", 1,"影视中心"));
//        	list.add(makeFalseData("气象中心", "国内大城市6小时精细化预报", "预报产品", "10.10.31.98", 0,"影视中心"));
//        	list.add(makeFalseData("气象中心", "国内常规城镇预报", "预报产品", "10.10.31.98", 0,"全媒体气象产品室，影视中心"));
//        }
//        int outNum = 0;
//        for (int i = 0; i < list.size(); i++) {
//			if (i >= offset){
//				if (outNum < limit){
//					rows.add(list.get(i));
//					outNum++;
//				}
//			}
//		}
        
    	resultMap.put("total", total);
    	resultMap.put("rows", rows);
    	return resultMap;
	}
	
	public Map<String, Object> makeFalseData(String departmentName, String name, String dataType,
			String ipAddr, int timer, String useDepartment){
		Map<String, Object> outMap = new HashMap<String, Object>();
		outMap.put("fields.department_name", departmentName);
		outMap.put("name", name);
		outMap.put("aging_status", "正常");
		outMap.put("fields.data_type", dataType);
		outMap.put("fields.ip_addr", ipAddr);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, -timer);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
		outMap.put("should_time", sdf.format(cal.getTime()) + ":00:00");
		
		double random = Math.random();
		outMap.put("fields.file_size", Math.round(random * 10000));
		outMap.put("fields.use_department", useDepartment);
		return outMap;
	}
	
}
