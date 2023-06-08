package com.yigou.common.feign.codes;


import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class HttpMessageConfiguration implements WebFluxConfigurer {

    private final CustomJsonDecoder customJsonDecoder;
    private final CustomJsonEncoder customJsonEncoder;


    public HttpMessageConfiguration(CustomJsonDecoder customJsonDecoder, CustomJsonEncoder customJsonEncoder) {
        this.customJsonDecoder = customJsonDecoder;
        this.customJsonEncoder = customJsonEncoder;
    }


    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        //自定义解码器
        //   configurer.defaultCodecs().jackson2JsonDecoder(new CustomJsonDecoder(objectMapper));
        //自定义编码器 编码器的道理和解码器基本类似，可以自行尝试
        // 自动加载 classpath 中所有 Jackson Module

        configurer.defaultCodecs().jackson2JsonEncoder(customJsonEncoder);
        configurer.defaultCodecs().jackson2JsonDecoder(customJsonDecoder);
    }




}
