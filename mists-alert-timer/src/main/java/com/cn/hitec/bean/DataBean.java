package com.cn.hitec.bean;

import java.util.Map;

public class DataBean {
	private String type = ""; // 所属业务(2级)
	private String name = "";
	private String should_time; // 应到时间
	private String last_time; // 最晚时间
	private String aging_status = "未处理"; // 时效状态
	private Map<String, Object> fields;
	private String serviceType = ""; // 所属业务(1级)
	private String startMoniter = "yes";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Object> getFields() {
		return fields;
	}

	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
	}

	public String getShould_time() {
		return should_time;
	}

	public void setShould_time(String should_time) {
		this.should_time = should_time;
	}

	public String getLast_time() {
		return last_time;
	}

	public void setLast_time(String last_time) {
		this.last_time = last_time;
	}

	public String getAging_status() {
		return aging_status;
	}

	public void setAging_status(String aging_status) {
		this.aging_status = aging_status;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getStartMoniter() {
		return startMoniter;
	}

	public void setStartMoniter(String startMoniter) {
		this.startMoniter = startMoniter;
	}
}