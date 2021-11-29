package com.example.demo2;

import com.example.demo2.entity.BeanB;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author wlei3
 * @since 2021/11/29 16:40
 */
public class GetAnnotationBeanTest {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("com.example.demo2.entity");
        context.refresh();
        BeanB bean = context.getBean(BeanB.class);
        System.out.println(bean);
    }
}
