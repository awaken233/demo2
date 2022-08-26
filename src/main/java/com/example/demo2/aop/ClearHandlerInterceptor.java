package com.example.demo2.aop;

import cn.hutool.core.util.IdUtil;
import com.example.demo2.dto.WebUser;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class ClearHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        MDC.put("requestId", IdUtil.objectId());
        WebUser.setCurrentUser(new WebUser(1L));
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) throws Exception {
        WebUser.resetWebUser();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
