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
	public final static String KEY_RESULT = "result";
	public final static String KEY_RESULTDATA = "resultData";
	public final static String KEY_MESSAGE = "message";
	public final static String KEY_SPEND = "spend";
	public final static String KEY_TOTAL= "total";
	public final static String KEY_ROWS= "rows";

	public final static String VAL_SUCCESS = "success";
	public final static String VAL_ERROR = "error";
	public final static String VAL_FAIL = "fail";


	public Map<String,Object> outMap = new HashMap<String,Object>();


}
