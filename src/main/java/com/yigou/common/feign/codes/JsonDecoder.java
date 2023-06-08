/*
package com.yigou.common.feign.codes;

import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonDecoder implements Decoder<Object> {
    @Override
    public boolean canDecode(@NonNull ResolvableType elementType, MimeType mimeType) {
        return true;
    }
    @NonNull
    @Override
    public Flux<Object> decode(@NonNull Publisher<DataBuffer> inputStream, @NonNull ResolvableType elementType, MimeType mimeType, Map<String, Object> hints) {
        return null;
    }

    @NonNull
    @Override
    public Mono<Object> decodeToMono(@NonNull Publisher<DataBuffer> inputStream, @NonNull ResolvableType elementType, MimeType mimeType, Map<String, Object> hints) {
        if (inputStream instanceof Mono){
            return Mono.from(inputStream).flatMap(strea->{
                new FastJsonHttpMessageConverter().read(, null, new CustomHttpInputMessage(dataBuffer.asInputStream()));
            })
        }
    }

    @NonNull
    @Override
    public List<MimeType> getDecodableMimeTypes() {
        return Collections.singletonList(MediaType.APPLICATION_JSON);
    }


    public static class CustomHttpInputMessage implements HttpInputMessage {
        private final InputStream inputStream;

        public CustomHttpInputMessage(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @NonNull
        @Override
        public InputStream getBody() throws IOException {
            return inputStream;
        }

        @NonNull
        @Override
        public HttpHeaders getHeaders() {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            return httpHeaders;
        }
    }
}
*/
