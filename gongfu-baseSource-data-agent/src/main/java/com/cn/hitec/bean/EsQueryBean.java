package com.cn.hitec.bean;

import java.util.Map;

/**
 * ES查询结构体
 */
public class EsQueryBean {

	private String[] indices;
	private String[] types;
	private String templataName;
	private Map<String, Object> parameters;

	public String[] getIndices() {
		return indices;
	}

	public void setIndices(String[] indices) {
		this.indices = indices;
	}

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	public String getTemplataName() {
		return templataName;
	}

	public void setTemplataName(String templataName) {
		this.templataName = templataName;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
}
