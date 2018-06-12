package com.cn.hitec.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class ModuleInfo implements Serializable {
    private static final long serialVersionUID = -4005994623153409L;

    @Id
    @GeneratedValue
    private long pk_id;

    private String name;
    private String ip_addr;
    private String descs;

    public ModuleInfo() {
    }

    public ModuleInfo(String name, String ip_addr, String descs) {
        this.name = name;
        this.ip_addr = ip_addr;
        this.descs = descs;
    }


    public long getPk_id() {
        return pk_id;
    }

    public void setPk_id(long pk_id) {
        this.pk_id = pk_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp_addr() {
        return ip_addr;
    }

    public void setIp_addr(String ip_addr) {
        this.ip_addr = ip_addr;
    }

    public String getDescs() {
        return descs;
    }

    public void setDescs(String descs) {
        this.descs = descs;
    }
}
