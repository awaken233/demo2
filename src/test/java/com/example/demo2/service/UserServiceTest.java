package com.example.demo2.service;

import com.example.demo2.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void save() {
        userService.save(new User());
    }

    @Test
    public void sendEmail() {
        userService.sendEmail(new User());
    }

    @Test
    public void register() {
        userService.register(new User());
    }
}
