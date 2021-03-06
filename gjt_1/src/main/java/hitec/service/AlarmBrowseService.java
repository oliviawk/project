package hitec.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import hitec.domain.DataInfo;
import hitec.repository.DataInfoRepository;
import hitec.repository.ESRepository;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("AlarmBrowseService")
public class AlarmBrowseService {

	private static final Logger logger = LoggerFactory.getLogger(AlarmBrowseService.class);
	
    @Autowired
    DataInfoRepository dataInfoRepository;
    @Autowired
    ESRepository es;
    
    public Object getAlarmTypeNode(){
        Map<String, Object> responseMap = new HashMap<String, Object>();
        String message = "获取告警分类菜单成功！";
        
        List<DataInfo> dataInfos = null;
        try {
            // 通过mysql获取告警分类菜单栏节点
            dataInfos = dataInfoRepository.findDatasByIsData(0);
        } catch (Exception e) {
            e.printStackTrace();
            responseMap.put("result", "fail");
            responseMap.put("message", "获取告警分类菜单节点出错！");
            return responseMap;
        }
        
        if (dataInfos == null || dataInfos.size() < 1){
            responseMap.put("result", "fail");
            responseMap.put("message", "获取告警分类菜单节点，返回为空");
            return responseMap;
        }
        StringBuffer nodeHtml = new StringBuffer();
        
        List<DataInfo> nodes = new ArrayList<DataInfo>();
        for (int i = 0; i < dataInfos.size(); i++) {
            DataInfo dataInfo = dataInfos.get(i);
            if (dataInfo.getParentId() == 0){
                nodes.add(dataInfo);
            }
        }
        
        for (int i = 0; i < nodes.size(); i++) {
            DataInfo dataInfo = nodes.get(i);
            nodeHtml.append("<h3>"+ dataInfo.getName() +"</h3>");
            long id = dataInfo.getId();
            List<DataInfo> childNodes = getChildNode(dataInfos, id);
            if (childNodes != null && childNodes.size() != 0){
                for (int j = 0; j < childNodes.size(); j++) {
                    DataInfo childDataInfo = childNodes.get(j);
                    long childId = childDataInfo.getId();
                    List<DataInfo> childNodes2 = getChildNode(dataInfos, childId);
                    if (childNodes2 != null && childNodes2.size() != 0){
                        for (int k = 0; k < childNodes2.size(); k++) {
                            DataInfo childDataInfo2 = childNodes2.get(k);
                            if (k == 0){
                                nodeHtml.append("<div class='task'>");
                                nodeHtml.append("<h4><a role='button' class='alarmType'><span class='glyphicon glyphicon-menu-down'"
                                        + " aria-hidden='true'></span> "+ childDataInfo.getName() +"</a></h4><ul class='warning-type'>");
                            }
                            // 查询es获取告警等级的个数
                            long oneLevelNumber = 0 ;
                            long twoLevelNumber = 0 ;
                            long threeLevelNumber = 0 ;
                            if (childDataInfo.getSubName() != null){
                                try {
									oneLevelNumber = queryLevelNumberByEs(childDataInfo2.getName(), childDataInfo.getSubName(), "-");
									twoLevelNumber = queryLevelNumberByEs(childDataInfo2.getName(), childDataInfo.getSubName(), "-");
									threeLevelNumber = queryLevelNumberByEs(childDataInfo2.getName(), childDataInfo.getSubName(), "1");
								} catch (Exception e) {
									e.printStackTrace();
	                                message = "通过es获取告警等级个数出错！";
								}
                            }
                            nodeHtml.append("<li>"+ childDataInfo2.getName() +"<div class='right'><span class='badge type1'>"+ oneLevelNumber +"</span>"
                                    + "<span class='badge type2'>"+ twoLevelNumber +"</span>"
                                    + "<span class='badge type3'>"+ threeLevelNumber +"</span></div></li>");
                            
                            if (k == childNodes2.size() - 1){
                                nodeHtml.append("</ul>");
                                nodeHtml.append("</div>");
                            }
                        }
                        
                    }else{  // 最后一级
                        if (j == 0){
                            nodeHtml.append("<div class='task'>");
                            nodeHtml.append("<ul class='warning-type'>");
                        }
                        // 查询es获取告警等级的个数
                        long oneLevelNumber = 0 ;
                        long twoLevelNumber = 0 ;
                        long threeLevelNumber = 0 ;
                        if (childDataInfo.getSubName() != null){
                            try {
                                oneLevelNumber = queryLevelNumberByEs(childDataInfo.getName(), childDataInfo.getSubName(), "-");
                                twoLevelNumber = queryLevelNumberByEs(childDataInfo.getName(), childDataInfo.getSubName(), "-");
                                threeLevelNumber = queryLevelNumberByEs(childDataInfo.getName(), childDataInfo.getSubName(), "1");
                            } catch (Exception e) {
                                e.printStackTrace();
                                message = "通过es获取告警等级个数出错！";
                            }
                        }
                        nodeHtml.append("<li>"+ childDataInfo.getName() +"<div class='right'><span class='badge type1'>"+ oneLevelNumber +"</span>"
                                + "<span class='badge type2'>"+ twoLevelNumber +"</span>"
                                + "<span class='badge type3'>"+ threeLevelNumber +"</span></div></li>");
                        if (j == childNodes.size() - 1){
                            nodeHtml.append("</ul>");
                            nodeHtml.append("</div>");
                        }
                    }
                }
            }
        }
        
        responseMap.put("result", "success");
        responseMap.put("resultData", nodeHtml);
        responseMap.put("message", message);
        return responseMap;
    }
    
    public Object getAllAlarm(HttpServletRequest request) {
    	String sort = request.getParameter("sort");
		String order = request.getParameter("order");
		String limitStr = request.getParameter("limit");
		String offsetStr = request.getParameter("offset");
    	String intervalTimeStr = request.getParameter("intervalTimeStr");
    	
    	int limit = 10;
		int offset = 0;
		try {
			offset = Integer.parseInt(offsetStr);
			limit = Integer.parseInt(limitStr);
		} catch (Exception e) {
			logger.error("页数和显示条数转int报错"+ e.toString());
		}
    	
    	if (StringUtils.isEmpty(intervalTimeStr)){
    		intervalTimeStr = "12";
    	}
    	int intervalTime = Integer.parseInt(intervalTimeStr);
    	Calendar cal = Calendar.getInstance();
    	// 时间往前推多少小时
    	cal.add(Calendar.HOUR_OF_DAY, -intervalTime);
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String showTime = sdf.format(cal.getTime());
    	return queryAllAlarmByEs(showTime, limit, offset, sort, order);
	}
    
    /**
     * @Description: TODO(获取子节点) 
     * @author HYW
     * @date 2018年3月13日 上午11:11:50 
     */
    public List<DataInfo> getChildNode(List<DataInfo> dataInfos ,long id){
        if (dataInfos == null || dataInfos.size() == 0){
            return null;
        }
        List<DataInfo> childNodes = new ArrayList<DataInfo>();
        for (int i = 0; i < dataInfos.size(); i++) {
            DataInfo dataInfo = dataInfos.get(i);
            if (id == dataInfo.getParentId()){
                childNodes.add(dataInfo);
            }
        }
        return childNodes;
    }

    /**
     * @Description: TODO(通过es查询不同告警等级的个数) 
     * @author HYW
     * @date 2018年3月14日 上午11:25:47 
     */
    public long queryLevelNumberByEs(String module, String groupId, String level){
        // 创建一个查询类
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 添加查询条件
        queryBuilder.must(QueryBuilders.termQuery("dataName", module));
        queryBuilder.must(QueryBuilders.wildcardQuery("groupId", "*_"+ groupId +"_*"));
        queryBuilder.must(QueryBuilders.termQuery("level", level));
        
        // 历史最多三天，三个index
    	String[] queryIndex = new String[3];
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	for (int i = 0; i < 3; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -i);
			String index = "data_" + sdf.format(cal.getTime());
			queryIndex[i] = index;
		}
        
        SearchRequestBuilder requestBuilder = es.client.prepareSearch(queryIndex)
                .setTypes(new String[]{"alert"})
                .setSearchType(SearchType.DEFAULT)
                .setQuery(queryBuilder);
        
        SearchResponse response = requestBuilder.setScroll(new TimeValue(2000)).get();
        
        long levelNumber = response.getHits().getTotalHits();
        
        return levelNumber;
    }
    
    /**
     * @Description: TODO(查询所有的告警事件) 
     * @author HYW
     * @date 2018年3月21日 上午15:25:47 
     */
    public Object queryAllAlarmByEs(String showTime, int limit,
    		int offset, String sort, String order){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	List<Map<String, Object>> rows = new ArrayList<Map<String,Object>>();
    	long total = 0;
    	// 创建查询类
    	BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
    	
    	RangeQueryBuilder rangequery = QueryBuilders.rangeQuery("occur_time");
    	rangequery.gte(showTime);
    	queryBuilder.must(rangequery);
    	
    	// 历史最多三天，三个index
    	String[] queryIndex = new String[3];
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	for (int i = 0; i < 3; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -i);
			String index = "data_" + sdf.format(cal.getTime());
			queryIndex[i] = index;
		}
    	
    	SearchRequestBuilder requestBuilder = es.client.prepareSearch(queryIndex)
    		.setTypes(new String[]{"alert"})
    		.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
    		.setQuery(queryBuilder)
    		.setFrom(offset)
    		.setSize(limit);
    	
    	// 排序
    	if (!StringUtils.isEmpty(sort)){
    		if ("asc".equals(order)){
    			requestBuilder.addSort(sort, SortOrder.ASC);
    		}else{
    			requestBuilder.addSort(sort, SortOrder.DESC);
    		}
    	}else{
    		requestBuilder.addSort("occur_time", SortOrder.DESC);
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
