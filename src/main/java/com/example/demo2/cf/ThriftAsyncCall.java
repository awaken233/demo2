package com.example.demo2.cf;

@FunctionalInterface
public interface ThriftAsyncCall {

    void invoke() throws TException;
}