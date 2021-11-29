package com.example.demo2.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wlei3
 * @since 2021/11/26 18:46
 */
@Component
public class BeanA {

    @Autowired
    private BeanB beanB;
}
