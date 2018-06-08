package com.hitec.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
@Entity
public class DataInfo_old implements Serializable  {
    private static final long serialVersionUID = -400599461520898L;

    @Id
    @GeneratedValue
    private int pk_id;

    private int id;

    private int parent_id;
    private String name;
    private int is_data;
    private String strategy_ids;
    private int timeout_threshold;
    private String monitor_times;
    private int alert_level;
    private String di_name;
    private String sub_name;
    private int should_time;
    private String ip;
    private String file_path;
    private String module;
    private String service_type;

    public DataInfo_old(int id, int parent_id, String name, int is_data, String strategy_ids, int timeout_threshold, String monitor_times, int alert_level, String di_name, String sub_name, int should_time, String ip, String file_path, String module, String service_type) {
        this.id = id;
        this.parent_id = parent_id;
        this.name = name;
        this.is_data = is_data;
        this.strategy_ids = strategy_ids;
        this.timeout_threshold = timeout_threshold;
        this.monitor_times = monitor_times;
        this.alert_level = alert_level;
        this.di_name = di_name;
        this.sub_name = sub_name;
        this.should_time = should_time;
        this.ip = ip;
        this.file_path = file_path;
        this.module = module;
        this.service_type = service_type;
    }

    public DataInfo_old(){
        super();
    }

    public int getPk_id() {
        return pk_id;
    }

    public void setPk_id(int pk_id) {
        this.pk_id = pk_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
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

    public String getStrategy_ids() {
        return strategy_ids;
    }

    public void setStrategy_ids(String strategy_ids) {
        this.strategy_ids = strategy_ids;
    }

    public int getTimeout_threshold() {
        return timeout_threshold;
    }

    public void setTimeout_threshold(int timeout_threshold) {
        this.timeout_threshold = timeout_threshold;
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

    public String getDi_name() {
        return di_name;
    }

    public void setDi_name(String di_name) {
        this.di_name = di_name;
    }

    public String getSub_name() {
        return sub_name;
    }

    public void setSub_name(String sub_name) {
        this.sub_name = sub_name;
    }

    public int getShould_time() {
        return should_time;
    }

    public void setShould_time(int should_time) {
        this.should_time = should_time;
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
}
