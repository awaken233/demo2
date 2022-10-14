package com.example.demo2.typeTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthMessageHandler implements MessageHandler<AuthRequest> {

    @Override
    public void execute(AuthRequest msg) {
        log.info("AuthMessageHandler");
    }
}
