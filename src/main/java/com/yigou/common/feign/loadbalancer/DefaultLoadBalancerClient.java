/*
 * Copyright 2013-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yigou.common.feign.loadbalancer;

import com.yigou.common.feign.bean.RequestParamInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LoadBalancerClientsProperties.class)
public class DefaultLoadBalancerClient {
    private static final Log LOG = LogFactory.getLog(DefaultLoadBalancerClient.class);
    private final LoadBalancerClient loadBalancerClient;
    private final LoadBalancerClientFactory loadBalancerClientFactory;

    public DefaultLoadBalancerClient(LoadBalancerClient loadBalancerClient,
                                     LoadBalancerClientFactory loadBalancerClientFactory) {
        this.loadBalancerClient = loadBalancerClient;
        this.loadBalancerClientFactory = loadBalancerClientFactory;
    }

    public ServiceInstance getServiceInstance(RequestParamInfo request) {
        final URI originalUri = URI.create(request.getUrl()+request.getRequestPath());
        String serviceId = originalUri.getHost();
        Assert.state(serviceId != null, "Request URI does not contain a valid hostname: " + originalUri);
        String hint = getHint(serviceId);
        DefaultRequest<RequestDataContext> lbRequest = new DefaultRequest<>(
                new RequestDataContext(buildRequestData(request), hint));
       ExecutorService executorService = Executors.newSingleThreadExecutor();
        // WebFlux异步调用，同步会报错
        Future<ServiceInstance> future = executorService.submit(() -> loadBalancerClient.choose(serviceId.toUpperCase(), lbRequest));
       ServiceInstance serviceInstance;
        try {
            serviceInstance = future.get();
        }catch (Exception e){
            LOG.error(e.getMessage());
            serviceInstance = null;
        }
        executorService.shutdown();
        return serviceInstance;
       /* ReactiveLoadBalancer<ServiceInstance> loadBalancer = loadBalancerClientFactory.getInstance(serviceId);
        if (loadBalancer == null) {
            return Mono.just(new EmptyResponse());
        }
        return Mono.from(loadBalancer.choose(lbRequest));*/
    }


    static RequestData buildRequestData(RequestParamInfo request) {
        HttpHeaders requestHeaders = new HttpHeaders();
        return new RequestData(request.getRequestMethod(), URI.create(request.getUrl()+request.getRequestPath()),
                requestHeaders, null, new HashMap<>());
    }


    private String getHint(String serviceId) {
        LoadBalancerProperties properties = loadBalancerClientFactory.getProperties(serviceId);
        String defaultHint = properties.getHint().getOrDefault("default", "default");
        String hintPropertyValue = properties.getHint().get(serviceId);
        return hintPropertyValue != null ? hintPropertyValue : defaultHint;
    }


}
