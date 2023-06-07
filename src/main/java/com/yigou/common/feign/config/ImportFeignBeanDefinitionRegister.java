package com.yigou.common.feign.config;

import com.yigou.common.feign.annotations.EnableFeignClient;
import com.yigou.common.feign.annotations.FeignClient;
import com.yigou.common.feign.bean.FeignClientFactoryBean;
import com.yigou.common.feign.bean.FeignClientSpecification;
import com.yigou.common.feign.utils.ClassPathScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Map;

@Slf4j
public class ImportFeignBeanDefinitionRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware, ApplicationContextAware {
    private ResourceLoader resourceLoader;
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata metadata, @NonNull BeanDefinitionRegistry registry) {

        this.registerFeignClients(metadata, registry);

    }

    public void registerFeignClients(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        LinkedHashSet<BeanDefinition> candidateComponents = new LinkedHashSet<>();
        ClassPathScanner classPathScanner = new ClassPathScanner(this.environment, this.resourceLoader);
        for (String basePackage : classPathScanner.getBasePackageByAnnotation(metadata, EnableFeignClient.class)) {
            candidateComponents.addAll(classPathScanner.getAllBeanDefinitionByPackagePath(basePackage, FeignClient.class));
        }


        for (BeanDefinition candidateComponent : candidateComponents) {
            if (candidateComponent instanceof AnnotatedBeanDefinition beanDefinition) {
                AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                Assert.isTrue(annotationMetadata.isInterface(), "@FeignClientBuilder can only be specified on an interface");
                Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(FeignClient.class.getName());
               /* attributes.forEach((key, value) -> log.info("ReactivateFeign上的注解：" + key + "     注解的值：　" + value));
                String serviceName = this.getServiceName(attributes);
                String feignClientClassName = annotationMetadata.getClassName();*/
                //FeignClientContext feignClientContext=new FeignClientContext();
                // feignClientContext.setApplicationContext(this.applicationContext);


                this.registerFeignClient(registry, annotationMetadata, attributes);
            }
        }
    }

    /**
     * 获取注解的服务提供方的服务名或服务Url
     *
     * @param attributes 注解内的所有属性
     * @return
     */
    private String getServiceName(Map<String, Object> attributes) {
        if (attributes == null) {
            return null;
        } else {
            String value = (String) attributes.get("serviceName");
            if (!StringUtils.hasText(value)) {
                value = (String) attributes.get("serviceUrl");
            }
            if (StringUtils.hasText(value)) {
                return value;
            } else {
                throw new IllegalStateException("Either 'name' or 'value' must be provided in @" + FeignClient.class.getSimpleName());
            }
        }
    }

    private void registerClientConfiguration2(BeanDefinitionRegistry registry, String name,String className, Object configuration) {

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(FeignClientSpecification.class);
        builder.addConstructorArgValue(name);
        builder.addConstructorArgValue(className);
        builder.addConstructorArgValue(configuration);
        registry.registerBeanDefinition(name+"."+FeignClientSpecification.class.getSimpleName(), builder.getBeanDefinition());

    }

    private void registerClientConfiguration(BeanDefinitionRegistry registry, String name, Object configuration) {

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(FeignClientSpecification.class);
        builder.addConstructorArgValue(name);
        builder.addConstructorArgValue(configuration);
        registry.registerBeanDefinition(FeignClientSpecification.class.getCanonicalName(), builder.getBeanDefinition());

    }

    private void registerFeignClient(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata, Map<String, Object> attributes) {
        String className = annotationMetadata.getClassName();
        this.eagerlyRegisterFeignClientBeanDefinition(className, attributes, registry);
    }

    private void eagerlyRegisterFeignClientBeanDefinition(String className, Map<String, Object> attributes, BeanDefinitionRegistry registry) {
        Class<?> aClass = null;
        try {
            aClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("代理类未找到FeignClient不生效："+className);
            throw new RuntimeException(e);
        }
        this.validate(attributes);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(FeignClientFactoryBean.class);


        builder.addConstructorArgValue(aClass);
        String baseUrl = this.getName(attributes);
        builder.addConstructorArgValue(baseUrl);
        registry.registerBeanDefinition(className, builder.getBeanDefinition());
    }


    /**
     * 简单验证fallback是否为实体类
     *
     * @param attributes
     */
    private void validate(Map<String, Object> attributes) {
        AnnotationAttributes annotation = AnnotationAttributes.fromMap(attributes);
        validateFallback(annotation.getClass("fallback"));
        //validateFallbackFactory(annotation.getClass("fallbackFactory"));
    }

    static void validateFallback(final Class clazz) {
        Assert.isTrue(!clazz.isInterface(), "Fallback class must implement the interface annotated by @FeignClient");
    }

    String getName(Map<String, Object> attributes) {
        return this.getName((ConfigurableBeanFactory) null, attributes);
    }

    String getName(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
        String name = (String) attributes.get("serviceId");
        if (!StringUtils.hasText(name)) {
            name = (String) attributes.get("name");
        }

        if (!StringUtils.hasText(name)) {
            name = (String) attributes.get("value");
        }
        if (!StringUtils.hasText(name)) {
            name = (String) attributes.get("serviceName");
        }
        if (!StringUtils.hasText(name)) {
            name = (String) attributes.get("serviceUrl");
        }

        name = this.resolve(beanFactory, name);
        return getName(name);
    }

    static String getName(String name) {
        if (!StringUtils.hasText(name)) {
            return "";
        } else {
            String host = null;
            try {
                String url;
                if (!name.startsWith("http://") && !name.startsWith("https://")) {
                    url = "http://" + name;
                } else {
                    url = name;
                }

                host = (new URI(url)).getHost();
            } catch (URISyntaxException var3) {
            }

            Assert.state(host != null, "Service id not legal hostname (" + name + ")");
            return name;
        }
    }

    private String resolve(ConfigurableBeanFactory beanFactory, String value) {
        if (StringUtils.hasText(value)) {
            if (beanFactory == null) {
                return this.environment.resolvePlaceholders(value);
            } else {
                BeanExpressionResolver resolver = beanFactory.getBeanExpressionResolver();
                String resolved = beanFactory.resolveEmbeddedValue(value);
                if (resolver == null) {
                    return resolved;
                } else {
                    Object evaluateValue = resolver.evaluate(resolved, new BeanExpressionContext(beanFactory, (Scope) null));
                    return evaluateValue != null ? String.valueOf(evaluateValue) : null;
                }
            }
        } else {
            return value;
        }
    }
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
