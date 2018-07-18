package hitec.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * 
 * @ClassName: DataInfo 
 * @Description: TODO(连接数据库，查询告警台左侧告警分类的实体类) 
 * @author HYW
 * @date 2018年3月12日 下午2:21:45 
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
    private String subName;
    private String monitorTimes;
    private String shouldTime;
    private String timeoutThreshold;
    private int regular;
    private String ip;
    private String filePath;
    private String module;
    private String serviceType;
    private int alertLevel;
    private String startMoniter;
    private int isData;
	private  String fileNameDefine;

	public DataInfo() {
	}

	public DataInfo(long id, long parentId, String name, String subName, String monitorTimes, String shouldTime, String timeoutThreshold, int regular, String ip, String filePath, String module, String serviceType, int alertLevel, String startMoniter, int isData, String fileNameDefine) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.subName = subName;
		this.monitorTimes = monitorTimes;
		this.shouldTime = shouldTime;
		this.timeoutThreshold = timeoutThreshold;
		this.regular = regular;
		this.ip = ip;
		this.filePath = filePath;
		this.module = module;
		this.serviceType = serviceType;
		this.alertLevel = alertLevel;
		this.startMoniter = startMoniter;
		this.isData = isData;
		this.fileNameDefine = fileNameDefine;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
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

	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	public String getMonitorTimes() {
		return monitorTimes;
	}

	public void setMonitorTimes(String monitorTimes) {
		this.monitorTimes = monitorTimes;
	}

	public String getShouldTime() {
		return shouldTime;
	}

	public void setShouldTime(String shouldTime) {
		this.shouldTime = shouldTime;
	}

	public String getTimeoutThreshold() {
		return timeoutThreshold;
	}

	public void setTimeoutThreshold(String timeoutThreshold) {
		this.timeoutThreshold = timeoutThreshold;
	}

	public int getRegular() {
		return regular;
	}

	public void setRegular(int regular) {
		this.regular = regular;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public int getAlertLevel() {
		return alertLevel;
	}

	public void setAlertLevel(int alertLevel) {
		this.alertLevel = alertLevel;
	}

	public String getStartMoniter() {
		return startMoniter;
	}

	public void setStartMoniter(String startMoniter) {
		this.startMoniter = startMoniter;
	}

	public int getIsData() {
		return isData;
	}

	public void setIsData(int isData) {
		this.isData = isData;
	}

	public String getFileNameDefine() {
		return fileNameDefine;
	}

	public void setFileNameDefine(String fileNameDefine) {
		this.fileNameDefine = fileNameDefine;
	}
}
