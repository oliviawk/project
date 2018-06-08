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
	
	@Value("${esIndexName}")
	public String esIndexName;
	
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
			String index = esIndexName + sdf.format(cal.getTime());
			queryIndex[i] = index;
		}
    	
    	BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    	if ("告警".equals(queryType)){	// 查询全部或告警
    		boolQuery.mustNot(QueryBuilders.termQuery("aging_status", "正常"));
    	}
    	
    	if ("正常".equals(alertLevel)){	// 查询告警等级
    		boolQuery.must(QueryBuilders.termQuery("aging_status", "正常"));
    	}else if("严重".equals(alertLevel)){
    		boolQuery.mustNot(QueryBuilders.termQuery("aging_status", "正常"));
    		boolQuery.must(QueryBuilders.termsQuery("name", new String[]{"国内精细化城镇预报"}));
    	}else if("紧急".equals(alertLevel)){
    		boolQuery.mustNot(QueryBuilders.termQuery("aging_status", "正常"));
    		boolQuery.must(QueryBuilders.termsQuery("name", new String[]{}));
    	}else if("警告".equals(alertLevel)){
    		boolQuery.mustNot(QueryBuilders.termQuery("aging_status", "正常"));
    		boolQuery.must(QueryBuilders.termsQuery("name", new String[]{}));
    	}
    	
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

    	resultMap.put("total", total);
    	resultMap.put("rows", rows);
    	return resultMap;
	}
	
}
