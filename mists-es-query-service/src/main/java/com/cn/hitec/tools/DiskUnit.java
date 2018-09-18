package com.cn.hitec.tools;

import java.math.BigDecimal;

/**
 * @description: 描述信息
 * @author: fukl
 * @data: 2018年08月20日 下午4:24
 */
public enum DiskUnit {
    K("K", 1),KB("KB", 1), M("M",2), MB("MB",2),G("G", 3), GB("GB", 3), T("T", 4),TB("TB", 4);
    // 成员变量
    private String unit;
    private int pow;
    private long nl = 1024;

    public static final String UNIT_KB = "KB";
    public static final String UNIT_MB = "MB";
    public static final String UNIT_G = "G";
    public static final String UNIT_T = "T";

    // 构造方法
    private DiskUnit(String unit,int pow) {
        this.unit = unit;
        this.pow = pow;
    }

    public static String getUnit(String str){
        String s = null;
        for (DiskUnit du : DiskUnit.values()){
            if (str.toUpperCase().equals(du.unit)){
                s = du.unit;
                break;
            }
        }
        return s;
    }

    /**
     * 转换单位
     * @param strUnit   单位：M、G、T
     * @param lSize     数值大小
     * @param scale     小数点后保留几位
     * @return
     */
    //返回文件总大小
    public static double transforDiskSize(String strUnit,double lSize,int scale) {
        double ret = 0.0;
        for (DiskUnit du : DiskUnit.values()){
            if (du.unit.equals(strUnit.toUpperCase())){
                double a = Math.pow(du.nl,du.pow);
                BigDecimal b = new BigDecimal(lSize / a);
                ret = b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
                break;
            }
        }

        return ret;
    }

}
