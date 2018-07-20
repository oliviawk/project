package com.cn.hitec.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * 
 * @ClassName: DataInfo 
 * @Description: TODO(连接数据库，数据源配置实体类) 
 * @author HYW
 * @date 2018年4月09日 上午10:18:45 
 *
 */
@Entity
public class DataInfo implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -5898458914568801789L;

    @Id
    @GeneratedValue
    private long pkId;
    
    private long id;
    private long parentId;
    private String name;
    private int isData;
    private String subName;
    private String monitor_times;
    private String should_time;
    private String ip;
    
    public DataInfo() {}

    public DataInfo(long pkId, long id, long parentId, String name, int isData, String subName,
    		String monitor_times, String should_time, String ip) {
        super();
        this.pkId = pkId;
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.isData = isData;
        this.subName = subName;
        this.monitor_times = monitor_times;
        this.should_time = should_time;
        this.ip = ip;
    }

    public long getPkId() {
        return pkId;
    }

    public void setPkId(long pkId) {
        this.pkId = pkId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsData() {
        return isData;
    }

    public void setIsData(int isData) {
        this.isData = isData;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

	public String getMonitor_times() {
		return monitor_times;
	}

	public void setMonitor_times(String monitor_times) {
		this.monitor_times = monitor_times;
	}

	public String getShould_time() {
		return should_time;
	}

	public void setShould_time(String should_time) {
		this.should_time = should_time;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
    
}
