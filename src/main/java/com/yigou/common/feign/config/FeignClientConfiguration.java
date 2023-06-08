package com.yigou.common.feign.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration(proxyBeanMethods = false)
public class FeignClientConfiguration {

    @Bean
    @Scope("prototype")
    public ConnectionProperties connectionProperties() {
        return new ConnectionProperties();
    }
    @Autowired
    private ReactorLoadBalancerExchangeFilterFunction filterFunction;
    @Bean
    @Scope("prototype")
    @LoadBalanced
    public WebClient.Builder builder(){
        return WebClient.builder().filter(filterFunction);
    }






}
