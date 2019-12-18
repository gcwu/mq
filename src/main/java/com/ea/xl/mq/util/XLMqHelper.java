package com.ea.xl.mq.util;

import com.ea.xl.mq.config.XLMqProperties;
import com.ea.xl.mq.model.XLMessage;
import org.springframework.context.ApplicationContext;

/**
 * @author wangtr
 * @version 1.0
 * @date 2019/6/10
 */
public abstract class XLMqHelper {

    /**
     * 消息队列配置项
     */
    protected XLMqProperties xlMqProperties;
    /**
     * 是否开启消息队列
     */
    protected String enable = "false";

    protected ApplicationContext applicationContext;

    public abstract boolean send(XLMessage message);

    public abstract void reciever();

    public XLMqProperties getXlMqProperties() {
        return xlMqProperties;
    }

    public void setXlMqProperties(XLMqProperties xlMqProperties) {
        this.xlMqProperties = xlMqProperties;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public XLMqHelper() {
    }
}
