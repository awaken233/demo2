package com.example.demo2.enums;

public enum ComputerState implements BaseCodeEnum {
    OPEN(10),         //开启
    CLOSE(11),         //关闭
    OFF_LINE(12),     //离线
    FAULT(200),     //故障
    UNKNOWN(255);     //未知

    private int code;

    ComputerState(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        // 1 2 3 4 5 6 7 8
        return this.code;
    }
}
