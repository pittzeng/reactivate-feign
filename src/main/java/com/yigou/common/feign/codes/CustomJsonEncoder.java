package com.yigou.common.feign.codes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.AbstractJackson2Encoder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.SimpleTimeZone;


public class CustomJsonEncoder extends AbstractJackson2Encoder {

    static final ObjectMapper objectMapper= new ObjectMapper()
            .findAndRegisterModules()
            // 时区序列化为 +08:00 形式
            .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false)
            // 日期、时间序列化为字符串
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            // 持续时间序列化为字符串
            .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
            // 当出现 Java 类中未知的属性时不报错，而是忽略此 JSON 字段
            .configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false)
            // 枚举类型调用 `toString` 方法进行序列化
            .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            // 设置 java.util.Date 类型序列化格式
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            // 设置 Jackson 使用的时区
            .setTimeZone(SimpleTimeZone.getTimeZone("GMT+8"));
    public CustomJsonEncoder() {
        super(objectMapper, MediaType.APPLICATION_JSON,MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    @NonNull
    public Flux<DataBuffer> encode(@NonNull Publisher<?> inputStream, @NonNull DataBufferFactory bufferFactory, @NonNull ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return super.encode(inputStream, bufferFactory, elementType, mimeType, hints);
    }


}
