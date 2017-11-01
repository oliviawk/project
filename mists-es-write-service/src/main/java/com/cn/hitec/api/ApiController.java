package com.cn.hitec.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cn.hitec.tools.AppPathTool;



/**
 * 
 * @ClassName: ApiController 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author james
 * @date 2017年6月8日 下午1:14:41 
 */
@Controller
public class ApiController {
	private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
	
	@RequestMapping("/api")
	public String index(ModelMap map , @RequestParam(defaultValue = "index",value = "mdname") String mdname){
		String path = AppPathTool.getDataPath();
		logger.info("API<接口调用>:"+path+mdname+".md");
        map.addAttribute("markdown","/api/"+mdname+".md");
        return "api/markdown";
	}
	
}