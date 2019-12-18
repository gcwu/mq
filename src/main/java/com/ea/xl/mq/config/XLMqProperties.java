package com.ea.xl.mq.config;

/**
 * 消息队列配置类
 *
 * @author wangtr
 * @version 1.0
 * @date 2019/6/10
 */
public class XLMqProperties {

    /**
     * 消息队列开关：1开启0关闭
     */
    private String enable = "false";
    /**
     * 主机
     */
    private String host;
    /**
     * 端口
     */
    private String port;
    /**
     * 连接用户名
     */
    private String username;
    /**
     * 连接密码
     */
    private String password;
    /**
     * 虚拟主机virtualHost
     */
    private String virtualhost = "/";
    /**
     * 消息未确认时，同一时间给消费者推送消息的最大数量
     */
    private String prefetchCount;

    /**
     * 重试次数,默认3次
     */
    private int count = 3;

    /**
     * 失败多久重试一次 默认5秒（单位毫秒）
     */
    private int time = 5000;

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVirtualhost() {
        return virtualhost;
    }

    public void setVirtualhost(String virtualhost) {
        this.virtualhost = virtualhost;
    }

    public String getPrefetchCount() {
        return prefetchCount;
    }

    public void setPrefetchCount(String prefetchCount) {
        this.prefetchCount = prefetchCount;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
