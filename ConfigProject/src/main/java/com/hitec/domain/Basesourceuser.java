package com.hitec.domain;/**
 * Created by libin on 2018/9/4.
 */

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @ClassName Basesourceuser
 * @Description TODO
 * @Author Li Cong
 * @Date 2018/9/4 10:53
 * @vERSION 1.0
 **/
@Entity
public class Basesourceuser implements Serializable {
    private static final long serialVersionUID = -40059946153398L;
    @Id
    @GeneratedValue
    private Long id;
    private  String user;
    private  String  sms;
    private  float enable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Basesourceuser() {
    }

    public Basesourceuser(String user, String sms, float enable) {
        this.user = user;
        this.sms = sms;
        this.enable = enable;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public float getEnable() {
        return enable;
    }

    public void setEnable(float enable) {
        this.enable = enable;
    }
}
