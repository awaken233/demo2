package com.example.demo2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * AWS S3 配置属性类
 */
@Data
@Component
@ConfigurationProperties(prefix = "aws.s3")
public class S3Properties {
    
    /**
     * AWS Access Key
     */
    private String accessKey;
    
    /**
     * AWS Secret Key
     */
    private String secretKey;
    
    /**
     * AWS Region
     */
    private String region = "us-east-1";
    
    /**
     * S3 Bucket名称
     */
    private String bucket;
    
    /**
     * 自定义端点 (可选，用于MinIO等)
     */
    private String endpoint;
    
    /**
     * 上传配置
     */
    private Upload upload = new Upload();
    
    /**
     * 下载配置
     */
    private Download download = new Download();
    
    /**
     * 客户端配置
     */
    private Client client = new Client();
    
    @Data
    public static class Upload {
        /**
         * 最大文件大小
         */
        private String maxFileSize = "100MB";
        
        /**
         * 允许的文件类型
         */
        private String allowedTypes = "jpg,jpeg,png,gif,pdf,txt,doc,docx";
        
        public List<String> getAllowedTypesList() {
            return Arrays.asList(allowedTypes.split(","));
        }
    }
    
    @Data
    public static class Download {
        /**
         * 预签名URL过期时间
         */
        private Duration urlExpiration = Duration.ofHours(1);
    }
    
    @Data
    public static class Client {
        /**
         * 重试配置
         */
        private Retry retry = new Retry();
        
        @Data
        public static class Retry {
            /**
             * 最大重试次数
             */
            private int maxAttempts = 3;
            
            /**
             * 退避延迟 (毫秒)
             */
            private long backoffDelay = 1000;
        }
    }
} 