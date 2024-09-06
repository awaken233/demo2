package com.example.demo2.entity;

import lombok.Data;

/**
 * @author wanglei
 * @since 2024/9/7 00:08
 */
@Data
public class R<T> {
    private Integer code;
    private String msg;
    private T data;

    public R(T data) {
        this.data = data;
        this.code = 200;
        this.msg = "success";
    }

    public static <T> R<T> ok(T data) {
        return new R<>(data);
    }
}
