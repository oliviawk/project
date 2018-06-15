package com.cn.hitec.tools;

public enum AlertType {
    OVERTIME("01"),
    ABNORMAL("02"),
    DELAY("03"),
    FILEEX("04"),
    NOTE("05");

    String value;
    AlertType(String value) {
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
