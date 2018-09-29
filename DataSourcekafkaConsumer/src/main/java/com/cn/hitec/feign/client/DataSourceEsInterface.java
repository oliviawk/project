package com.cn.hitec.feign.client;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cn.hitec.bean.EsBean;

@FeignClient("mists-es-write-service")
public interface DataSourceEsInterface {

	@RequestMapping(value = "/datasource/insertDataSource_DI", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> insertDataSource_DI(@RequestBody String json);
	
	@RequestMapping(value = "/datasource/insertDataSource", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> insertDataSource(@RequestBody String json);

	@RequestMapping(value = "/datasource/insertList", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> insertList(@RequestBody String json);

	@RequestMapping(value = "/write/updateDataSource", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> updateDataSource(@RequestBody EsBean esBean);

	@RequestMapping(value = "/write/insertIrregulardata", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> insertIrregulardata(@RequestBody EsBean esBean);

	@RequestMapping(value = "/mqpfsource/insert", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> insertMQPFData(@RequestBody EsBean esBean);

}
