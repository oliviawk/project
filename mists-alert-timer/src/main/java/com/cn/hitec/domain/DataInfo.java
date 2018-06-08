package com.cn.hitec.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class DataInfo implements Serializable  {
    private static final long serialVersionUID = -40059946153398L;

    @Id
    @GeneratedValue
    private long pk_id;

    private long id;
    private long parent_id;
    private String name;
    private int is_data;
    private String timeout_threshold;
    private String monitor_times;
    private int alert_level;
    private String sub_name;
    private String should_time;
    private String ip;
    private String file_path;
    private String file_size_define;
    private String file_name_define;
    private String module;
    private String service_type;
    private String start_moniter;
    private int  regular;

    public DataInfo() { super();}


    public long getPk_id() {
        return pk_id;
    }

    public void setPk_id(long pk_id) {
        this.pk_id = pk_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParent_id() {
        return parent_id;
    }

    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIs_data() {
        return is_data;
    }

    public void setIs_data(int is_data) {
        this.is_data = is_data;
    }

    public String getMonitor_times() {
        return monitor_times;
    }

    public void setMonitor_times(String monitor_times) {
        this.monitor_times = monitor_times;
    }

    public int getAlert_level() {
        return alert_level;
    }

    public void setAlert_level(int alert_level) {
        this.alert_level = alert_level;
    }

    public String getSub_name() {
        return sub_name;
    }

    public void setSub_name(String sub_name) {
        this.sub_name = sub_name;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getStart_moniter() {
        return start_moniter;
    }

    public void setStart_moniter(String start_moniter) {
        this.start_moniter = start_moniter;
    }

    public String getTimeout_threshold() {
        return timeout_threshold;
    }

    public void setTimeout_threshold(String timeout_threshold) {
        this.timeout_threshold = timeout_threshold;
    }

    public String getShould_time() {
        return should_time;
    }

    public void setShould_time(String should_time) {
        this.should_time = should_time;
    }

    public int getRegular() {
        return regular;
    }

    public void setRegular(int regular) {
        this.regular = regular;
    }

    public String getFile_size_define() {
        return file_size_define;
    }

    public void setFile_size_define(String file_size_define) {
        this.file_size_define = file_size_define;
    }

    public String getFile_name_define() {
        return file_name_define;
    }

    public void setFile_name_define(String file_name_define) {
        this.file_name_define = file_name_define;
    }
}
