package com.cn.hitec.controller;

import com.cn.hitec.service.DataSourceSendMakeProjectTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.hitec.service.MsgConsumer;

@Controller
@RequestMapping(value="/dataSourceKafka")
public class MsgConsumerController {
	
	@Autowired()
	DataSourceSendMakeProjectTable dataSourceSendMakeProjectTable;

	@RequestMapping("/updataInsertBaseFilter")
	@ResponseBody
	public String updataInsertBaseFilter(){
		System.out.println("被调用了.....,");
		dataSourceSendMakeProjectTable.updateInsertBaseFilter();
		return "SUCCESS";
	}
	
}
