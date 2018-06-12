package com.cn.hitec.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class SourceDataInfo implements Serializable {
    private static final long serialVersionUID = -4005994623153399L;

    @Id
    @GeneratedValue
    private long pk_id;

    private String name;
    private String sub_name;
    private String path;
    private String file_name;
    private String descs;

    public SourceDataInfo() {
    }

    public SourceDataInfo(String name, String sub_name, String path, String file_name, String descs) {
        this.name = name;
        this.sub_name = sub_name;
        this.path = path;
        this.file_name = file_name;
        this.descs = descs;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public String getSub_name() {
        return sub_name;
    }

    public void setSub_name(String sub_name) {
        this.sub_name = sub_name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getDescs() {
        return descs;
    }

    public void setDescs(String descs) {
        this.descs = descs;
    }
}
