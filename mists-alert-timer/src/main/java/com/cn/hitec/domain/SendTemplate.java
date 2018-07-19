package com.cn.hitec.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @description: 描述信息
 * @author: fukl
 * @data: 2018年07月19日 下午6:37
 */
@Entity
public class SendTemplate implements Serializable {
    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String type;
    private String wechart_content_template;
    private int wechart_send_enable;
    private String sms_content_template;
    private int sms_send_enable;

    public SendTemplate() {
    }

    public SendTemplate(String name, String type, String wechart_content_template, int wechart_send_enable, String sms_content_template, int sms_send_enable) {
        this.name = name;
        this.type = type;
        this.wechart_content_template = wechart_content_template;
        this.wechart_send_enable = wechart_send_enable;
        this.sms_content_template = sms_content_template;
        this.sms_send_enable = sms_send_enable;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getWechart_content_template() {
        return wechart_content_template;
    }

    public void setWechart_content_template(String wechart_content_template) {
        this.wechart_content_template = wechart_content_template;
    }

    public int getWechart_send_enable() {
        return wechart_send_enable;
    }

    public void setWechart_send_enable(int wechart_send_enable) {
        this.wechart_send_enable = wechart_send_enable;
    }

    public String getSms_content_template() {
        return sms_content_template;
    }

    public void setSms_content_template(String sms_content_template) {
        this.sms_content_template = sms_content_template;
    }

    public int getSms_send_enable() {
        return sms_send_enable;
    }

    public void setSms_send_enable(int sms_send_enable) {
        this.sms_send_enable = sms_send_enable;
    }
}
