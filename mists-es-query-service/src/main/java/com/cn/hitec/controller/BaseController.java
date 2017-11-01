package com.cn.hitec.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @description: TODO 基础的Controller
 * @author james
 * @since 2017年7月20日 上午11:44:46 
 * @version 
 *
 */
public class BaseController {
	public Map<String,Object> outMap = new HashMap<String,Object>();
	
	public final String KEY_RESULT = "result";
	public final String KEY_RESULTDATA = "resultData";
	public final String KEY_MESSAGE = "message";
	public final String KEY_SPEND = "spend";
	
	public final String VAL_SUCCESS = "success";
	public final String VAL_ERROR = "error";
	public final String VAL_FAIL = "fail";
	
	
}
