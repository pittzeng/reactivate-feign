package com.yigou.common.feign.config;

import com.yigou.common.feign.bean.TimeOutRetry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "com.yigou.common.feign.services.default")
public class ConnectionProperties {
    /**
     * Http连接超时
     */
    private Integer connectionTimeOut;
    /**
     * 查询请求超时
     */
    private Integer readTimeOut;
    /**
     * 转发
     */
    private boolean followRedirects;

    /**
     * 请求最大请求
     */
    private Integer maxInMemorySize;
    private TimeOutRetry timeOutRetry;

    public Integer getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public void setConnectionTimeOut(Integer connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public Integer getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(Integer readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public Integer getMaxInMemorySize() {
        return maxInMemorySize;
    }

    public void setMaxInMemorySize(Integer maxInMemorySize) {
        this.maxInMemorySize = maxInMemorySize;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public TimeOutRetry getTimeOutRetry() {
        return timeOutRetry;
    }

    public void setTimeOutRetry(TimeOutRetry timeOutRetry) {
        this.timeOutRetry = timeOutRetry;
    }
}
