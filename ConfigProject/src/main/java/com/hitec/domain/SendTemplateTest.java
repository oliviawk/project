package com.hitec.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @Description: 发送模板类
 * @author: fukl
 * @data: 2018年02月07日 14:34
 */
@Getter
@Setter
@Entity
public class SendTemplateTest implements Serializable {

    private static final long serialVersionUID = -40059945615208988L;

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String type;
    private String wechartContentTemplate;
    private String wechartSendEnable;
    private String smsContentTemplate;
    private String smsSendEnable;

    public SendTemplateTest() {
        super();
    }

    public SendTemplateTest(long id, String name, String type, String wechartContentTemplate, String wechartSendEnable, String smsContentTemplate, String smsSendEnable) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.wechartContentTemplate = wechartContentTemplate;
        this.wechartSendEnable = wechartSendEnable;
        this.smsContentTemplate = smsContentTemplate;
        this.smsSendEnable = smsSendEnable;
    }


}
