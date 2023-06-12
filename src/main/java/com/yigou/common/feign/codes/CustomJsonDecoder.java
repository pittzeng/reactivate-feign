package com.yigou.common.feign.codes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.AbstractJackson2Decoder;
import org.springframework.util.MimeType;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.SimpleTimeZone;


public class CustomJsonDecoder extends AbstractJackson2Decoder {

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

    public CustomJsonDecoder() {
        super(objectMapper, MediaType.APPLICATION_JSON,MediaType.APPLICATION_OCTET_STREAM);
    }


    @Override
    public Object decode(DataBuffer dataBuffer, ResolvableType targetType, MimeType mimeType, Map<String, Object> hints) throws DecodingException {
        return super.decode(dataBuffer,targetType,mimeType,hints);
       /* Object read = null;
        boolean flag = false;
        try {

            read = new FastJsonHttpMessageConverter().read(targetType.getType(), null, new CustomHttpInputMessage(dataBuffer.asInputStream()));
            flag = true;
               *//* //如果是json格式 以及是否是自己的业务包
                if (mimeType.toString().contains(MediaType.APPLICATION_JSON_VALUE)) {
                    read = fastJsonHttpMessageConverter.read(targetType.getType(), null, new CustomHttpInputMessage(dataBuffer.asInputStream()));
                } else {

                    //不是想转换的数据直接返回父类调用
                    return super.decode(dataBuffer, targetType, mimeType, hints);
                }
*//*
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (flag) {
                DataBufferUtils.release(dataBuffer);
            }
        }
        return read;*/
    }

/*
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
    }*/
}
