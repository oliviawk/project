package com.cn.hitec.feign.client;


import com.cn.hitec.bean.*;
import com.sun.javafx.collections.MappingChange;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
public interface EsService {

	/**
	 * 检测es健康状态， 测试
	 * @return
	 */
	@RequestMapping(value="/write/getHealth",method=RequestMethod.GET,consumes="application/json")
	public String getESHealth();

	/**
	 * 检测es健康状态， 测试
	 * @return
	 */

	@RequestMapping(value="/write/update",method=RequestMethod.POST,consumes="application/json")
	public String add(EsBean esBean);

	@RequestMapping(value="/write/getId",method=RequestMethod.POST,consumes="application/json")
	public Map<String,Object> search(SearchBean esBean);

	@RequestMapping(value="/write/updatebyid",method=RequestMethod.POST,consumes="application/json")
	public Map<String,Object> updatebyid(UpdatebyIdBean esbean );

}
