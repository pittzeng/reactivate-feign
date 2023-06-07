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


import com.yigou.common.feign.config.ReactivateFeignAutoConfiguration;
import com.yigou.common.feign.handler.RestHandler;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerClientAutoConfiguration;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * An autoconfiguration that instantiates {@link LoadBalancerClient}-based implementations
 * of {@link Client}.
 *
 * @author Olga Maciaszek-Sharma
 * @author Nguyen Ky Thanh
 * @author changjin wei(魏昌进)
 * @since 2.2.0
 */
@ConditionalOnClass(RestHandler.class)
@ConditionalOnBean({ LoadBalancerClient.class, LoadBalancerClientFactory.class })
@AutoConfigureBefore(ReactivateFeignAutoConfiguration.class)
@AutoConfigureAfter({ ReactorLoadBalancerClientAutoConfiguration.class, LoadBalancerAutoConfiguration.class })
@Configuration(proxyBeanMethods = false)
// Order is important here, last should be the default, first should be optional
// see
// https://github.com/spring-cloud/spring-cloud-netflix/issues/2086#issuecomment-316281653
@Import({DefaultLoadBalancerClient.class })
public class FeignLoadBalancerAutoConfiguration {

    public FeignLoadBalancerAutoConfiguration(){}

}
