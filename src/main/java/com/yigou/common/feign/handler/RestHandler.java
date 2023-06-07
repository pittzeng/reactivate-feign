package com.yigou.common.feign.handler;

import com.yigou.common.feign.bean.RequestParamInfo;
import org.reactivestreams.Publisher;

public interface RestHandler {

    Publisher<?> invoke(RequestParamInfo requestParamInfo);
}
