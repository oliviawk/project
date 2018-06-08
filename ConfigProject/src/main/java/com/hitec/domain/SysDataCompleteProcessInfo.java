package com.hitec.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 
 * @ClassName: SysDataCompleteProcessInfo 
 * @Description: TODO(数据全流程关键资料配置信息) 
 * @author HYW
 * @date 2017年12月14日 下午3:16:14 
 *
 */
@Getter
@Setter
@Entity
public class SysDataCompleteProcessInfo implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -40059945615208988L;

    @Id
    @GeneratedValue
    private Long pkId;
    
    private String fileName;
    private String ctsCode;
    private String dpcCode;
    private String sodCode;
    private String fileType;
    private long sortId;            //大类排序
    private long sortPkId;          //资料排序
    private int offsetTimer;        //偏移时次
    private String timerCron;       //资料时次的cron表达式
    private String coalescingName;        
    private String fileGroup;              //分组（判断多对一）
    private int isMonitor;              //是否监视
    private int isProvinceMonitor;      //是否分省监视
    private int isImportant;            //是否关键
    
    public SysDataCompleteProcessInfo() {
        super();
    }

    public SysDataCompleteProcessInfo(Long pkId, String fileName,
            String ctsCode, String dpcCode, String sodCode, String fileType,
            long sortId, long sortPkId, int offsetTimer, String timerCron,
            String coalescingName, String fileGroup, int isMonitor, int isProvinceMonitor, int isImportant) {
        super();
        this.pkId = pkId;
        this.fileName = fileName;
        this.ctsCode = ctsCode;
        this.dpcCode = dpcCode;
        this.sodCode = sodCode;
        this.fileType = fileType;
        this.sortId = sortId;
        this.sortPkId = sortPkId;
        this.offsetTimer = offsetTimer;
        this.timerCron = timerCron;
        this.coalescingName = coalescingName;
        this.fileGroup = fileGroup;
        this.isMonitor = isMonitor;
        this.isProvinceMonitor = isProvinceMonitor;
        this.isImportant = isImportant;
    }


}
