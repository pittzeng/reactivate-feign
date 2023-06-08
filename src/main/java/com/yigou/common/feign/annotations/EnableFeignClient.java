package com.yigou.common.feign.annotations;

import com.yigou.common.feign.config.ImportFeignBeanDefinitionRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ImportFeignBeanDefinitionRegister.class)
public @interface EnableFeignClient {
    String[] basePackages() default "";
}
