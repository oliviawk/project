package hitec.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Null;

import hitec.domain.Alert_strategy;
import hitec.domain.User_Catalog;
import hitec.repository.*;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import hitec.domain.DataInfo;
import hitec.domain.DataSourceSetting;
import hitec.feign.client.DataSourceEsInterface;
import org.springframework.transaction.annotation.Transactional;

@Service("DataSourceSettingService")
public class DataSourceSettingService {

	private static final Logger logger = LoggerFactory.getLogger(DataSourceSettingService.class);
	
	@Autowired
	ESRepository es;
	@Autowired
	DataSourceSettingRepository dataSourceSettingRepository;
	@Autowired
	DataSourceEsInterface dataSourceEsInterface;
	@Autowired
	DataInfoRepository dataInfoRepository;
	@Autowired
	User_Catalog_Repository user_catalog_repository;
    @Autowired
	Alert_strategy_Repository alert_strategy_repository;
	@Value("${es.indexHeader}")
	public String indexHeader;
	
	public List<Map<String, Object>> getPossibleNeedDataFileName(){
		List<Map<String, Object>> outData = new ArrayList<Map<String, Object>>();
		
		SearchRequestBuilder requestBuilder = es.client.prepareSearch(indexHeader +"possible_needed_data")
	    		.setTypes(new String[]{"POSSIBLE_NEEDED_DATA"})
	    		.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
	    		.setQuery(null)
	    		.setSize(100);
		
		
		SearchResponse response = requestBuilder.setScroll(new TimeValue(2000)).get();
    	
		do {
			for (SearchHit hits : response.getHits().getHits()) {
	            try {
	            	Map<String, Object> data = new HashMap<String, Object>();
	            	Map<String, Object> source = hits.getSource();
	            	data.put("id", hits.getId());
	            	data.put("fileName", source.get("fileName"));
	            	outData.add(data);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
			response = es.client.prepareSearchScroll(response.getScrollId())
                    .setScroll(new TimeValue(2000))
                    .execute().actionGet();
        } while(response.getHits().getHits().length != 0);
		
		return outData;
	}

	public DataSourceSetting insertDataSource(HttpServletRequest request) throws  RuntimeException{
		DataSourceSetting dataSourceSetting = new DataSourceSetting();

		DataSourceSetting backData = null;
//		try {
			logger.info(JSON.toJSONString(request.getParameterMap()));

			dataSourceSetting.setName(request.getParameter("name"));
			dataSourceSetting.setFileName(request.getParameter("fileName"));
			dataSourceSetting.setTimeFormat(request.getParameter("timeFormat"));
			dataSourceSetting.setDirectory(request.getParameter("directory"));
			dataSourceSetting.setSendUser(request.getParameter("senderUser"));
			dataSourceSetting.setDataType(request.getParameter("dataType"));
			dataSourceSetting.setIpAddr(request.getParameter("ipAddr"));
			dataSourceSetting.setPhone(request.getParameter("phone"));
			dataSourceSetting.setDepartmentName(request.getParameter("departmentName"));
			dataSourceSetting.setUseDepartment(request.getParameter("useDepartment"));
			dataSourceSetting.setMoniterTimer(request.getParameter("moniterTimer"));
			/**取出所有的DataSourcesSetting和添加的数据对比查看是否存在,如果存在则直接返回并提示数据已存在！**/
			List<DataSourceSetting> dataSourceSettingList=dataSourceSettingRepository.findAll();
			for (DataSourceSetting dataSourceSettingone :dataSourceSettingList){
				//根据用户ip，用户名，文件名和文件路径判断是否相同，如果四个条件同时满足那就是同一条数据， 同时数据名字不能相同，只要名字相同即视为同一条数据.
				if (dataSourceSetting.getName().equals(dataSourceSettingone.getName()) ||
						(dataSourceSetting.getFileName().equals(dataSourceSettingone.getFileName())&&
						dataSourceSetting.getDirectory().equals(dataSourceSettingone.getDirectory())&&
						dataSourceSetting.getIpAddr().equals(dataSourceSettingone.getIpAddr())&&
						dataSourceSetting.getSendUser().equals(dataSourceSettingone.getSendUser()))
						){
					logger.error("添加的元数据已存在，不能重复添加！！！");
					DataSourceSetting dataSourceSettinghuaxin= new DataSourceSetting();
					dataSourceSettinghuaxin.setSendUser("DataSourceSetting数据重复");
					return dataSourceSettinghuaxin;
				}
			}
			backData = dataSourceSettingRepository.save(dataSourceSetting);
//		} catch (Exception e) {
//			StringWriter sw = new StringWriter();
//			PrintWriter pw = new PrintWriter(sw);
//			e.printStackTrace(pw);
//			logger.error("添加元数据报错："+ sw.toString());
//			return null;
//		}
		return backData;
	}
	
	/**
	* @Description 删除es中可能数据 TODO <pre>
	* @author HuYiWu <pre>
	* @date 2018年4月25日 下午7:06:19 <pre>
	 */
	public String deleteImpossibleData(String deleteId){
		if (StringUtils.isNotEmpty(deleteId)){
			Map<String, Object> deleteData = new HashMap<String, Object>();
			deleteData.put("_index", indexHeader +"possible_needed_data");
			deleteData.put("_type", "POSSIBLE_NEEDED_DATA");
			deleteData.put("_id", deleteId);
			Map<String, Object> backData = dataSourceEsInterface.deleteByid(JSON.toJSONString(deleteData));
			System.out.println(JSON.toJSONString(backData));
			if (backData != null && "success".equals(String.valueOf(backData.get("result")))){
				return "success";
			}else{
				return "模板库删除失败，"+ String.valueOf(backData.get("message"));
			}
		}
		return "模板库删除失败，ID为空";
	}

	public List<String> getPossibleNeedDataIpAddr() {
		List<String> outData = new ArrayList<String>();
		
		AggregationBuilder aggregation = AggregationBuilders.terms("groupBy").field("ipAddr");
		
		SearchRequestBuilder requestBuilder = es.client.prepareSearch(indexHeader +"possible_needed_data")
	    		.setTypes(new String[]{"POSSIBLE_NEEDED_DATA"})
	    		.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
	    		.setQuery(null)
	    		.addAggregation(aggregation)
	    		.setSize(100);
		
		SearchResponse response = requestBuilder.setExplain(false).get();
    	
		Terms terms = response.getAggregations().get("groupBy");
        List<Bucket> buckets = terms.getBuckets();
        for(Bucket bucket:buckets){
        	outData.add(String.valueOf(bucket.getKey()));
        }
		
		return outData;
	}

	public List<String> getPossibleNeedDataSendUserByIpAddr(String ipAddr) {
		List<String> outData = new ArrayList<String>();
		
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery.must(QueryBuilders.termQuery("ipAddr", ipAddr));
		
		AggregationBuilder aggregation = AggregationBuilders.terms("groupBy").field("sendUser");
		
		SearchRequestBuilder requestBuilder = es.client.prepareSearch(indexHeader +"possible_needed_data")
	    		.setTypes(new String[]{"POSSIBLE_NEEDED_DATA"})
	    		.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
	    		.setQuery(boolQuery)
	    		.addAggregation(aggregation)
	    		.setSize(100);
		
		SearchResponse response = requestBuilder.setExplain(false).get();
    	
		Terms terms = response.getAggregations().get("groupBy");
        List<Bucket> buckets = terms.getBuckets();
        for(Bucket bucket:buckets){
        	outData.add(String.valueOf(bucket.getKey()));
        }
		
		return outData;
	}

	public List<Map<String, Object>> getPossibleNeedDataFileNameByIpAddrAndSendUser(String ipAddr,
			String sendUser) {
		List<Map<String, Object>> outData = new ArrayList<Map<String, Object>>();
		
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery.must(QueryBuilders.termQuery("ipAddr", ipAddr));
		boolQuery.must(QueryBuilders.termQuery("sendUser", sendUser));
		
		SearchRequestBuilder requestBuilder = es.client.prepareSearch(indexHeader +"possible_needed_data")
	    		.setTypes(new String[]{"POSSIBLE_NEEDED_DATA"})
	    		.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
	    		.setQuery(boolQuery)
	    		.setSize(100);
		
		SearchResponse response = requestBuilder.setScroll(new TimeValue(2000)).get();
    	
		do {
			for (SearchHit hits : response.getHits().getHits()) {
	            try {
	            	Map<String, Object> data = new HashMap<String, Object>();
	            	Map<String, Object> source = hits.getSource();
	            	data.put("id", hits.getId());
	            	data.put("fileName", source.get("fileName"));
	            	outData.add(data);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
			response = es.client.prepareSearchScroll(response.getScrollId())
                    .setScroll(new TimeValue(2000))
                    .execute().actionGet();
        } while(response.getHits().getHits().length != 0);

		return outData;
	}

	public List<Map<String, Object>> getPossibleNeedDataFileNameByIpAddrAndSendUserAndFileName(String ipAddr, String sendUser,String fileName) {
		List<Map<String, Object>> outData = new ArrayList<Map<String, Object>>();

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery.must(QueryBuilders.termQuery("ipAddr", ipAddr));
		boolQuery.must(QueryBuilders.termQuery("sendUser", sendUser));
		boolQuery.must(QueryBuilders.wildcardQuery("fileName","*"+fileName+"*"));

		SearchRequestBuilder requestBuilder = es.client.prepareSearch(indexHeader +"possible_needed_data")
				.setTypes(new String[]{"POSSIBLE_NEEDED_DATA"})
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(boolQuery)
				.setSize(30);

		SearchResponse response = requestBuilder.setScroll(new TimeValue(1000)).get();

		for (SearchHit hits : response.getHits().getHits()) {
			try {
				Map<String, Object> data = new HashMap<String, Object>();
				Map<String, Object> source = hits.getSource();
				data.put("id", hits.getId());
				data.put("fileName", source.get("fileName"));
				outData.add(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return outData;
	}


	public DataInfo insertDataInfo(HttpServletRequest request) throws RuntimeException{
		//1.判断是否存在当前类型数据
		List<DataInfo> findTypeDatasByParentId = dataInfoRepository.findDatasByParentId(3003);
		String insertDateType = request.getParameter("dataType");
		String filename= request.getParameter("fileName");
		String timeformat=request.getParameter("timeFormat");
		if (timeformat!=null||timeformat.equals("")){
			String timeregx="\\d{"+timeformat.length()+"}";
			filename=filename.replace(timeregx,"{"+timeformat+"}");
		}
		long insertId = -1;
		long lastId = -1;
		for (int i = 0; i < findTypeDatasByParentId.size(); i++) {
			DataInfo dataInfo = findTypeDatasByParentId.get(i);
			if (dataInfo != null && dataInfo.getName().equals(insertDateType)){
				insertId = dataInfo.getId();
				break;
			}
			if (i == findTypeDatasByParentId.size() - 1){
				lastId = dataInfo.getId();
			}
		}
		if (insertId == -1){//不存在当前类型，需先入库类型
			insertId = lastId + 1;
			DataInfo insertType = new DataInfo();
			insertType.setId(insertId);
			insertType.setParentId(3003);
			insertType.setName(insertDateType);
			
			DataInfo backData = null;
//			try {
				backData = dataInfoRepository.save(insertType);

//			} catch (Exception e) {
//				StringWriter sw = new StringWriter();
//				PrintWriter pw = new PrintWriter(sw);
//				e.printStackTrace(pw);
//				logger.error("插入一条数据源类型出错,"+ sw.toString());
//				return null;
//
//			}
			if (backData != null){	//插入类型成功，继续插入数据，新插入类型下没有数据，id从parentId * 1000 + 1开始
				DataInfo insertData = new DataInfo();
				insertData.setId(insertId * 1000 + 1);
				insertData.setParentId(insertId);
				String name = request.getParameter("name");
				insertData.setName(name);
				insertData.setSubName(name);
				insertData.setMonitorTimes(request.getParameter("moniterTimer"));
				insertData.setShouldTime("0");
                insertData.setFileNameDefine(filename);
				insertData.setTimeoutThreshold("0");
				insertData.setRegular(1);
				insertData.setIp(request.getParameter("ipAddr"));
				insertData.setFilePath(request.getParameter("directory"));
				insertData.setModule("DS");
				insertData.setServiceType("DATASOURCE");
				insertData.setAlertLevel(1);
				insertData.setStartMoniter("yes");
				insertData.setIsData(1);
//				try {
					backData = dataInfoRepository.save(insertData);
//				} catch (Exception e) {
//					StringWriter sw = new StringWriter();
//					PrintWriter pw = new PrintWriter(sw);
//					e.printStackTrace(pw);
//					logger.error("插入一条数据源类型出错,"+ sw.toString());
//					return null;
//				}
			}
			return backData;
		}
		
		//2.判断是否存在当前这条数据，根据识别类型、ip、环节判断
		List<DataInfo> datasByParentId = dataInfoRepository.findDatasByParentId(insertId);
		String key = request.getParameter("name");

		long lastInsertId = insertId * 1000 + 1;
		for (int i = 0; i < datasByParentId.size(); i++) {
			DataInfo baseDataInfo = datasByParentId.get(i);
			String baseKey = baseDataInfo.getName() ;
			if (StringUtils.isNotEmpty(key) && key.equals(baseKey)){//数据已经存在  不用再次插入
				return baseDataInfo;
			}
			if (i == datasByParentId.size() -1){
				lastInsertId = baseDataInfo.getId() + 1;
			}
		}
		
		//3.保存当前数据
		DataInfo insertData = new DataInfo();
		insertData.setId(lastInsertId);
		insertData.setParentId(insertId);
		String name = request.getParameter("name");
		insertData.setName(name);
		insertData.setSubName(name);
		insertData.setMonitorTimes(request.getParameter("moniterTimer"));
		insertData.setShouldTime("0");
		insertData.setTimeoutThreshold("0");
		insertData.setRegular(1);
		insertData.setFileNameDefine(filename);
		insertData.setIp(request.getParameter("ipAddr"));
		insertData.setFilePath(request.getParameter("directory"));
		insertData.setModule("DS");
		insertData.setServiceType("DATASOURCE");
		insertData.setAlertLevel(1);
		insertData.setStartMoniter("yes");
		insertData.setIsData(1);

		DataInfo backData = null;
//		try {
			backData = dataInfoRepository.save(insertData);

//		} catch (Exception e) {
//			StringWriter sw = new StringWriter();
//			PrintWriter pw = new PrintWriter(sw);
//			e.printStackTrace(pw);
//			logger.error("插入一条数据源数据出错,"+ sw.toString());
//			return null;
//		}
		return backData;
	}

	public Object getDataSourceSettingData(int offset, int limit) {
		List<DataSourceSetting> findAll = dataSourceSettingRepository.findAll(offset,limit);
		int size = dataSourceSettingRepository.findAll().size();
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("rows", findAll);
		jsonObj.put("total", size);
		return jsonObj;
	}
	@Transactional(rollbackFor = {RuntimeException.class})
	public String EditDataSource(DataSourceSetting dataSourceSetting,ArrayList<DataSourceSetting> list)throws RuntimeException {
		String message = "fail";
		DataInfo dataInfo=null;
		String name=dataSourceSetting.getName();
		String ip=dataSourceSetting.getIpAddr();
		String filepath=dataSourceSetting.getDirectory();
		for (DataSourceSetting dataSourceSetting1:list){
			String beforefilepath=dataSourceSetting1.getDirectory();
			String beforefilename=dataSourceSetting1.getFileName();
			String beforetimeformat=dataSourceSetting1.getTimeFormat();
			String beforeip=dataSourceSetting1.getIpAddr();
			String beforename=dataSourceSetting1.getName();

			if(beforetimeformat!=null&&beforefilename!=null&&beforetimeformat!=""&&beforefilename!=""){
				int leng=beforetimeformat.length();
				String regx="\\d{"+leng+"}";
				beforefilename=beforefilename.replace(regx,"{"+beforetimeformat+"}");
			}

				logger.info("名字："+beforename+"ip："+beforeip+"文件名："+beforefilename+"路径："+beforefilepath);
				dataInfo=dataInfoRepository.findDatainfo(beforename,beforeip,beforefilename,beforefilepath);
			if (null==dataInfo){
				logger.error("查询数据出错！！");
				return  message;
			}
		}
		//	insertData.setId(lastInsertId);
//			insertData.setParentId(insertId);
		dataInfo.setName(name);
		dataInfo.setSubName(name);
		dataInfo.setMonitorTimes(dataSourceSetting.getMoniterTimer());
//			insertData.setShouldTime("0");
//			insertData.setTimeoutThreshold("0");
//			insertData.setRegular(1);
//			dataInfo.setFileNameDefine(filename);
		dataInfo.setIp(ip);
		dataInfo.setFilePath(filepath);
//			insertData.setModule("DS");
//			insertData.setServiceType("DATASOURCE");
//			insertData.setAlertLevel(1);
//			insertData.setStartMoniter("yes");
//			 insertData.setIsData(1);
		DataInfo backData = null;
		backData = dataInfoRepository.save(dataInfo);
		DataSourceSetting save = dataSourceSettingRepository.save(dataSourceSetting);
		if(save!=null){
			message = "success";
		}
		return message;
	}
	@Transactional(rollbackFor = {RuntimeException.class})
	public List<DataInfo> deleteDataSource(ArrayList<DataSourceSetting> arrayList) throws RuntimeException {

		dataSourceSettingRepository.delete(arrayList);
		List<DataInfo> dataInfoList =new ArrayList<DataInfo>();
		int shu=0;
		int shutwo=0;
		long pk_id=-1;
		    for (int i=0;i<arrayList.size();i++){
		    	logger.info("开始删除datainfo数据");
		    	DataSourceSetting dataSourceSetting=arrayList.get(i);
		    	String filename=dataSourceSetting.getFileName();
		    	String timeformat=dataSourceSetting.getTimeFormat();
		    	if (timeformat!=null && !timeformat.equals("")){
					String regx="\\d{"+dataSourceSetting.getTimeFormat().length()+"}";
					if (filename.indexOf(regx)!=-1){
						filename=filename.replace(regx,"{"+timeformat+"}");
					}
				}
				else {
		    		logger.info("时间格式为空--"+dataSourceSetting.getTimeFormat());
				}

		    	  DataInfo dataInfo=dataInfoRepository.findDatainfo(dataSourceSetting.getName(),dataSourceSetting.getIpAddr(),filename,dataSourceSetting.getDirectory());

		    	  if(dataInfo!=null){
		    	  	  dataInfoList.add(dataInfo);
//				         try {
					          shu=alert_strategy_repository.delectAlert_strategy(pk_id);
//						 } catch (Exception e) {
//					       StringWriter sw = new StringWriter();
//					       PrintWriter pw = new PrintWriter(sw);
//					       e.printStackTrace(pw);
//					       logger.error("删除Alert_strategy一条数据源类型出错,"+ sw.toString());
//				       }
			   }
			   else {
			   	logger.info("删除Alert_stratergy_Repository失败未查询到对应的数据");
			   	throw  new RuntimeException("无对应数据");
			   }
//				try {
					shutwo =dataInfoRepository.deletedatainfo(dataSourceSetting.getName(),dataSourceSetting.getIpAddr(),filename,dataSourceSetting.getDirectory());
//				} catch (Exception e) {
//					StringWriter sw = new StringWriter();
//					PrintWriter pw = new PrintWriter(sw);
//					e.printStackTrace(pw);
//					logger.error("删除datainfo一条数据源类型出错,"+ sw.toString());
//				}
				logger.info(filename+"个数："+shutwo);

		    }
		return  dataInfoList;
	}

	public User_Catalog findAll_User_catalog(String user_name,String user_ip){
		return user_catalog_repository.findAll_User_catalog(user_name,user_ip);
	}
	@Transactional(rollbackFor = {RuntimeException.class})
	public Map<String, Object> InsertData(HttpServletRequest request,Map<String, Object> outData) throws RuntimeException {
//		String type = "success";
//		String message = "成功";
		DataInfo dataInfo = insertDataInfo(request);// 添加datainfo为了预生成数据
		if (dataInfo == null) {
			outData.put("type", "fail");
			outData.put("message", "添加数据返回为空");
			return outData;
		}
		DataSourceSetting insertResult = insertDataSource(request);// 添加数据库
		if (insertResult == null) {
			outData.put("type", "fail");
			outData.put("message", "添加元数据返回为空");
			return outData;
		}
		if (insertResult.getSendUser().equals("DataSourceSetting数据重复")){
			outData.put("type", "fail");
			outData.put("message", "添加的元数据已存在不能重复添加！！！");
			return outData;
		}
		return  outData;
	}
}
