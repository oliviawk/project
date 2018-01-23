package com.cn.hitec.domain;

import java.util.List;

public class EsBean {
	private String index;
	private String type;
	private String id;
	private List<String> data;
	
	public EsBean(String index, String type, String id, List<String> data) {
		super();
		this.index = index;
		this.type = type;
		this.id = id;
		this.data = data;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "EsBean [index=" + index + ", type=" + type + ", id=" + id + ", data=" + data + "]";
	}

}
