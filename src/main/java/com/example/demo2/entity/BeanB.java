package com.example.demo2.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wlei3
 * @since 2021/11/26 18:47
 */
@Component
public class BeanB {

    @Autowired
    private BeanA beanA;
}
