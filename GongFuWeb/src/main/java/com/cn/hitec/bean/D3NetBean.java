package com.cn.hitec.bean;

/**
 * 
 * @ClassName: D3NetBean 
 * @Description: TODO(D3网络流量控件所需数据的bean) 
 * @author HYW
 * @date 2017年8月27日 下午4:15:27 
 *
 */
public class D3NetBean {

    private String time;
    private double upload;
    private double down;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public double getUpload() {
		return upload;
	}
	public void setUpload(double upload) {
		this.upload = upload;
	}
	public double getDown() {
		return down;
	}
	public void setDown(double down) {
		this.down = down;
	}
    
    
}
