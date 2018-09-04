package com.cn.hitec.tools;

import java.util.Calendar;

/**
 * @description: 描述信息
 * @author: fukl
 * @data: 2018年08月21日 下午4:02
 */
public enum DateUnit {
    HH,MM,SS;
    public static int getDateUnit(DateUnit dateUnit){
        DateUnit b = dateUnit;
        switch (b){
            case HH:
                return Calendar.HOUR_OF_DAY;
            case MM:
                return Calendar.MINUTE;
            case SS:
                return Calendar.SECOND;
            default:
                return Calendar.MINUTE;
        }
    }
}
