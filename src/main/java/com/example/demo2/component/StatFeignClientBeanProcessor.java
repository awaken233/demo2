package com.example.demo2.component;

import feign.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
public class StatFeignClientBeanProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException {
        if (bean instanceof Client) {
            log.info("StatFeignClientBeanProcessor bean name : {} , original bean type : {} ",
                beanName, bean.getClass().getSimpleName());
            return new StatFeignClient((Client) bean);
        }
        return bean;
    }
}