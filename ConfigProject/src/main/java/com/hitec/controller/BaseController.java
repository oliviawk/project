package com.hitec.controller;

import com.alibaba.fastjson.JSONObject;
import com.hitec.util.Tools;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;

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
	public final String KEY_RESULT = "result";
	public final String KEY_RESULTDATA = "resultData";
	public final String KEY_MESSAGE = "message";
	public final String KEY_SPEND = "spend";

	public final String VAL_SUCCESS = "success";
	public final String VAL_FAIL = "fail";

	public JSONObject getAllParam(HttpServletRequest request) {
		try {
			if (Tools.isNull(request)) {
				return null;
			}

			if (request.getParameterMap().keySet().size() < 1) {
				return null;
			}

			JSONObject paramObj = new JSONObject();
			for (String key : request.getParameterMap().keySet()) {
				paramObj.put(key, request.getParameter(key));
			}
			return paramObj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
