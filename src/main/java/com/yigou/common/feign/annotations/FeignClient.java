package com.yigou.common.feign.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface FeignClient {
    /**
     * serviceName 和Url二选一个配置即可
     * @return
     */
    String serviceName() default "";
    String serviceUrl() default "";

    /**
     * 失败回调
     * @return
     */
    Class<?>fallback() default Void.class;

    /**
     * serviceContext
     * @return
     */
    String path() default "";


}
