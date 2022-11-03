package com.example.demo2.component;

import com.example.demo2.service.HrWorkUnitService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class One implements ApplicationContextAware, InitializingBean {


    private ApplicationContext applicationContext;
    private One self;
    @Resource(name = "asyncPool")
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private HrWorkUnitService hrWorkUnitService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        self = applicationContext.getBean(One.class);
    }

    public void test() {
        self.test2();
    }

    private void test2() {
        hrWorkUnitService.findParentToRootDid(60000004L, Lists.newArrayList("1"));
    }
}
