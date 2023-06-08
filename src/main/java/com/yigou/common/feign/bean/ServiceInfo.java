package com.yigou.common.feign.bean;

public class ServiceInfo {


    /**
     * serviceName 或serviceUrl最后都将转成最后的BaseUrl，进行请求
     */
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

}
