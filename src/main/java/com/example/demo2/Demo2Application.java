package com.example.demo2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wlei3
 */
@SpringBootApplication
public class Demo2Application {

//    public static void main(String[] args) {
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//        context.scan("com.example.demo2.entity");
//        context.refresh();
//        BeanB bean = context.getBean(BeanB.class);
//        System.out.println(bean);
//    }

    public static void main(String[] args) {
        SpringApplication.run(Demo2Application.class, args);
    }

}
