package com.cn.hitec.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by libin on 2018/6/22.
 */
@Entity
public class User_Catalog implements Serializable {
    @Id
    @GeneratedValue
    private  Long user_catalog_id;
    private  String user_name;
    private  String user_catalog_content;
    private String user_catalog_ip;

    public User_Catalog() {
    }

    public User_Catalog(String user_name, String user_catalog_content, String user_catalog_ip) {
        this.user_name = user_name;
        this.user_catalog_content = user_catalog_content;
        this.user_catalog_ip = user_catalog_ip;
    }

    public Long getUser_catalog_id() {
        return user_catalog_id;
    }

    public void setUser_catalog_id(Long user_catalog_id) {
        this.user_catalog_id = user_catalog_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_catalog_content() {
        return user_catalog_content;
    }

    public void setUser_catalog_content(String user_catalog_content) {
        this.user_catalog_content = user_catalog_content;
    }

    public String getUser_catalog_ip() {
        return user_catalog_ip;
    }

    public void setUser_catalog_ip(String user_catalog_ip) {
        this.user_catalog_ip = user_catalog_ip;
    }
}
