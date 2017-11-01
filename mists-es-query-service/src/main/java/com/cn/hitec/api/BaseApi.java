package com.cn.hitec.api;

import com.cn.hitec.controller.BaseController;
import com.cn.hitec.service.ESClientAdminService;
import com.cn.hitec.service.ESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
@RequestMapping("/baseapi")
public class BaseApi extends BaseController {
	@Autowired
	ESClientAdminService esClientAdminService;
	@Autowired
	ESService esService;


	@RequestMapping(value="/avg",method= RequestMethod.GET)
	public Map<String,Object> avg(@RequestBody String type){
		long start = System.currentTimeMillis();

		System.out.printf("参数：%s",type);
		outMap.put(KEY_RESULT,VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA,null);
		outMap.put(KEY_MESSAGE,"数据获取成功！");
		outMap.put(KEY_SPEND,(System.currentTimeMillis()-start)+"mm");
		return outMap;
	}

	@RequestMapping(value="/max",method= RequestMethod.GET)
	public Map<String,Object> max(){
		long start = System.currentTimeMillis();

		outMap.put(KEY_RESULT,VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA,null);
		outMap.put(KEY_MESSAGE,"数据获取成功！");
		outMap.put(KEY_SPEND,(System.currentTimeMillis()-start)+"mm");
		return outMap;
	}


	@RequestMapping(value="/test",method= RequestMethod.GET)
	public Map<String,Object> test(){
		long start = System.currentTimeMillis();

		esService.testServer();
		outMap.put(KEY_RESULT,VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA,null);
		outMap.put(KEY_MESSAGE,"数据获取成功！");
		outMap.put(KEY_SPEND,(System.currentTimeMillis()-start)+"mm");
		return outMap;
	}

}
