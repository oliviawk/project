package com.hitec.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @Description: 发送模板类
 * @author: fukl
 * @data: 2018年02月07日 14:34
 */
@Entity
public class SendTemplate implements Serializable {

    private static final long serialVersionUID = -40059945615208948L;

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String type;
    private String wechartContentTemplate;
    private String wechartSendEnable;
    private String smsContentTemplate;
    private String smsSendEnable;

    public SendTemplate() {
        super();
    }

    public SendTemplate(long id,String name, String type, String wechartContentTemplate, String wechartSendEnable, String smsContentTemplate, String smsSendEnable) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.wechartContentTemplate = wechartContentTemplate;
        this.wechartSendEnable = wechartSendEnable;
        this.smsContentTemplate = smsContentTemplate;
        this.smsSendEnable = smsSendEnable;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public String getWechartContentTemplate() {
        return wechartContentTemplate;
    }

    public void setWechartContentTemplate(String wechartContentTemplate) {
        this.wechartContentTemplate = wechartContentTemplate;
    }

    public String getWechartSendEnable() {
        return wechartSendEnable;
    }

    public void setWechartSendEnable(String wechartSendEnable) {
        this.wechartSendEnable = wechartSendEnable;
    }

    public String getSmsContentTemplate() {
        return smsContentTemplate;
    }

    public void setSmsContentTemplate(String smsContentTemplate) {
        this.smsContentTemplate = smsContentTemplate;
    }

    public String getSmsSendEnable() {
        return smsSendEnable;
    }

    public void setSmsSendEnable(String smsSendEnable) {
        this.smsSendEnable = smsSendEnable;
    }
}
