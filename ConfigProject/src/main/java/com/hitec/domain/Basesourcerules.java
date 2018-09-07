package com.hitec.domain;/**
 * Created by libin on 2018/9/4.
 */

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @ClassName Basesourcerules
 * @Description TODO
 * @Author Li Cong
 * @Date 2018/9/4 10:52
 * @vERSION 1.0
 **/
@Entity
public class Basesourcerules implements Serializable {
    private static final long serialVersionUID = -40059946153398L;
    @Id
    @GeneratedValue
    private long id;
    private Integer  maxAlerts;
    private  Integer currentAlerts;
    private  String  alertTimeRange;
    private  Integer interval;

    public Basesourcerules() {
    }

    public Basesourcerules(int maxAlerts, int currentAlerts, String alertTimeRange, int interval) {
        this.maxAlerts = maxAlerts;
        this.currentAlerts = currentAlerts;
        this.alertTimeRange = alertTimeRange;
        this.interval = interval;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getMaxAlerts() {
        return maxAlerts;
    }

    public void setMaxAlerts(Integer maxAlerts) {
        this.maxAlerts = maxAlerts;
    }

    public Integer getCurrentAlerts() {
        return currentAlerts;
    }

    public void setCurrentAlerts(Integer currentAlerts) {
        this.currentAlerts = currentAlerts;
    }

    public String getAlertTimeRange() {
        return alertTimeRange;
    }

    public void setAlertTimeRange(String alertTimeRange) {
        this.alertTimeRange = alertTimeRange;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }
}
