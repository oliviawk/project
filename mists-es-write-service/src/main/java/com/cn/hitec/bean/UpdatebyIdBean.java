package com.cn.hitec.bean;

import java.util.Map;

public class UpdatebyIdBean {
    private String index;
    private String type;
    private String id;
  //  private Map<String,Object> params;
  private String json;

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

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

//    public Map<String, Object> getParams() {
//        return params;
//    }
//
//    public void setParams(Map<String, Object> params) {
//        this.params = params;
//    }
}
