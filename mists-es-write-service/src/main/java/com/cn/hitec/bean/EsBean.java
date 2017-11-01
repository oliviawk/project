package com.cn.hitec.bean;

import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

/**
 * ES数据结构体
 */
public class EsBean {


    private String index;
    private String type;
    private String id;
    private List<String> data;
    private Map<String,Object> params;

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

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
