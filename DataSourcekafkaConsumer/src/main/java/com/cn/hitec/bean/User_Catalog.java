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
    private  Long User_catalog_id;
    private  String User_name;
    private  String User_catalog_content;
    public User_Catalog() {
    }

    public Long getUser_catalog_id() {
        return User_catalog_id;
    }

    public void setUser_catalog_id(Long user_catalog_id) {
        User_catalog_id = user_catalog_id;
    }

    public String getUser_name() {
        return User_name;
    }

    public void setUser_name(String user_name) {
        User_name = user_name;
    }

    public String getUser_catalog_content() {
        return User_catalog_content;
    }

    public void setUser_catalog_content(String user_catalog_content) {
        User_catalog_content = user_catalog_content;
    }
}
