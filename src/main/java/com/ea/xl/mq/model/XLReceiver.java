package com.ea.xl.mq.model;

/**
 * 星链MQ定义的接收对象体
 *
 * @author wangtr
 * @version 1.0
 * @date 2019/6/15
 */
public class XLReceiver {

    /**
     * 消息内容
     */
    private byte[] body;

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public XLReceiver() {
    }

    public XLReceiver(byte[] body) {
        this.body = body;
    }
}
