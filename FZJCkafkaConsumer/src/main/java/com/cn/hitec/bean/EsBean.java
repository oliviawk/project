package com.cn.hitec.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ES数据结构体
 */
public class EsBean {


    private String type;
    private String id;
    private List<String> data;
    
	public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
