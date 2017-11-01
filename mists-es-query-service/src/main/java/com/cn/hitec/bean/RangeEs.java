package com.cn.hitec.bean;

/**
 * @Description: 比较大小类
 * @author: fukl
 * @data: 2017年09月28日 14:18
 */
public enum RangeEs {

    GT("gt"),GTE("gte"),LT("lt"),LTE("lte");

    public String value;

    RangeEs(String value) {
        this.value = value;
    }

    public  String getValue(){
        return this.value;
    }
}
