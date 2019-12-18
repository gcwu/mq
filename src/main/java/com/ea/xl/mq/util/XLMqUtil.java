package com.ea.xl.mq.util;

import com.ea.xl.mq.annotation.XLMqReciever;
import com.ea.xl.mq.model.XLMessage;
import com.ea.xl.mq.model.XLReceiver;
import com.ea.xl.mq.reciever.MessageHandler;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author wangtr
 * @version 1.0
 * @date 2019/6/10
 */
public class XLMqUtil extends XLMqHelper {

    private  final Logger log = LoggerFactory.getLogger(XLMqUtil.class);

    ConnectionFactory factory = new ConnectionFactory();

    private static Channel channel = null;

    ConnectionFactory getFactory(){
        factory.setHost(xlMqProperties.getHost());
        factory.setPort(Integer.parseInt(xlMqProperties.getPort()));
        factory.setUsername(xlMqProperties.getUsername());
        factory.setPassword(xlMqProperties.getPassword());
        factory.setVirtualHost(xlMqProperties.getVirtualhost());
        return factory;
    }

    @Override
    public boolean send(XLMessage message) {
        if("false".equals(enable)){
            log.warn("消息队列是手动关闭的");
            return false;
        }
        try {
            //Connection connection = getFactory().newConnection();
            //Channel channel =  connection.createChannel();
            //指定一个topic类型,持久化的,非自动删除的交换机
            channel.exchangeDeclare(message.getTopicExchangeName(), "topic", true, false, null);
            //创建一个持久化的,百排他的,非自动删除的队列
            channel.queueDeclare(message.getQueueName(),true,false,false,null);

            //通过路由键绑定队列和交换机
            channel.queueBind(message.getQueueName(),message.getTopicExchangeName(),message.getRouteKey());

            HashMap<String,Object> headers = new HashMap<>();
            headers.put("x-death",1);

            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2) //deliveryMode=1代表不持久化，deliveryMode=2代表持久化
                    .contentEncoding("UTF-8")
                    .headers(headers)
                    .build();
            // 开启发送方确认模式
            channel.confirmSelect();
            channel.basicPublish(message.getTopicExchangeName(), message.getRouteKey(), properties, message.getMessage());
//            channel.addConfirmListener(new ConfirmListener() {
//                @Override
//                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
//                    System.out.println("未确认消息，标识：" + deliveryTag);
//                }
//                @Override
//                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
//                    System.out.println(String.format("已确认消息，标识：%d，多个消息：%b", deliveryTag, multiple));
//                }
//            });
            //channel.close();
            //connection.close();
            return true;
        } catch (Exception e) {
            log.error("消息队列发送消息失败, 原因:{}", e);
            return false;
        }

    }

    public void reciever() {
        if("false".equals(enable)){
            log.warn("消息队列是手动关闭的");
            return ;
        }
        log.info("---------->开始查找消息队列监听");
        //根据接口类型返回相应的所有bean
        Map<String, MessageHandler> beans = applicationContext.getBeansOfType(MessageHandler.class);
        //获取到连接以及信道
        Connection connection = null;
        try {
            connection = getFactory().newConnection();
            channel = connection.createChannel();
        } catch (IOException e) {
            log.error("------->启动消息队列监听失败{}", e.getMessage());
        } catch (TimeoutException e) {
            log.error("------->启动消息队列监听失败{}", e.getMessage());
        }
        for (Map.Entry<String, MessageHandler> entry : beans.entrySet()) {
            log.info("--------->扫描到消息队列监听{}.{}",entry.getValue().getClass().getName(),entry.getKey());
            Class classes = entry.getValue().getClass();
            Method[] methods = classes.getDeclaredMethods();
            for (Method method : methods) {
                XLMqReciever xlMqReciever = method.getAnnotation(XLMqReciever.class);
                if (xlMqReciever != null && !StringUtils.isEmpty(xlMqReciever.queueName()) ) {
                    try {
                        if (xlMqProperties.getPrefetchCount()!=null && !"".equals(xlMqProperties.getPrefetchCount())){
                            //fasle代表该设置应用于consumer级别，不是channel级别
                            channel.basicQos(0,Integer.parseInt(xlMqProperties.getPrefetchCount()),false);
                        }
                        channel.queueDeclare(xlMqReciever.queueName(),true,false,false,null);
                        //设置autoAck为false，即需要手动确认
                        String s = channel.basicConsume(xlMqReciever.queueName(),false,new DefaultConsumer(channel) {
                            @Override public void handleDelivery(String consumerTag, Envelope envelope,
                                                                 AMQP.BasicProperties properties, byte[] body) {
                                XLReceiver receiver = new XLReceiver(body);
                                try {
                                    method.invoke(entry.getValue(),receiver);
                                    entry.getValue().ackSuccess(envelope,channel);
                                } catch (Exception e){
                                    log.error("------->执行消息失败,请检查方法【{}】",method, e);
                                    entry.getValue().ackFail(envelope,channel);
                                    retry(envelope,properties,body,method);
                                }
                            }
                        });
                        System.out.println("==================>ssssssss"+s);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("------->启动消息队列监听失败{}", e.getMessage());
                    }
                }
            }
        }

    }

    /**
     * 重试
     * @param envelope
     * @param properties
     * @param body
     * @param method
     */
    private void retry(Envelope envelope, AMQP.BasicProperties properties, byte[] body,Method method){

        try {
            Thread.sleep(xlMqProperties.getTime());
        } catch (InterruptedException e) {
            log.error("Thread.sleep error",e);
        }
        //判断失败次数
        long retryCount = getRetryCount(properties);
        log.info("=============第{}次重试机制开始============",retryCount);
        if(retryCount>=xlMqProperties.getCount()){
            //如果失败超过三次
            log.error("=============消息{}次都失败了，请检查代码 ============>method={} ;参数 body={}"
                    ,retryCount,method,new String(body));
        }else{
            //发送到重试队列,10s后重试

            HashMap<String,Object> headers = new HashMap<>();
            headers.put("x-death",retryCount+1);

            AMQP.BasicProperties newProperties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2) //deliveryMode=1代表不持久化，deliveryMode=2代表持久化
                    .contentEncoding("UTF-8")
                    .headers(headers)
                    .build();
            try {
                channel.basicPublish(envelope.getExchange(),envelope.getRoutingKey(),newProperties,body);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private long getRetryCount(AMQP.BasicProperties properties){
        long retryCount = 0L;
        Map<String,Object> header = properties.getHeaders();
        if(header != null && header.containsKey("x-death")){
            try {
                retryCount  = Integer.parseInt(header.get("x-death").toString());
            }catch (Exception e){
                log.error("x-death丢失",e);
            }

        }
        return retryCount;
    }

}
