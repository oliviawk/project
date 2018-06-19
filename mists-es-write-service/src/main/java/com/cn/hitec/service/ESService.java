package com.cn.hitec.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.cn.hitec.bean.AlertBeanNew;
import com.cn.hitec.repository.jpa.DataInfoRepository;
import com.cn.hitec.tools.AlertType;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetResponse;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.hitec.repository.ESRepository;
import com.cn.hitec.tools.Pub;


/**
 * @ClassName:
 * @Description:
 * @author: fukl
 * @data: 2017年08月3日 下午1:14
 */
@Slf4j
@Service
public class ESService {
	@Autowired
	private ESRepository es;
	@Autowired
	ESClientAdminService esClientAdminService;
	@Autowired
	AlertService alertService;

	@Autowired
	DataInfoRepository dataInfoRepository;

	/**
	 * 添加数据 自动生成id
	 * 
	 * @param index
	 * @param type
	 */
	public int add(String index, String type, String id,List<String> listJson) {
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
				if (StringUtils.isEmpty(id)){
					es.bulkProcessor.add(new IndexRequest(index, type).source(json, XContentType.JSON));
				}else{
					es.bulkProcessor.add(new IndexRequest(index, type,id).source(json, XContentType.JSON));
				}
				// System.out.println(json);

			}
			// System.out.println("清理缓存！");
			// es.bulkProcessor.flush();
		} catch (Exception e) {
			e.printStackTrace();
            log.error(e+"");
		} finally {
			return listSize - error_num;
		}

	}

	/**
	 * 添加数据
	 * 
	 * @param index
	 * @param type
	 * @param listJson
	 * @return
	 */
	public int insert(String index, String type, List<String> listJson) {
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
				es.client.prepareIndex(index, type).setSource(json, XContentType.JSON).get();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return listSize - error_num;
		}

	}

	/**
	 * 添加数据
	 * 
	 * @param index
	 * @param listJson
	 * @return
	 */

	public int insert1(String index, List<String> listJson) {
		int error_num = 0;
		int listSize = 0;
		String type = "";
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
				if (StringUtils.countOccurrencesOf(json, "serviceType") > 0) {// 判断json是否为空
					JSONObject jsob = JSONObject.parseObject(json);

					type = jsob.get("serviceType").toString();
					String strType = jsob.get("type").toString();
					JSONObject obj = JSONObject.parseObject(jsob.get("fields").toString());
					String strIp = obj.get("ip_addr").toString();
					String strModule = obj.get("module").toString();
					String strDataTime = obj.get("data_time").toString();

					String strid =  Pub.MD5(type+","+strType+","+strModule+","+strIp+","+strDataTime);
					if(StringUtils.isEmpty(type) || StringUtils.isEmpty(strid)){
						error_num++;
						continue;
					}
					// 去掉serviceType,避免表中出现内容重复
					jsob.remove("serviceType");
					json = jsob.toJSONString();
//					System.out.println(index+","+type+","+strid);
//					if ("LAPS".equals(type)){
//						System.out.println(json);
//					}
					//判断数据是否被修改过(aging_status 不是'未处理'状态 ，表示为修改过)，如果修改过，则不再修改
					Map<String, Object> tempMap = getDocumentById(new String[]{index},type,strid);
					if (tempMap.containsKey("aging_status") && !tempMap.get("aging_status").equals("未处理")){
						log.info("已修改："+type+","+strType+","+strModule+","+strIp+","+strDataTime);
						continue;
					}
					IndexResponse response = es.client.prepareIndex(index, type,strid).setSource(json, XContentType.JSON).get();
					if(response.status().getStatus() != 201){
						error_num ++;
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return listSize - error_num;
		}

	}

	/**
	 * 添加数据 指定id
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @param json
	 */
	public void add(String index, String type, String id, String json) {

		try {
			es.bulkProcessor.add(new IndexRequest(index, type, id).source(json, XContentType.JSON));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加数据 返回IndexResponse
	 * 
	 * @param index
	 * @param type
	 * @param json
	 */
	public String add_resultId(String index, String type, String json) {
		String strId = null;
		IndexResponse response = null;
		try {
			if (StringUtils.isEmpty(json)) {
				return null;
			}
			response = es.client.prepareIndex(index, type).setSource(json, XContentType.JSON).get();
			strId = response.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return strId;
	}

	public int update_field(String index, String type, String id, Map<String, Object> params) {
		int res = -1;
		try {
			XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();
            xContentBuilder.startObject();
			for (String strKey : params.keySet()) {
				xContentBuilder.field(strKey, params.get(strKey));
			}
			xContentBuilder.endObject();

			UpdateRequest updateRequest = new UpdateRequest();
			updateRequest.index(index);
			updateRequest.type(type);
			updateRequest.id(id);
			updateRequest.doc(xContentBuilder);
			UpdateResponse updateResponse = es.client.update(updateRequest).get();
			String resStatus = updateResponse.status().toString();
			if ("OK".equals(resStatus)) {
				res = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}


	/**
	 * 过滤重复数据（相同类型和时次的数据） 目前的主要数据录入类
	 * @desc 2018.3.20 修改
	 * @param index
	 * @param type
	 */
	public int update(String index, String type, List<String> listJson) {
		int error_num = 0;
		int listSize = 0;
		try {
			if (listJson == null || listJson.size() < 1) {
				log.error("参数为空");
				return 0;
			}
			listSize = listJson.size();
			Map<String, Object> map = null;
			Map<String, Object> fields = null;
//			Map<String, Object> DIMap = null;

			for (String json : listJson) {
				try {
					if (StringUtils.isEmpty(json)) {
						log.error("数据为空");
						error_num++;
						continue;

					}
					//给关键变量赋值
					map = JSON.parseObject(json);
					fields = (Map<String, Object>) map.get("fields");

					String subType = map.get("type").toString(); // 数据名称
					String subModule = fields.get("module").toString();
					String subIp = fields.get("ip_addr").toString();
					String subKey = type+","+subType+","+subModule+","+subIp;
					String str_id = Pub.MD5(subKey + "," + fields.get("data_time"));

					Date dataTimeIndex = Pub.transform_StringToDate(fields.get("data_time").toString(),
							"yyyy-MM-dd HH:mm:ss.SSSZ");
					index = Pub.Index_Head + Pub.transform_DateToString(dataTimeIndex, Pub.Index_Food_Simpledataformat);

					List<Object> curmodules = dataInfoRepository.findAlertRules(type,subModule,subType,subIp);
					JSONArray rulesArray = JSON.parseArray(JSON.toJSONString(curmodules));
					if(rulesArray.size() > 0){
						rulesArray = (JSONArray)rulesArray.get(0);
					}

					// 如果是定时 有规律的数据， 需要查询后入库
					Map<String, Object> resultMap = new HashMap<>();
					if (Pub.alert_time_map.containsKey(subKey)) {
						String[] indices = null;
						if ("T639".equals(subType) || "风流场".equals(subType)) { // 风流场数据，不需要判断时效性
							Date tempDate = Pub.transform_StringToDate(fields.get("data_time").toString(),
									"yyyy-MM-dd HH:mm:ss.SSSZ");
							indices = new String[] { Pub.Index_Head + Pub.transform_DateToString(tempDate, Pub.Index_Food_Simpledataformat) };
						} else {
							indices = Pub.getIndices(dataTimeIndex, 1); // 获取今天和昨天的
																	// index
						}
						resultMap = getDocumentById(indices, type, str_id) ;

					} else {
						map.put("aging_status", "正常");
						if(fields.containsKey("event_status")){
							// 判断数据状态
							if (!fields.get("event_status").toString().toUpperCase().equals("OK")
									&& !fields.get("event_status").toString().equals("0")) {
								map.put("aging_status", "异常");
							}
						}

						if(rulesArray.size() > 0 && rulesArray.getString(0) != null){
							if("异常".equals(map.get("aging_status"))){
								dataInfoRepository.addAlertCnt(rulesArray.getLongValue(0));
							}
							else if(rulesArray.getLongValue(3) != 0){
								dataInfoRepository.resetAlertCnt(rulesArray.getLongValue(0));
							}

						}


//						log.info("这是一条非定时数据,类型为：{}, 时次为：{}", subType, fields.get("data_time"));
						es.bulkProcessor.add(new IndexRequest(index, type,str_id).source(json, XContentType.JSON));
						continue;
					}
					if (resultMap.containsKey("_id")) { // 如果查询到id
//						log.info("这是预生成数据,类型为：{}, 时次为：{}", subType, fields.get("data_time"));
						AlertBeanNew alertBean = null;
						String alertType = "alert";
						index = resultMap.get("_index").toString();
						type = resultMap.get("_type").toString();
						Map<String, Object> hitsSource_fields = (Map<String, Object>) resultMap.get("fields");

						// 过滤掉不应该有的数据
						if ("21".equals(fields.get("event_status"))) {
							continue;
						}

						if ("T639".equals(subType) || "风流场".equals(subType)) { // 如果是风场数据  要与其他定时数据分开分析
							// 确定是否进行 数据状态 告警
							if (fields.get("event_status").toString().toUpperCase().equals("OK")
									|| fields.get("event_status").toString().equals("0")) {
								map.put("aging_status", "正常");
								// fields.put("event_info","正常");
							} else {
								// 判断如果原数据是正确的， 新数据是错误的， 舍弃新数据
								if (hitsSource_fields.containsKey("event_status")
										&& (hitsSource_fields.get("event_status").toString().toUpperCase().equals("OK")
												|| hitsSource_fields.get("event_status").toString().equals("0"))) {
									log.warn("--舍弃掉 预修改错误的数据：" + json);
									continue;
								}
								map.put("aging_status", "异常");
								String alertTitle = subType + "--" + fields.get("module") + "--"
										+ fields.get("data_time") + " 时次产品 ，发生错误："
										+ fields.get("event_info").toString();

								// 初始化告警实体类
								alertBean = alertService.getAlertBean(AlertType.ABNORMAL.getValue(), alertTitle,type, map);
							}
						}
						// 当 数据库里的数据 和 当前数据 一样时（目前是按照数据状态来判断），放弃掉该条数据
						else if (hitsSource_fields.containsKey("event_status") && fields.get("event_status").toString()
								.equals(hitsSource_fields.get("event_status"))) {
							log.warn("--舍弃掉 相同的数据：" + json);
							continue;
						} else {
							// 将 应到时间 和 最晚到达时间，添到的数据中
							map.put("should_time",
									resultMap.containsKey("should_time") ? resultMap.get("should_time") : "");
							map.put("last_time", resultMap.containsKey("last_time") ? resultMap.get("last_time") : "");
							map.put("name",
									resultMap.containsKey("name") ? resultMap.get("name") : "");

							// 确定是否进行 数据状态 告警
							if (fields.get("event_status").toString().toUpperCase().equals("OK")
									|| fields.get("event_status").toString().equals("0")) {
								map.put("aging_status", "正常");

								//判断文件大小是否正常
								String strSizeDefine = hitsSource_fields.containsKey("file_size_define") ? hitsSource_fields.get("file_size_define").toString():"";
								if (!StringUtils.isEmpty(strSizeDefine)){
									long lFileSize = fields.containsKey("file_size") ? Long.parseLong(fields.get("file_size").toString()): 0L;
									String[] strSizeDefines = strSizeDefine.split(",");
									long mix = 0L;
									long max = 0L;
									if (strSizeDefines.length == 1){
										mix = Long.parseLong(strSizeDefines[0]);
										if (mix > lFileSize){
											map.put("aging_status", "异常");
											String alertTitle = subType + "--" + fields.get("module") + "--"
													+ fields.get("data_time") + " 时次产品 ,文件大小 不在正常范围内。";
											fields.put("event_info", "文件大小异常: "+ "阈值为 大于"+mix+" byte ,实际值为 "+lFileSize+" byte");
											// 初始化告警实体类
											alertBean = alertService.getAlertBean(AlertType.FILEEX.getValue(), alertTitle, type,map);
										}

									}else if(strSizeDefines.length == 2){
										mix = Long.parseLong(strSizeDefines[0]);
										max = Long.parseLong(strSizeDefines[1]);
										if (mix > lFileSize || lFileSize > max){
											map.put("aging_status", "异常");
											String alertTitle = subType + "--" + fields.get("module") + "--"
													+ fields.get("data_time") + " 时次产品 ,文件大小 不在正常范围内。";
											fields.put("event_info", "文件大小异常: "+ "阈值范围为 "+mix+"--"+max+" byte ,实际值为 "+lFileSize+" byte");
											// 初始化告警实体类
											alertBean = alertService.getAlertBean(AlertType.FILEEX.getValue(), alertTitle, type,map);
										}
									}

									/*if (alertBean != null) {
										alertService.alert(es,index, alertType, alertBean); // 生成告警
										alertBean = null;
									}*/
								}
								//用文件本身的到达时间和最晚到达时间做比较，判断是否超时到达
								long occurTime = Long.valueOf(map.get("occur_time").toString());
								Date lastDate = Pub.transform_StringToDate(resultMap.get("last_time").toString(),
										"yyyy-MM-dd HH:mm:ss.SSSZ");
								// 确定是否 时效告警 ,修改时效状态
								if (occurTime - lastDate.getTime() >= 1000 && "正常".equals(map.get("aging_status"))) {
									Date shuldDate = Pub.transform_StringToDate(resultMap.get("should_time").toString(),
											"yyyy-MM-dd HH:mm:ss.SSSZ");
									map.put("aging_status", "迟到");
									String temp = Pub.transform_time((int) (occurTime - shuldDate.getTime()));
									String alertTitle = subType + "--" + fields.get("module") + "--"
											+ fields.get("data_time") + " 时次产品,延迟" + temp + "到达";

									fields.put("event_info", "延迟" + temp + "到达");
									// 初始化告警实体类
									alertBean = alertService.getAlertBean(AlertType.DELAY.getValue(), alertTitle, type, map);

//									if (alertBean != null) {
//										alertService.alert(es,index, alertType, alertBean); // 生成告警
//										alertBean = null;
//									}

								}
							} else {
								// 判断如果原数据是正确的， 新数据是错误的， 舍弃新数据
								if (hitsSource_fields.containsKey("event_status")
										&& (hitsSource_fields.get("event_status").toString().toUpperCase().equals("OK")
												|| hitsSource_fields.get("event_status").toString().equals("0"))) {
									log.warn("--舍弃掉 预修改错误的数据：" + json);
									continue;
								}
								map.put("aging_status", "异常");
								String alertTitle = subType + "--" + fields.get("module") + "--"
										+ fields.get("data_time") + " 时次产品 ，发生错误："
										+ fields.get("event_info").toString();
								fields.put("event_info", "数据异常");
								// 初始化告警实体类
								alertBean = alertService.getAlertBean(AlertType.ABNORMAL.getValue(), alertTitle, type,map);
							}

						}
						/*-------5.29 新代码*/
						if (hitsSource_fields.containsKey("file_name") && !StringUtils.isEmpty(hitsSource_fields.get("file_name"))){
							fields.put("file_name", hitsSource_fields.get("file_name"));
						}

						if (alertBean != null) {
							/*   需要修改该方法  2018.3.20没有修改   */
							alertService.alert(es,index, alertType, alertBean,rulesArray); // 生成告警
							alertBean = null;
						}
						else{
							if(rulesArray.size() > 0 && rulesArray.getString(0) != null){
								//告警状态正常置零告警次数
								if(rulesArray.getInteger(3) != 0){
									dataInfoRepository.resetAlertCnt(rulesArray.getLongValue(0));
								}
								//无告警时提前到达是否提示
								if(rulesArray.getInteger(4) == 1){
									String alertTitle = subType + "--" + fields.get("module") + "--"
											+ fields.get("data_time") + " 时次产品到达";
									alertBean = alertService.getAlertBean(AlertType.NOTE.getValue(), alertTitle, type,map);
									alertService.alert(es,index, alertType, alertBean,rulesArray);
								}
							}
						}
						// 数据入库
						es.bulkProcessor.add(new IndexRequest(index, type, str_id).source(map));
//						DIMap = null;
					} else {
						if(fields.containsKey("event_status")){
							// 判断数据状态
							if (fields.get("event_status").toString().toUpperCase().equals("OK")
									|| fields.get("event_status").toString().equals("0")) {
								map.put("aging_status", "正常");
								// fields.put("event_info","正常");
							} else {
								map.put("aging_status", "异常");
							}
						}

						log.info("这是一条未查询到的数据,类型为：{}, 时次为：{}", subType, fields.get("data_time"));
						es.bulkProcessor.add(new IndexRequest(index, type,str_id).source(map));
					}

				} catch (Exception e) {
					e.printStackTrace();
					log.error("错误数据："+json);
					error_num++;
				}
			}
			System.out.println("---------------------------------------------------------------------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return listSize - error_num;
		}

	}

	/**
	 * 查询单条数据
	 *
	 * @param indexs
	 * @param type
	 * @param id
	 * @return
	 */
	public Map<String, Object> getDocumentById(String[] indexs, String type, String id) {
		Map<String, Object> resultMap = new HashMap<>();
		try {

			String[] indices = esClientAdminService.indexExists(es,indexs);
			if (indices == null || indices.length < 1) {
				return resultMap;
			}
			for (String s : indices){
//			    System.out.println(s+"--"+type+"---"+id);
				GetResponse response = es.client.prepareGet(s, type, id).get();
				if (response != null && response.getSource() != null){
					resultMap = response.getSource();
					resultMap.put("_id", response.getId());
					resultMap.put("_type", response.getType());
					resultMap.put("_index", response.getIndex());
				}
			}

		} catch (Exception e) {
		    e.printStackTrace();
			log.error(e.getMessage());
			resultMap = new HashMap<>();
		} finally {
			return resultMap;
		}

	}

	/**
	 * 查询单条数据ID
	 *
	 * @param indexs
	 * @param type
	 * @param subType
	 * @param fields
	 * @return
	 */
	public Map<String, Object> getDocumentId(String[] indexs, String type, String subType, String name,
			Map<String, Object> fields) {
		Map<String, Object> resultMap = new HashMap<>();
		try {

			String[] indices = esClientAdminService.indexExists(es,indexs);
			if (indices == null || indices.length < 1) {
				return resultMap;
			}
			// 创建查询类
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
			queryBuilder.must(QueryBuilders.termQuery("type", subType));

			queryBuilder.must(QueryBuilders.termQuery("fields.data_time", fields.get("data_time").toString()));
			queryBuilder.must(QueryBuilders.termQuery("fields.module", fields.get("module").toString()));
			queryBuilder.must(QueryBuilders.termQuery("fields.ip_addr", fields.get("ip_addr").toString()));

//            log.info(queryBuilder.toString());
			// 返回查询结果
			SearchResponse response = es.client.prepareSearch(indices).setTypes(type)
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(queryBuilder).setExplain(true).get();

			SearchHit[] searchHits = response.getHits().getHits();

			if (response.getHits().getTotalHits() != 1) {
                log.info("searchHits.dataLength :" + response.getHits().getTotalHits());
				log.error("预生成数据有误，请查询ES，查询条件为：indexs:{} , type:{} , module:{}, name:{}, fields:{}", indexs, type,
						subType, name, fields);
			}
			for (SearchHit hits : searchHits) {
				resultMap = hits.getSource();
				resultMap.put("_id", hits.getId());
				resultMap.put("_type", hits.getType());
				resultMap.put("_index", hits.getIndex());
//				log.info("查询后的数据："+JSON.toJSONString(resultMap));
				break;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			// resultMap = new HashMap<>();
		} finally {
			return resultMap;
		}

	}

	/**
	 * Laps质检入库--查询单条ID
	 *
	 * @param index
	 * @param type

	 */
	public Map<String, Object> getDocumentId1(String index, String type,  Map<String, Object> fields) {
		String id = "";

		Map<String, Object> resultMap = new HashMap<>();
		try {
			//创建查询类
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
			//queryBuilder.must(QueryBuilders.termQuery("type", subType));
			for ( Map.Entry<String, Object> entry : fields.entrySet()) {
				System.out.println(entry.getKey().trim()+entry.getValue().toString().trim());
				queryBuilder.must(QueryBuilders.termQuery(entry.getKey().trim(),entry.getValue().toString().trim()));

			}
			//queryBuilder.must(QueryBuilders.termQuery("fields.event_status","OK"));
			//queryBuilder.must(QueryBuilders.termQuery("fields.data_time", fields.get("data_time").toString()));
			// queryBuilder.must(QueryBuilders.termQuery("fields.module", fields.get("module").toString()));
			// queryBuilder.must(QueryBuilders.termQuery("fields.ip_addr", fields.get("ip_addr").toString()));

			//返回查询结果
			SearchResponse response = es.client.prepareSearch(index)
					.setTypes(type)
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setQuery(queryBuilder)
					.setExplain(true).get();

			SearchHit[] searchHits = response.getHits().getHits();
			log.info("searchHits.dataLength :" + response.getHits().getTotalHits());
			System.out.println("searchHits.dataLength :" + response.getHits().getTotalHits());
			if (response.getHits().getTotalHits() != 1) {
				log.error("请查询ES，查询条件为：indexs:{} , type:{} , fields:{},id不唯一，多条入库", index, type, fields);
			} else {
				for (SearchHit hits : searchHits) {
					resultMap = hits.getSource();
					resultMap.put("_id", hits.getId());
					// resultMap.put("_type", hits.getType());
					//resultMap.put("_index", hits.getIndex());
					// System.out.println(resultMap.toString());
					// System.out.println(resultMap.get("fields"));
					//id = hits.getId();
					break;
				}
			}

            /*for (SearchHit hits:searchHits) {
                resultMap = hits.getSource();
                resultMap.put("_id",hits.getId());
                resultMap.put("_type",hits.getType());
                resultMap.put("_index",hits.getIndex());
                break;
            }*/
		} catch (Exception e) {
			log.error(e.getMessage());
//            resultMap = new HashMap<>();
		} finally {
			return resultMap;
		}
	}//结束查询


}
