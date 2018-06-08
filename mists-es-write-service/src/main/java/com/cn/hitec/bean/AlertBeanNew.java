package com.cn.hitec.bean;

/**
 * 告警详情实体类
 */
public class AlertBeanNew {

    //es 字段  index、type、id
    private String str_index;
    private String str_type;
    private String str_id;

    private String type;                    //" SYSTEM.ALARM.EI " 总的告警类型
    private String name;                    //告警名称
    private String message;                 //告警大致信息


    private String groupId;                 //事件类别ID
    private String occur_time;              //告警生成时间
    private String alertType;               //告警类型      01 表示超时
    private String eventType;               //事件类型
    private String eventTitle;              //事件标题
    private String level;                   //告警等级
    private String desc;                    //告警描述
    private String cause;                   //解决方案

    private String module;                  //环节名称
    private String dataName;                //资料名称
    private String subName;                //资料名称
    private String data_time;               //资料时次
    private String ipAddr;                  //资料ip

    private String path;                    //路径
    private String fileName;
    private String errorMessage;

    private String receive_time;            //资料接收时间
    private String should_time;             //资料应到时间
    private String last_time;               //资料最晚到达时间

    private String start_time;              //资料内部字段
    private String end_time;                //资料内部字段

    public AlertBeanNew(){}

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getStr_index() {
        return str_index;
    }

    public void setStr_index(String str_index) {
        this.str_index = str_index;
    }

    public String getStr_type() {
        return str_type;
    }

    public void setStr_type(String str_type) {
        this.str_type = str_type;
    }

    public String getStr_id() {
        return str_id;
    }

    public void setStr_id(String str_id) {
        this.str_id = str_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOccur_time() {
        return occur_time;
    }

    public void setOccur_time(String occur_time) {
        this.occur_time = occur_time;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getData_time() {
        return data_time;
    }

    public void setData_time(String data_time) {
        this.data_time = data_time;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getReceive_time() {
        return receive_time;
    }

    public void setReceive_time(String receive_time) {
        this.receive_time = receive_time;
    }

    public String getShould_time() {
        return should_time;
    }

    public void setShould_time(String should_time) {
        this.should_time = should_time;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
