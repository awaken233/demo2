package com.example.demo2.component;

import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        // 根据response的状态码决定返回什么样的异常，
        // 例如这里简单地把所有异常都转换成了RuntimeException，实际使用时可能需要更细致的处理。
        return new RuntimeException("Feign client error, status: " + response.status());
    }
}
