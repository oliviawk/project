package hitec.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class DataSourceSetting implements Serializable{

	private static final long serialVersionUID = -959264530724062543L;
	
	@Id
	@GeneratedValue
	private long pkId;
	private String name;
	private String directory;
	private String fileName;
	private String timeFormat;
	private String sendUser;
	private String ipAddr;
	private String dataType;
	private String departmentName;
	private String phone;
	private String useDepartment;
	private String moniterTimer;
	
	public DataSourceSetting() {
		super();
	}
	
	public long getPkId() {
		return pkId;
	}

	public void setPkId(long pkId) {
		this.pkId = pkId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getSendUser() {
		return sendUser;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUseDepartment() {
		return useDepartment;
	}

	public void setUseDepartment(String useDepartment) {
		this.useDepartment = useDepartment;
	}
	
	public String getMoniterTimer() {
		return moniterTimer;
	}

	public void setMoniterTimer(String moniterTimer) {
		this.moniterTimer = moniterTimer;
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}
	
}
