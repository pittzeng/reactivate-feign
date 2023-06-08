/*
package com.yigou.common.feign.codes;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Encoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonEncoder implements Encoder<Object> {
    @Override
    public boolean canEncode(@NonNull ResolvableType elementType, MimeType mimeType) {
        return true;
    }


    @NonNull
    @Override
    public Flux<DataBuffer> encode(@NonNull Publisher<?> inputStream, @NonNull DataBufferFactory bufferFactory, @NonNull ResolvableType elementType,
                                   MimeType mimeType, Map<String, Object> hints) {
        if (inputStream instanceof Mono) {
            return Mono.from(inputStream)
                    .map(value -> encodeValue(value, bufferFactory))
                    .flux();
        } else {
           return Flux.from(inputStream).map(value -> encodeValue(value, bufferFactory));
        }
    }

    @NonNull
    @Override
    public List<MimeType> getEncodableMimeTypes() {
        return Collections.singletonList(MimeTypeUtils.APPLICATION_JSON);
    }


    */
/**
     * 处理数据
     *//*

    private DataBuffer encodeValue(Object value, DataBufferFactory bufferFactory) {
        byte[] bytes = JSON.toJSONBytes(value, JSONWriter.Feature.WriteMapNullValue, JSONWriter.Feature.WriteNullStringAsEmpty);
        DataBuffer buffer = bufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }
}
*/
