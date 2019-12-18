package com.ea.xl.mq.model;

/**
 * 星链MQ定义的消息体
 *
 * @author wangtr
 * @version 1.0
 * @date 2019/6/13
 */
public class XLMessage {

    /**
     * routeKey
     */
    private String routeKey;

    /**
     * 队列名
     */
    private String queueName;

    /**
     * 交换机名
     */
    private String topicExchangeName;

    /**
     * 消息内容
     */
    private byte[] message;

    /**
     * 标识业务的唯一id
     */
    private String requestId;

    public String getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getTopicExchangeName() {
        return topicExchangeName;
    }

    public void setTopicExchangeName(String topicExchangeName) {
        this.topicExchangeName = topicExchangeName;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public XLMessage() {
    }

    public XLMessage(String routeKey, String queueName, String topicExchangeName, byte[] message, String requestId) {
        this.routeKey = routeKey;
        this.queueName = queueName;
        this.topicExchangeName = topicExchangeName;
        this.message = message;
        this.requestId = requestId;
    }

    public XLMessage(String routeKey, String queueName, String topicExchangeName) {
        this.routeKey = routeKey;
        this.queueName = queueName;
        this.topicExchangeName = topicExchangeName;
    }
}
