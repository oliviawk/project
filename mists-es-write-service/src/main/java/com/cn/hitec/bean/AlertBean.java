package com.cn.hitec.bean;

/**
 * 告警详情实体类
 */
public class AlertBean {

    private String type;              //告警编号
    private String alertType;         //告警类型  、  超时、迟到、异常
    private String level;             //告警等级
    private String title;             //告警标题
    private String time;
    private String ip;
    private String desc;                //events_info的内容
    private String cause;               //可能发生的事件
    private String data_name;           //资料名称
    private String data_time;           //资料时间
    private String  module;             //环节

    private String documentId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getData_name() {
        return data_name;
    }

    public void setData_name(String data_name) {
        this.data_name = data_name;
    }

    public String getData_time() {
        return data_time;
    }

    public void setData_time(String data_time) {
        this.data_time = data_time;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
