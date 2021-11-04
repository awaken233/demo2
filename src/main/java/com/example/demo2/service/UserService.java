package com.example.demo2.service;

import com.example.demo2.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public User save(User user) {
        return new User();
    }

    public void sendEmail(User user) {
        return ;
    }

    public void register(User user) {
        this.save(user);
        this.sendEmail(user);
    }
}
