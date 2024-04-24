package com.example.demo2.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "bblg.config")
public class BblgProperties {
    private String appId;
    private String publicKey;
    private String privateKey;
}