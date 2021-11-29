package com.example.demo2;

import com.example.demo2.entity.BeanB;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author wlei3
 */
@SpringBootApplication
public class Demo2Application {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("com.example.demo2.entity");
        context.refresh();
        BeanB bean = context.getBean(BeanB.class);
        System.out.println(bean);
    }

}
