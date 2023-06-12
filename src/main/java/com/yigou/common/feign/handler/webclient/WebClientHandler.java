package com.yigou.common.feign.handler.webclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yigou.common.feign.bean.RequestParamInfo;
import com.yigou.common.feign.handler.RestHandler;
import org.reactivestreams.Publisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.Map;
import java.util.Objects;

//@Configuration
//@ConditionalOnBean({WebClientConfig.class,LoadBalancerBeanPostProcessorAutoConfiguration.class})
public class WebClientHandler implements RestHandler {


    public final String baseUrl;

    private WebClient.Builder webClientBuilder;


    public WebClientHandler(WebClient.Builder builder, String baseUrl) {
        this.webClientBuilder = builder;
        this.baseUrl = baseUrl;
    }


    public WebClient getWebClientByBaseUrl() {
        String url = this.baseUrl;
        if (StringUtils.hasLength(this.baseUrl) && !baseUrl.startsWith("http")) {
            url = "http://" + this.baseUrl;
        }
        return webClientBuilder
                .baseUrl(url)
                .build();
    }

    @Override
    public Publisher<?> invoke(RequestParamInfo requestParamInfo) {
        WebClient.RequestBodySpec uri = getWebClientByBaseUrl()
                .method(requestParamInfo.getRequestMethod())
                .uri(uriBuilder -> uriBuilder
                        .path(requestParamInfo.getRequestPath())
                        .queryParams(requestParamInfo.getRequestParam())
                        .build());
        WebClient.RequestHeadersSpec<?> requestHeadersSpec;
        if (requestParamInfo.getRequestBody() == null || requestParamInfo.getRequestBody().equals(Void.class)) {
            requestHeadersSpec = uri
                    .accept(MediaType.APPLICATION_JSON);
        } else {
            requestHeadersSpec = uri.body(BodyInserters.fromValue(requestParamInfo.getRequestBody()))
                    .accept(MediaType.APPLICATION_JSON);
        }
        if (requestParamInfo.isReturnFlux()) {
            return requestHeadersSpec.exchangeToFlux(clientResponse -> {
                return clientResponse.bodyToFlux(ParameterizedTypeReference.forType(requestParamInfo.getResultBody()));
            });
        } else {
            return requestHeadersSpec.exchangeToMono(clientResponse -> {
                var objectParameterizedTypeReference = ParameterizedTypeReference.forType(requestParamInfo.getResultBody());
                return clientResponse.bodyToMono(objectParameterizedTypeReference);
            });
        }
    }


    public <T> Mono<T> getMono(String path, Map<String, String> requestParam, ParameterizedTypeReference<T> responseClass) {

        return GetResponseSpec(path, requestParam)
                .bodyToMono(responseClass);
    }

    public <T> Flux<T> getFlux(String path, Map<String, String> requestParam, Class<T> responseClass) {
        return GetResponseSpec(path, requestParam)
                .bodyToFlux(responseClass);
    }

    public <T> Mono<T> postMono(String path, Map<String, String> requestParam, Class<T> responseClass) {
        return postMono(path, requestParam, null, responseClass);
    }

    public <T> Flux<T> postFlux(String path, Map<String, String> requestParam, Object requestBody, Class<T> responseClass) {
        return PostResponseSpec(path, requestParam, requestBody)
                .bodyToFlux(responseClass);
    }

    public <T> Mono<T> postMono(String path, Map<String, String> requestParam, Object requestBody, Class<T> responseClass) {
        return PostResponseSpec(path, requestParam, requestBody)
                .bodyToMono(responseClass);
    }

    static URL parseUrl(String urlStr) {

        URL url;
        try {
            url = new URL(urlStr);
            if (url.getProtocol() == null) {
                url = new URL("http://localhost:80");
            }
            return url;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public WebClient.ResponseSpec PostResponseSpec(String path, Map<String, String> requestParam, Object body) {
        if (null == body) {
            return getWebClientByBaseUrl()
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path(path)
                            .queryParams(getRequestParamMap(requestParam)).build())
                    .retrieve();
        }
        return getWebClientByBaseUrl()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParams(getRequestParamMap(requestParam)).build())
                .body(BodyInserters.fromValue(body))
                .retrieve();
    }

    public WebClient.ResponseSpec GetResponseSpec(String path, Map<String, String> requestParam) {
        return getWebClientByBaseUrl()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParams(getRequestParamMap(requestParam)).build())
                .retrieve();
    }

    private MultiValueMap<String, String> getRequestParamMap(Map<String, String> params) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            queryParams.add(entry.getKey(), entry.getValue());
        }

        return queryParams;
    }

    private MultiValueMap<String, String> getRequestParamMapByObj(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.convertValue(obj, new TypeReference<Map<String, Object>>() {
        });
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (Objects.isNull(entry.getValue())) {
                continue;
            }
            queryParams.add(entry.getKey(), String.valueOf(entry.getValue()));
        }

        return queryParams;
    }


    /**
     * post json请求结果解析成对象
     *
     * @param url             url
     * @param requestJsonBody 请求body，可以是对象或者是map
     * @param responseClass   解析对象
     * @return T
     * @author admin
     * @since 2019/10/30
     */
    private <T> Mono<T> postJson(String url, Object requestJsonBody, Class<T> responseClass) {
        return getWebClientByBaseUrl()
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestJsonBody), Object.class)
                .retrieve().bodyToMono(responseClass);
    }

    /**
     * get请求，解析成对象
     *
     * @param scheme       协议 http/https
     * @param host         host
     * @param requestParam query params
     * @return T
     * @author admin
     * @since 2019/10/30
     */
    private <T> Mono<T> getMono(String scheme, String host, String path, Object requestParam, Class<T> responseClass) {
        return getWebClientByBaseUrl()
                .get()
                .uri(uriBuilder -> uriBuilder.scheme(scheme).host(host).path(path).queryParams(getRequestParamMapByObj(requestParam)).build())
                .retrieve()
                .bodyToMono(responseClass);
    }

    public String getBaseUrl() {
        return baseUrl;
    }


    public WebClient.Builder getWebClientBuilder() {
        return webClientBuilder;
    }

    public void setWebClientBuilder(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }


}
