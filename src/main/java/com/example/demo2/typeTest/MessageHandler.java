package com.example.demo2.typeTest;

/**
 * @author wlei3
 * @since 2022/10/13 20:30
 */
public interface MessageHandler<T extends Message> {

    void execute(T msg);
}
