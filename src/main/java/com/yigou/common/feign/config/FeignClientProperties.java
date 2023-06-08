package com.yigou.common.feign.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 每个服务使用各自的配置，在配置文件内配置好相应的联接参数等等
 */
@Configuration
@ConfigurationProperties(prefix = "com.yigou.common.feign")
public class FeignClientProperties {

    private Map<String,ConnectionProperties> services;

    public Map<String, ConnectionProperties> getServices() {
        return services;
    }

    public void setServices(Map<String, ConnectionProperties> services) {
        this.services = services;
    }



}
