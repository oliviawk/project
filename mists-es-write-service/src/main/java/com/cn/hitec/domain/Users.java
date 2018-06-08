package com.cn.hitec.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Users implements Serializable  {
    private static final long serialVersionUID = -4005994623153398L;

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String parent_id;
    private String email ;
    private String wechart ;
    private String phone ;
    private String descs ;
    private int is_user ;

    public Users() {
    }

    public Users(String name, String parent_id, String email, String wechart, String phone, String descs, int is_user) {
        this.name = name;
        this.parent_id = parent_id;
        this.email = email;
        this.wechart = wechart;
        this.phone = phone;
        this.descs = descs;
        this.is_user = is_user;
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

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWechart() {
        return wechart;
    }

    public void setWechart(String wechart) {
        this.wechart = wechart;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescs() {
        return descs;
    }

    public void setDescs(String descs) {
        this.descs = descs;
    }

    public int getIs_user() {
        return is_user;
    }

    public void setIs_user(int is_user) {
        this.is_user = is_user;
    }
}
