package com.cn.hitec.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.script.mustache.SearchTemplateRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_Exsit;
import com.cn.hitec.controller.BaseController;
import com.cn.hitec.service.BoolTermQuery_I;
import com.cn.hitec.service.ESClientAdminService;
import com.cn.hitec.service.ESConfigService;
import com.cn.hitec.service.ESService;
import com.cn.hitec.service.ESWebService;

/**
 * 
 * 
 * @description: TODO(这里用一句话描述这个类的作用)
 * @author fukl
 * @since 2017年8月3日
 * @version
 *
 */
@RestController
@RequestMapping("/query")
public class QueryApi extends BaseController {
	@Autowired
	ESClientAdminService esClientAdminService;
	@Autowired
	ESService esService;
	@Autowired
	ESConfigService esConfigService;
	@Autowired
	ESWebService esWebService;
	@Autowired
	BoolTermQuery_I boolTermQueryI;

	/**
	 * 获取集群健康状态
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getHealth", method = RequestMethod.GET)
	public Map<String, Object> getHealth() {
		long start = System.currentTimeMillis();
		Map<String, Object> map = esClientAdminService.getClusterHealth();
		long spend = System.currentTimeMillis() - start;
		outMap.put(KEY_RESULT, VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA, map);
		outMap.put(KEY_MESSAGE, "数据获取成功！");
		outMap.put(KEY_SPEND, spend + "mm");
		return outMap;
	}

	/**
	 * 检测index 是否存在
	 * 
	 * @return
	 */
	@RequestMapping(value = "/indexIsExist", method = RequestMethod.POST)
	public Map<String, Object> indexIsExist(@RequestBody EsQueryBean_Exsit esQueryBean) {
		try {
			if (StringUtils.isEmpty(esQueryBean) || StringUtils.isEmpty(esQueryBean.getIndex())) {
				outMap.put(KEY_RESULT, VAL_ERROR);
				outMap.put(KEY_RESULTDATA, null);
				outMap.put(KEY_MESSAGE, "参数不能为空！");
			} else {
				// 判断index 是否存在
				boolean flag = esClientAdminService.indexIsExist(esQueryBean.getIndex());

				if (!StringUtils.isEmpty(esQueryBean.getSubType()) && flag) {
					Map<String, Object> params = new HashMap<>();
					Map<String, Object> mustMap = new HashMap<>();
					mustMap.put("type", esQueryBean.getSubType());
					params.put("must", mustMap);
					List list = boolTermQueryI.query_new(new String[] { esQueryBean.getIndex() },
							new String[] { esQueryBean.getType() }, params);
					// 判断数据是否存在
					if (list.size() > 0) {
						outMap.put(KEY_RESULTDATA, true);
					} else {
						outMap.put(KEY_RESULTDATA, false);
					}
				} else {
					outMap.put(KEY_RESULTDATA, flag);
				}
				outMap.put(KEY_RESULT, VAL_SUCCESS);
				outMap.put(KEY_MESSAGE, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, null);
			outMap.put(KEY_MESSAGE, e.getMessage());
		} finally {
			return outMap;
		}
	}

	/**
	 * 查询数据 term 查询
	 * 
	 * @param esQueryBean
	 * @return
	 */
	@RequestMapping(value = "/getdata_new", method = RequestMethod.POST)
	public Map<String, Object> getData_new(@RequestBody EsQueryBean esQueryBean) {
		long start = System.currentTimeMillis();
		List<Map> list = null;
		try {
			if (esQueryBean == null) {
				list = new ArrayList<>();
				outMap.put(KEY_RESULT, VAL_ERROR);
				outMap.put(KEY_RESULTDATA, list);
				outMap.put(KEY_MESSAGE, "参数错误！");
			} else {
				list = boolTermQueryI.query_new(esQueryBean.getIndices(), esQueryBean.getTypes(),
						esQueryBean.getParameters());
				outMap.put(KEY_RESULT, VAL_SUCCESS);
				outMap.put(KEY_RESULTDATA, list);
				outMap.put(KEY_MESSAGE, "获取数据成功");
				// System.out.println("接口返回耗时："+(System.currentTimeMillis() -
				// start));
				// System.out.println(JSON.toJSONString(esQueryBean));
				// System.out.println(JSON.toJSONString(list));
				// System.out.println("------------");
			}
		} catch (Exception e) {
			list = new ArrayList<>();
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, list);
			outMap.put(KEY_MESSAGE, e.getMessage());
			e.printStackTrace();

		} finally {
			long spend = System.currentTimeMillis() - start;
			outMap.put(KEY_SPEND, spend + "ms");
			return outMap;
		}
	}

	/**
	 * 查询数据 term 查询
	 * 
	 * @param esQueryBean
	 * @return
	 */
	@RequestMapping(value = "/getdata", method = RequestMethod.POST)
	public Map<String, Object> getData(@RequestBody EsQueryBean esQueryBean) {
		long start = System.currentTimeMillis();

		List<Map> list = null;
		try {
			if (esQueryBean == null) {
				list = new ArrayList<>();
				outMap.put(KEY_RESULT, VAL_ERROR);
				outMap.put(KEY_RESULTDATA, list);
				outMap.put(KEY_MESSAGE, "参数错误！");
			} else {
				list = boolTermQueryI.query(esQueryBean.getIndices(), esQueryBean.getTypes(),
						esQueryBean.getParameters());
				outMap.put(KEY_RESULT, VAL_SUCCESS);
				outMap.put(KEY_RESULTDATA, list);
				outMap.put(KEY_MESSAGE, "获取数据成功");
				// System.out.println(JSON.toJSONString(esQueryBean));
				// System.out.println(JSON.toJSONString(list));
				// System.out.println("------------");
			}
		} catch (Exception e) {
			list = new ArrayList<>();
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, list);
			outMap.put(KEY_MESSAGE, e.getMessage());
		} finally {
			long spend = System.currentTimeMillis() - start;
			outMap.put(KEY_SPEND, spend + "ms");
			return outMap;
		}
	}

	/**
	 * 查询数据 结果带ID term 查询
	 * 
	 * @param esQueryBean
	 * @return
	 */
	@RequestMapping(value = "/getdata_resultId", method = RequestMethod.POST)
	public Map<String, Object> getData_resultId(@RequestBody EsQueryBean esQueryBean) {
		long start = System.currentTimeMillis();
		Map resultMap = null;
		try {
			if (esQueryBean == null) {
				resultMap = new HashMap();
				outMap.put(KEY_RESULT, VAL_ERROR);
				outMap.put(KEY_RESULTDATA, resultMap);
				outMap.put(KEY_MESSAGE, "参数错误！");
			} else {
				resultMap = boolTermQueryI.query_resultId(esQueryBean.getIndices(), esQueryBean.getTypes(),
						esQueryBean.getParameters());
				outMap.put(KEY_RESULT, VAL_SUCCESS);
				outMap.put(KEY_RESULTDATA, resultMap);
				outMap.put(KEY_MESSAGE, "获取数据成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = new HashMap();
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, resultMap);
			outMap.put(KEY_MESSAGE, e.getMessage());
		} finally {
			long spend = System.currentTimeMillis() - start;
			outMap.put(KEY_SPEND, spend + "ms");
			return outMap;
		}
	}

	@RequestMapping(value = "/temp", method = RequestMethod.POST)
	public Map<String, Object> temp(@RequestBody SearchTemplateRequestBuilder builder) {
		long start = System.currentTimeMillis();
		List<Map> list = null;
		try {
			if (builder == null) {
				list = new ArrayList<>();
				outMap.put(KEY_RESULT, VAL_ERROR);
				outMap.put(KEY_RESULTDATA, list);
				outMap.put(KEY_MESSAGE, "参数错误！");
			} else {
				list = esService.temp(builder);
				outMap.put(KEY_RESULT, VAL_SUCCESS);
				outMap.put(KEY_RESULTDATA, list);
				outMap.put(KEY_MESSAGE, "获取数据成功");
			}
		} catch (Exception e) {
			list = new ArrayList<>();
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, list);
			outMap.put(KEY_MESSAGE, e.getMessage());
		} finally {
			long spend = System.currentTimeMillis() - start;
			outMap.put(KEY_SPEND, spend + "ms");
			return outMap;
		}
	}

	/**
	 * 根据 module 获取最新数据
	 * 
	 * @param esQueryBean
	 * @return
	 */
	@RequestMapping(value = "/getdatatemp", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> getData_temp(@RequestBody EsQueryBean esQueryBean) {
		long start = System.currentTimeMillis();
		List<Map> list = new ArrayList<>();
		try {
			if (esQueryBean == null || esQueryBean.getParameters() == null) {
				outMap.put(KEY_RESULT, VAL_ERROR);
				outMap.put(KEY_RESULTDATA, list);
				outMap.put(KEY_MESSAGE, "参数错误！");
			} else {
				list = esService.find_2(esQueryBean.getIndices(), esQueryBean.getTypes(), esQueryBean.getTemplataName(),
						esQueryBean.getParameters());
				outMap.put(KEY_RESULT, VAL_SUCCESS);
				outMap.put(KEY_RESULTDATA, list);
				outMap.put(KEY_MESSAGE, "获取数据成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<>();
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, list);
			outMap.put(KEY_MESSAGE, e.getMessage());
		} finally {
			long spend = System.currentTimeMillis() - start;
			outMap.put(KEY_SPEND, spend + "ms");
			return outMap;
		}
	}

	/**
	 * 根据 获取告警信息 获取最新数据
	 * 
	 * @param esQueryBean
	 * @return
	 */
	@RequestMapping(value = "/getalert", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> getAlertData(@RequestBody EsQueryBean esQueryBean) {
		long start = System.currentTimeMillis();

		List<Map> list = new ArrayList<>();
		try {
			if (esQueryBean == null) {
				outMap.put(KEY_RESULT, VAL_ERROR);
				outMap.put(KEY_RESULTDATA, list);
				outMap.put(KEY_MESSAGE, "参数错误！");
			} else {
				list = esConfigService.getConfigAlert(esQueryBean.getIndices(), esQueryBean.getParameters());
				outMap.put(KEY_RESULT, VAL_SUCCESS);
				outMap.put(KEY_RESULTDATA, list);
				outMap.put(KEY_MESSAGE, "获取数据成功");
				// System.out.println(JSON.toJSONString(esQueryBean));
				// System.out.println(JSON.toJSONString(list));
				// System.out.println("----");
			}
		} catch (Exception e) {
			list = new ArrayList<>();
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, list);
			outMap.put(KEY_MESSAGE, e.getMessage());
		} finally {
			long spend = System.currentTimeMillis() - start;
			outMap.put(KEY_SPEND, spend + "ms");
			return outMap;
		}
	}




	@RequestMapping(value = "/getDocumentById", method = RequestMethod.POST, consumes = "application/json")
	public String getDocumentById(@RequestBody String json) {
		return esClientAdminService.getDocumentById(json);
	}


	@RequestMapping(value = "/lctAggQuery", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> lctAggQuery(@RequestBody EsQueryBean esQueryBean) {
		long start = System.currentTimeMillis();
		Map<String, Object> resultMap = null;
		try {
			if (esQueryBean == null) {
				outMap.put(KEY_RESULT, VAL_ERROR);
				outMap.put(KEY_RESULTDATA, null);
				outMap.put(KEY_MESSAGE, "参数错误！");
			}else{
				Map params = esQueryBean.getParameters();
				String[] subTypes = params.get("subTypes").toString().split(",");
				resultMap = esWebService.lct_AggTerms(esQueryBean.getIndices(),esQueryBean.getTypes(),subTypes);
				outMap.put(KEY_RESULT, VAL_SUCCESS);
				outMap.put(KEY_RESULTDATA, resultMap);
				outMap.put(KEY_MESSAGE, "获取数据成功");
			}

		} catch (Exception e) {
			resultMap = new HashMap<>();
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, null);
			outMap.put(KEY_MESSAGE, e.getMessage());
		} finally {
			long spend = System.currentTimeMillis() - start;
			outMap.put(KEY_SPEND, spend + "ms");
			return outMap;
		}
	}

}
