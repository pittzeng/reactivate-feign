package com.yigou.common.feign.bean;

import com.yigou.common.feign.codes.CustomJsonDecoder;
import com.yigou.common.feign.codes.CustomJsonEncoder;
import com.yigou.common.feign.config.ConnectionProperties;
import com.yigou.common.feign.config.FeignClientProperties;
import com.yigou.common.feign.handler.RestHandler;
import com.yigou.common.feign.handler.webclient.WebClientHandler;
import com.yigou.common.feign.loadbalancer.DefaultLoadBalancerClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FeignClientFactoryBean implements FactoryBean<Object>, ApplicationContextAware {


    private Class<?> type;
    private String baseUrl;
    private ApplicationContext parentApplicationContext;

    private FeignClientContext feignClientContext;

    private RestHandler restHandler;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.parentApplicationContext = applicationContext;
    }

    public FeignClientFactoryBean(Class<?> clazz, String baseUrl) {
        this.type = clazz;
        this.baseUrl = baseUrl;
    }


    @Override
    public boolean isSingleton() {
        return true;
    }


    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public Object getObject() throws Exception {
        return getFeignObject();
    }

    private WebClient.Builder webclientBuilder;

    Object getFeignObject() {
        feignClientContext = parentApplicationContext.getBean(FeignClientContext.class);
        webclientBuilder = createBuilder();
        return Enhancer.create(type, new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                //if (method.getName().equals("equals")||method.getName().equals(""))
                RequestParamInfo requestParamInfo = extractMethodInfo(method, args);
                requestParamInfo.setUrl("http://" + baseUrl);

                DefaultLoadBalancerClient defaultLoadBalancerClient = feignClientContext.getInstance(baseUrl, DefaultLoadBalancerClient.class);
                ServiceInstance serviceInstance = defaultLoadBalancerClient.getServiceInstance(requestParamInfo);

                if (serviceInstance == null) {
                    log.error(requestParamInfo.getUrl() + "服务不存在");
                    return ServerResponse.notFound().build();

                }
               /*     URI serviceUrl = serviceInstance.getUri();
                    DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
                    URI uriBuilder = defaultUriBuilderFactory.builder().scheme(serviceUrl.getScheme()).host(serviceUrl.getHost()).port(serviceUrl.getPort()).build();

*/

                String url = serviceInstance.getUri().toString();
                restHandler = new WebClientHandler(webclientBuilder, url);
                return restHandler.invoke(requestParamInfo);

            }
        });
       /* return Enhancer.create(type, new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                RequestParamInfo requestParamInfo = extractMethodInfo(method, args);
                RestHandler restHandler = feignClientContext.getInstance(baseUrl, RestHandler.class);
                return restHandler.invoke(requestParamInfo);
            }
        });*/
    }


    public SslContext getTrustAllSslWebClient() {
        try {
            return SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        } catch (SSLException e) {
            //ignore
        }
        return null;
    }

    private WebClient.Builder createBuilder() {
        FeignClientProperties feignClientProperties = parentApplicationContext.getBean(FeignClientProperties.class);
        ConnectionProperties connectionProperties = feignClientProperties.getServices().get(this.baseUrl);
           /* HttpClient httpClient = feignClientContext.getInstance(this.baseUrl, HttpClient.class);
            httpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionProperties.getConnectionTimeOut());
            httpClient.doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(connectionProperties.getReadTimeOut(), TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(connectionProperties.getReadTimeOut(), TimeUnit.MILLISECONDS));
            });
             ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
            WebClient.Builder builder = feignClientContext.getInstance(this.baseUrl, WebClient.Builder.class);
            return builder.clientConnector(connector)
                    .exchangeStrategies(ExchangeStrategies.builder()
                            .codecs(clientCodecConfigurer -> {
                                clientCodecConfigurer.defaultCodecs().maxInMemorySize((connectionProperties.getMaxInMemorySize() * 1024 * 1024));
                            })
                            .build());
            */
        HttpClient httpClient = HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE)
                .responseTimeout(Duration.ofMillis(connectionProperties.getReadTimeOut()))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionProperties.getConnectionTimeOut())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(connectionProperties.getReadTimeOut(), TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(connectionProperties.getReadTimeOut(), TimeUnit.MILLISECONDS));
                });


        httpClient = httpClient.secure(sslContextSpec -> sslContextSpec.sslContext(getTrustAllSslWebClient()));


        CustomJsonDecoder customJsonDecoder = feignClientContext.getInstance(this.baseUrl, CustomJsonDecoder.class);
        CustomJsonEncoder customJsonEncoder = feignClientContext.getInstance(this.baseUrl, CustomJsonEncoder.class);
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return WebClient.builder()
                .clientConnector(connector)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> {
                                    configurer.defaultCodecs().maxInMemorySize((connectionProperties.getMaxInMemorySize() * 1024 * 1024));
                                    configurer.defaultCodecs().jackson2JsonDecoder(customJsonDecoder);
                                    configurer.defaultCodecs().jackson2JsonEncoder(customJsonEncoder);

                                    //configurer.customCodecs().register(new Jackson2CborDecoder());
                                    //configurer.customCodecs().register(new Jackson2CborEncoder());
                                }
                        )
                        .build()
                );
    }

    /**
     * 将方法中的注解信息，读取出来，路径参数，body 和返回值类型
     *
     * @param method 注解的方法
     * @param args   方法参数
     * @return
     * @throws NoSuchMethodException
     */
    protected RequestParamInfo extractMethodInfo(Method method, Object[] args) throws NoSuchMethodException {
        RequestParamInfo requestParamInfo = new RequestParamInfo();
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof GetMapping getMapping) {
                requestParamInfo.setRequestPath(getMapping.value()[0]);
                requestParamInfo.setRequestMethod(HttpMethod.GET);
            } else if (annotation instanceof PostMapping postMapping) {
                requestParamInfo.setRequestPath(postMapping.value()[0]);
                requestParamInfo.setRequestMethod(HttpMethod.POST);
            } else if (annotation instanceof DeleteMapping deleteMapping) {
                requestParamInfo.setRequestPath(deleteMapping.value()[0]);
                requestParamInfo.setRequestMethod(HttpMethod.DELETE);
            } else {
                throw new NoSuchMethodException();
            }
        }
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            //是否带路径变量参数
            PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                param.put(pathVariable.value(), Collections.singletonList(args[i].toString()));
            }
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if (requestParam != null) {
                param.put(requestParam.value(), Collections.singletonList(args[i].toString()));
            }
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody == null) {
                requestParamInfo.setRequestBody(null);
            } else {
                requestParamInfo.setRequestBody(args[i]);
            }
        }
        requestParamInfo.setRequestParam(param);
        boolean assignableFrom = method.getReturnType().isAssignableFrom(Flux.class);
        requestParamInfo.setReturnFlux(assignableFrom);
        Type[] actualTypeArguments = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments();

        Optional<Type> optionalType = Arrays.stream(actualTypeArguments).findFirst();
        if (optionalType.isPresent()) {
            requestParamInfo.setResultBody(optionalType.get());
        } else {
            requestParamInfo.setResultBody(new Object());
        }
        return requestParamInfo;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public ApplicationContext getParentApplicationContext() {
        return parentApplicationContext;
    }

    public void setParentApplicationContext(ApplicationContext parentApplicationContext) {
        this.parentApplicationContext = parentApplicationContext;
    }

    public FeignClientContext getFeignClientContext() {
        return feignClientContext;
    }

    public void setFeignClientContext(FeignClientContext feignClientContext) {
        this.feignClientContext = feignClientContext;
    }

    public RestHandler getRestHandler() {
        return restHandler;
    }

    public void setRestHandler(RestHandler restHandler) {
        this.restHandler = restHandler;
    }
}
