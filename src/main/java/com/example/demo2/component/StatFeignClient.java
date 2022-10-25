package com.example.demo2.component;

import feign.Client;
import feign.Request;
import feign.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class StatFeignClient implements Client {

    private final Client delegate;

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        long start = System.currentTimeMillis();
        Response response = null;
        try {
            response = delegate.execute(request, options);
        } finally {
//            log.info("url: {}, duration: {}", request.url(), System.currentTimeMillis() - start);
        }
        return response;
    }
}
