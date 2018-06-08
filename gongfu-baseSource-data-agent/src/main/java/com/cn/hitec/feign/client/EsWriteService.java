package com.cn.hitec.feign.client;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cn.hitec.bean.EsWriteBean;

/**
 * 
 * 
 * @description: TODO(这里用一句话描述这个类的作用)
 * @author james
 * @since 2017年7月20日 下午2:41:56
 * @version
 *
 */
@FeignClient("mists-es-write-service")
public interface EsWriteService {

	/**
	 * 检测es健康状态， 测试
	 * 
	 * @return
	 */
	@RequestMapping(value = "/write/getHealth", method = RequestMethod.GET, consumes = "application/json")
	String getESHealth();

	/**
	 * 添加数据
	 * 
	 * @return
	 */
	@RequestMapping(value = "/write/add", method = RequestMethod.POST, consumes = "application/json")
	Map<String, Object> add(EsWriteBean esBean);

	@RequestMapping(value = "/write/insert", method = RequestMethod.POST, consumes = "application/json")
	Map<String, Object> insert(EsWriteBean esBean);

	@RequestMapping(value = "/write/insert1", method = RequestMethod.POST, consumes = "application/json")
	Map<String, Object> insert1(EsWriteBean esBean);

	/**
	 * 修改数据
	 * 
	 * @param esBean
	 * @return
	 */
	@RequestMapping(value = "/write/update", method = RequestMethod.POST, consumes = "application/json")
	Map<String, Object> update(@RequestBody EsWriteBean esBean);

	@RequestMapping(value = "/write/update2", method = RequestMethod.POST, consumes = "application/json")
	Map<String, Object> update_field(@RequestBody EsWriteBean esBean);

}
