package com.example.demo2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.NamedThreadLocal;

import java.io.Serializable;

/**
 * @author wlei3
 * @since 2022/8/14 18:00
 */
@Data
@AllArgsConstructor
public class WebUser implements Serializable {

    private Long cid;

    private static final ThreadLocal<WebUser> userHolder =
        new NamedThreadLocal<>("WebUser");


    public static WebUser getCurrentUser() {
        return userHolder.get();
    }

    public static void setCurrentUser(WebUser webUser) {
        userHolder.set(webUser);
    }

    public static void resetWebUser() {
        userHolder.remove();
    }

}
