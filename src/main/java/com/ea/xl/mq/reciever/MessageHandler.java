package com.ea.xl.mq.reciever;

import com.ea.xl.mq.model.XLReceiver;
import com.ea.xl.mq.util.XLMqUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 监听消息的处理类
 *
 * @author wangtr
 * @version 1.0
 * @date 2019/6/12
 */
public abstract class MessageHandler{
    private  final Logger log = LoggerFactory.getLogger(XLMqUtil.class);
    public abstract void onMessage(XLReceiver receiver);

    public void ackSuccess(Envelope envelope, Channel channel) {
        try {
        channel.basicAck(envelope.getDeliveryTag(),false);
        }catch (Exception e){
            log.error("=========》执行basicAck失败"+e);
        }
    }

    /**
     * 抛弃此条消息
     * @param envelope
     * @param channel
     */
    public void ackFail(Envelope envelope, Channel channel) {
        try {
            channel.basicNack(envelope.getDeliveryTag(),false, false);
        }catch (Exception e){
            log.error("=========》执行ackFail失败"+e);
        }
    }

}
