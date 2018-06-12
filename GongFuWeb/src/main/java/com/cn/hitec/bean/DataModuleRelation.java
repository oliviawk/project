package com.cn.hitec.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class DataModuleRelation implements Serializable {
    private static final long serialVersionUID = -4005994623153410L;

    @Id
    @GeneratedValue
    private long pk_id;

    private int source_id;
    private int module_id;
    private int product_id;
    private String descs;

    public DataModuleRelation() {
    }

    public DataModuleRelation(int source_id, int module_id, int product_id, String descs) {
        this.source_id = source_id;
        this.module_id = module_id;
        this.product_id = product_id;
        this.descs = descs;
    }

    public long getPk_id() {
        return pk_id;
    }

    public void setPk_id(long pk_id) {
        this.pk_id = pk_id;
    }

    public int getSource_id() {
        return source_id;
    }

    public void setSource_id(int source_id) {
        this.source_id = source_id;
    }

    public int getModule_id() {
        return module_id;
    }

    public void setModule_id(int module_id) {
        this.module_id = module_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getDescs() {
        return descs;
    }

    public void setDescs(String descs) {
        this.descs = descs;
    }
}
