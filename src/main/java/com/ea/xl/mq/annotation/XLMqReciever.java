package com.ea.xl.mq.annotation;

import java.lang.annotation.*;

/**
 * 开启星链消息队列监听，用于method
 *
 * @author wangtr
 * @version 1.0
 * @date 2019/6/10
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XLMqReciever {

    String queueName() default "";

}
