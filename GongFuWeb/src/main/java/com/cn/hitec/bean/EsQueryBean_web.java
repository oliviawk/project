package com.cn.hitec.bean;

import java.util.Map;

/**
 * ES 查询 实体类
 */
public class EsQueryBean_web {
    private String[] indices;
    private String[] types;
    private Map<String,Object> parameters;

    private String module;
    private String size = "10";
    private String subType;
    private String strIp ;
    private String hour_before = "2";
    private String minute_before = "120";

    private String findType;

    private String rand;    // just for debug


    public String getHour_before() {
        return hour_before;
    }

    public void setHour_before(String hour_before) {
        this.hour_before = hour_before;
    }

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

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getStrIp() {
        return strIp;
    }

    public void setStrIp(String strIp) {
        this.strIp = strIp;
    }

    public String getMinute_before() {
        return minute_before;
    }

    public void setMinute_before(String minute_before) {
        this.minute_before = minute_before;
    }

    public String getFindType() {
        return findType;
    }

    public void setFindType(String findType) {
        this.findType = findType;
    }

    public String getRand() {
        return rand;
    }

    public void setRand(String rand) {
        this.rand = rand;
    }
}
