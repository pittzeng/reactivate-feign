package com.yigou.common.feign.exception;

import java.util.Date;

public class RetryException extends FeignException{
    private String requestUrl;
    private Date retryDate;
    public RetryException(int status, String message, String requestUrl,Date retryDate) {
        super(status, message);
        this.requestUrl=requestUrl;
        this.retryDate=retryDate;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Date getRetryDate() {
        return retryDate;
    }

    public void setRetryDate(Date retryDate) {
        this.retryDate = retryDate;
    }
}
