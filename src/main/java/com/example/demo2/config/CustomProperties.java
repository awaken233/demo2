package com.example.demo2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wlei3
 * @since 2022/9/8 10:01
 */
@Data
@Component
@ConfigurationProperties(prefix = "custom")
public class CustomProperties {

    private String cookie;
}
