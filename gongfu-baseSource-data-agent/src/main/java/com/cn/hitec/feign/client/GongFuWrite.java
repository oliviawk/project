package com.cn.hitec.feign.client;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cn.hitec.domain.EsBean;


@FeignClient("mists-es-write-service")
public interface GongFuWrite {

	@RequestMapping(value="/write/add",method=RequestMethod.POST,consumes="application/json")
	public Map<String,Object> add(@RequestBody EsBean esBean);
}
