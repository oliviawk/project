package com.cn.hitec.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.hitec.repository.ESRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName:
 * @Description: 查询配置文件
 * @author: fukl
 * @data: 2017年08月3日 下午1:14
 */
@Slf4j
@Service
public class ESConfigService {
	@Autowired
	private ESRepository es;

	/**
	 * 查询特定type的配置表信息
	 * 
	 * @param indices
	 * @param types
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<Map> getConfigAlert(String[] indices, String[] types, Map<String, Object> params) throws Exception {
		List<Map> resultList = new ArrayList<>();
		int sizeInt = 100;
		long timeValue = 15000;
		try {
			if (params != null) {
				if (params.get("size") != null && (Integer) params.get("size") > 0) {
					sizeInt = (Integer) params.get("size");
				}
				if (params.get("timeValue") != null && (long) params.get("timeValue") > 0) {
					timeValue = (Integer) params.get("timeValue");
				}
			}
			SearchResponse scrollResp = es.client.prepareSearch(indices).setTypes(types)
					.setScroll(new TimeValue(timeValue)).setSize(sizeInt).get();
			// max of 100 hits will be returned for each scroll Scroll until no
			// hits are returned
			do {
				for (SearchHit hit : scrollResp.getHits().getHits()) {
					// Handle the hit...
					try {
						hit.getSource().put("id", hit.getId());
						resultList.add(hit.getSource());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				scrollResp = es.client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(timeValue))
						.execute().actionGet();
			} while (scrollResp.getHits().getHits().length != 0);
			// Zero hits mark the end of the scroll and the while loop.

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			resultList = null;
		} finally {
			return resultList;
		}
	}

	/**
	 * 查询所有配置信息
	 * 
	 * @param indices
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<Map> getConfigAlert(String[] indices, Map<String, Object> params) throws Exception {
		List<Map> resultList = new ArrayList<>();
		int sizeInt = 100;
		long timeValue = 15000;
		try {
			if (params != null) {
				if (params.get("size") != null && (Integer) params.get("size") > 0) {
					sizeInt = (Integer) params.get("size");
				}
				if (params.get("timeValue") != null && (long) params.get("timeValue") > 0) {
					timeValue = (Integer) params.get("timeValue");
				}
			}
			SearchResponse scrollResp = es.client.prepareSearch(indices).setScroll(new TimeValue(timeValue))
					.setSize(sizeInt).get(); // max of 100 hits will be returned
												// for each scroll
			// Scroll until no hits are returned
			do {
				for (SearchHit hit : scrollResp.getHits().getHits()) {
					// Handle the hit...
					try {
						hit.getSource().put("id", hit.getId());
						// 因为入库的时候去掉了serviceType字段,所以需要额外添加
						hit.getSource().put("serviceType", hit.getType());
						resultList.add(hit.getSource());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				scrollResp = es.client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(timeValue))
						.execute().actionGet();
			} while (scrollResp.getHits().getHits().length != 0);
			// Zero hits mark the end of the scroll and the while loop.

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			resultList = null;
		} finally {
			return resultList;
		}
	}

}
