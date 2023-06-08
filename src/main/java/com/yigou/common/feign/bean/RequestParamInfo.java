package com.yigou.common.feign.bean;

import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

public class RequestParamInfo {
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;
    private String requestPath;
    private HttpMethod requestMethod;
    private MultiValueMap<String,String> requestParam;
    private Object requestBody;
    private boolean isReturnFlux;
    private Object resultBody;

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public HttpMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(HttpMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public MultiValueMap<String, String> getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(MultiValueMap<String, String> requestParam) {
        this.requestParam = requestParam;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    public boolean isReturnFlux() {
        return isReturnFlux;
    }

    public void setReturnFlux(boolean returnFlux) {
        isReturnFlux = returnFlux;
    }

    public  Object getResultBody() {
        return resultBody;
    }

    public void setResultBody(Object resultBody) {
        this.resultBody = resultBody;
    }
}
