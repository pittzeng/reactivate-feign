package com.yigou.common.feign.config;

import com.yigou.common.feign.bean.FeignClientContext;
import com.yigou.common.feign.bean.FeignClientSpecification;
import com.yigou.common.feign.codes.CustomJsonDecoder;
import com.yigou.common.feign.codes.CustomJsonEncoder;
import com.yigou.common.feign.codes.HttpMessageConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;


@Configuration
@ConditionalOnProperty(value = "com.yigou.common.feign.enable", havingValue = "true")
public class ReactivateFeignAutoConfiguration {
    static {
        System.out.println("初始化自动配置类ReactivateFeignAutoConfiguration");
    }


    @Bean
    public FeignClientConfiguration feignClientConfiguration() {
        return new FeignClientConfiguration();
    }

    @Bean
    @Scope("prototype")
    public FeignClientSpecification feignClientSpecification() {
        return new FeignClientSpecification("default", new Class[]{ConnectionProperties.class});
    }

    public List<FeignClientSpecification> configurations = new ArrayList<>();

    @Bean
    public FeignClientProperties feignClientProperties() {
        return new FeignClientProperties();
    }

    @Bean
    @Scope("prototype")
    public ConnectionProperties connectionProperties() {
        return new ConnectionProperties();
    }

   @Bean
    public CustomJsonDecoder customJsonDecoder(){
        return new CustomJsonDecoder();
    }
    @Bean
    public CustomJsonEncoder customJsonEncoder(){
        return new CustomJsonEncoder();
    }
    @Bean
    public HttpMessageConfiguration httpMessageConfiguration(CustomJsonDecoder customJsonDecoder, CustomJsonEncoder customJsonEncoder){
        return  new HttpMessageConfiguration(customJsonDecoder,customJsonEncoder);
    }
    @Bean
    @Scope("prototype")
    public FeignClientContext feignClientContext() {
        FeignClientContext feignClientContext = new FeignClientContext();
        feignClientContext.setConfigurations(configurations);
        return feignClientContext;
    }

    public List<FeignClientSpecification> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<FeignClientSpecification> configurations) {
        this.configurations = configurations;
    }


}
