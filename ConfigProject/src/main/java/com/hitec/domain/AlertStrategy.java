package com.hitec.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class AlertStrategy implements Serializable {
    private static final long serialVersionUID = -400599461521898L;
    @Id
    @GeneratedValue
    long id;
    String strategy_name;
    String send_users;
    String wechart_content;
    int wechart_send_enable;
    String sms_content;
    int sms_send_enable;
    long di_id;

    public AlertStrategy() {
        super();
    }

    public AlertStrategy(String strategy_name, String send_users, String wechart_content, int wechart_send_enable, String sms_content, int sms_send_enable, long di_id) {
        this.strategy_name = strategy_name;
        this.send_users = send_users;
        this.wechart_content = wechart_content;
        this.wechart_send_enable = wechart_send_enable;
        this.sms_content = sms_content;
        this.sms_send_enable = sms_send_enable;
        this.di_id = di_id;
    }

    public long getDi_id() {
        return di_id;
    }

    public void setDi_id(long di_id) {
        this.di_id = di_id;
    }

    public String getStrategy_name() {
        return strategy_name;
    }

    public void setStrategy_name(String strategy_name) {
        this.strategy_name = strategy_name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSend_users() {
        return send_users;
    }

    public void setSend_users(String send_users) {
        this.send_users = send_users;
    }

    public String getWechart_content() {
        return wechart_content;
    }

    public void setWechart_content(String wechart_content) {
        this.wechart_content = wechart_content;
    }

    public int getWechart_send_enable() {
        return wechart_send_enable;
    }

    public void setWechart_send_enable(int wechart_send_enable) {
        this.wechart_send_enable = wechart_send_enable;
    }

    public String getSms_content() {
        return sms_content;
    }

    public void setSms_content(String sms_content) {
        this.sms_content = sms_content;
    }

    public int getSms_send_enable() {
        return sms_send_enable;
    }

    public void setSms_send_enable(int sms_send_enable) {
        this.sms_send_enable = sms_send_enable;
    }


}
