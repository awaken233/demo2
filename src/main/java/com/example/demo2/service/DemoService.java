package com.example.demo2.service;

import com.example.demo2.Base;
import org.springframework.stereotype.Service;

@Service
public class DemoService implements IDemoService{

    public Base test() {
        return new Base();
    }

}
