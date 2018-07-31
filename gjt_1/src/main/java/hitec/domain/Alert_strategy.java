package hitec.domain;/**
 * Created by libin on 2018/7/31.
 */

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.io.Serializable;

/**
 * @ClassName alert_strategy
 * @Description TODO
 * @Author Li Cong
 * @Date 2018/7/31 12:23
 * @vERSION 1.0
 **/
@Entity
public class Alert_strategy implements Serializable {
    @Id
    @GeneratedValue
    private  Long Id;
    private  String   Strategy_name;
    private  String Send_users;
    private  String Wechart_content;
    private  Float Wechart_send_enable;
    private  String Sms_content;
    private  Float Sms_send_enable;
    private  Long Di_id;
    private  Long Template_id;

    public Alert_strategy() {
    }

    public Alert_strategy(Long id, String strategy_name, String send_users, String wechart_content, Float wechart_send_enable, String sms_content, Float sms_send_enable, Long di_id, Long template_id) {
        Id = id;
        Strategy_name = strategy_name;
        Send_users = send_users;
        Wechart_content = wechart_content;
        Wechart_send_enable = wechart_send_enable;
        Sms_content = sms_content;
        Sms_send_enable = sms_send_enable;
        Di_id = di_id;
        Template_id = template_id;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getStrategy_name() {
        return Strategy_name;
    }

    public void setStrategy_name(String strategy_name) {
        Strategy_name = strategy_name;
    }

    public String getSend_users() {
        return Send_users;
    }

    public void setSend_users(String send_users) {
        Send_users = send_users;
    }

    public String getWechart_content() {
        return Wechart_content;
    }

    public void setWechart_content(String wechart_content) {
        Wechart_content = wechart_content;
    }

    public Float getWechart_send_enable() {
        return Wechart_send_enable;
    }

    public void setWechart_send_enable(Float wechart_send_enable) {
        Wechart_send_enable = wechart_send_enable;
    }

    public String getSms_content() {
        return Sms_content;
    }

    public void setSms_content(String sms_content) {
        Sms_content = sms_content;
    }

    public Float getSms_send_enable() {
        return Sms_send_enable;
    }

    public void setSms_send_enable(Float sms_send_enable) {
        Sms_send_enable = sms_send_enable;
    }

    public Long getDi_id() {
        return Di_id;
    }

    public void setDi_id(Long di_id) {
        Di_id = di_id;
    }

    public Long getTemplate_id() {
        return Template_id;
    }

    public void setTemplate_id(Long template_id) {
        Template_id = template_id;
    }
}
