package com.yigou.common.feign.bean;

import com.yigou.common.feign.config.FeignClientConfiguration;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

import java.util.HashMap;
import java.util.Map;


public class FeignClientContext extends NamedContextFactory<FeignClientSpecification> {

    static {
        System.out.println("初始化了FeignClientContext");
    }

    private final static String sourceName = "com.yigou.cloud.common.feign.services";
    private final static String properties = "default";

    public FeignClientContext() {
        this(new HashMap<>());
    }


    public FeignClientContext(Map<String, ApplicationContextInitializer<GenericApplicationContext>> applicationContextInitializers) {
        super(FeignClientConfiguration.class, sourceName, properties, applicationContextInitializers);
    }


    public <T> T  getInstance(String serviceName,String beanName,Class<T>type){
        return getContext(serviceName).getBean(beanName,type);
    }
}
