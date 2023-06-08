package com.yigou.common.feign.bean;

import org.springframework.cloud.context.named.NamedContextFactory;

public class FeignClientSpecification implements NamedContextFactory.Specification {

    public FeignClientSpecification(String name,String className, Class<?>[]configuration) {
        this.name = name;
        this.className=className;
        this.configuration=configuration;
    }
    public FeignClientSpecification(String name, Class<?>[]configuration) {
        this.name = name;
        this.configuration=configuration;
    }

    /**
     * feignClient-ServiceName
     */
    private String name;
    /**
     * feign Client 注解所在的类
     */
    private String className;
    private Class<?>[] configuration;




    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Class<?>[] getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Class<?>[] configuration) {
        this.configuration = configuration;
    }
}
