package com.ea.xl.mq.listener;

import com.ea.xl.mq.config.XLMqProperties;
import com.ea.xl.mq.util.XLMqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * @author wangtr
 * @version 1.0
 * @date 2019/6/17
 */
public class XLMqStartupListener implements ServletContextListener {

    private static Logger log = LoggerFactory.getLogger(XLMqStartupListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        //获取applicationContext
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContextEvent.getServletContext());

        XLMqProperties xlMqProperties = applicationContext.getBean("xlMqProperties", XLMqProperties.class);

        if ("false".equals(xlMqProperties.getEnable())) {
            log.warn("##################手动关闭了消息队列##################");
        }

        //初始化XLMqUtil开启监听
        XLMqUtil helper = new XLMqUtil();
        helper.setXlMqProperties(xlMqProperties);
        helper.setEnable(xlMqProperties.getEnable());
        helper.setApplicationContext(applicationContext);
        helper.reciever();

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
