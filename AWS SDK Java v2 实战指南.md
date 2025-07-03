---
CREATE_TIME: 2025-01-20 16:30:00
UPDATE_TIME: 2025-01-20 16:30:00
tags:
  - aws
  - sdk
  - java
  - s3
  - tutorial
  - hands-on
---

# AWS SDK Java v2 实战指南

## 概述

- 掌握AWS SDK for Java v2的基础配置和使用方法
- 深入学习S3对象存储服务(Simple Storage Service)的常见操作
- 理解异步编程模式和响应式设计在SDK中的应用
- 探索最佳实践和性能优化技巧
- 为深入学习[[AWS SDK源码设计学习笔记-完整版]]打下实战基础

本指南通过实际代码示例，带您快速掌握AWS SDK for Java v2的使用方法，重点学习S3对象存储服务的操作，为后续深入学习源码中的优秀设计模式做好准备。

## 环境准备

### 1. 依赖配置

在 `pom.xml` 中添加必要的依赖：

```xml
<properties>
    <java.version>1.8</java.version>
    <aws.sdk.version>2.21.29</aws.sdk.version>
</properties>

<!-- 添加依赖管理，使用AWS SDK BOM -->
<dependencyManagement>
    <dependencies>
        <!-- AWS SDK BOM for version management -->
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>bom</artifactId>
            <version>${aws.sdk.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>

    <!-- AWS SDK S3 (版本由BOM管理) -->
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>s3</artifactId>
    </dependency>

    <!-- AWS SDK Netty HTTP Client (版本由BOM管理) -->
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>netty-nio-client</artifactId>
    </dependency>

    <!-- AWS SDK URL Connection HTTP Client (备用，版本由BOM管理) -->
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>url-connection-client</artifactId>
    </dependency>
</dependencies>
```

### 2. Spring Boot 项目中的配置

#### 添加Spring Boot依赖
```xml
<!-- Spring Boot Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<!-- Spring Boot Web (如果需要Web功能) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Boot 配置处理器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

#### application.yml 配置
```yaml
server:
  port: 8080

spring:
  application:
    name: aws-s3-demo

aws:
  s3:
    access-key: ${AWS_ACCESS_KEY:your-access-key}
    secret-key: ${AWS_SECRET_KEY:your-secret-key}
    region: ${AWS_REGION:us-east-1}
    bucket: ${AWS_S3_BUCKET:demo-bucket}
    endpoint: ${AWS_S3_ENDPOINT:}  # 可选，用于MinIO等
    upload:
      max-file-size: 100MB
      allowed-types: jpg,jpeg,png,gif,pdf,txt,doc,docx
    download:
      url-expiration: PT1H  # 1小时
    client:
      retry:
        max-attempts: 3
        backoff-delay: 1000

logging:
  level:
    com.example.demo2: DEBUG
    software.amazon.awssdk: INFO
```

#### 配置属性类
```java
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
```

#### Spring Configuration 类
```java
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.time.Duration;

/**
 * AWS S3 配置类
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AwsS3Config {
    
    private final S3Properties s3Properties;
    
    /**
     * 创建 S3Client Bean
     */
    @Bean
    public S3Client s3Client() {
        log.info("初始化 S3Client, region: {}, bucket: {}", 
                s3Properties.getRegion(), s3Properties.getBucket());
        
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                s3Properties.getAccessKey(), 
                s3Properties.getSecretKey());
        
        S3ClientBuilder builder = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.of(s3Properties.getRegion()));
        
        // 如果配置了自定义端点（如MinIO）
        if (s3Properties.getEndpoint() != null && !s3Properties.getEndpoint().isEmpty()) {
            builder.endpointOverride(URI.create(s3Properties.getEndpoint()))
                   .forcePathStyle(true);
            log.info("使用自定义端点: {}", s3Properties.getEndpoint());
        }
        
        return builder.build();
    }
    
    /**
     * 创建 S3AsyncClient Bean
     */
    @Bean
    public S3AsyncClient s3AsyncClient() {
        log.info("初始化 S3AsyncClient");
        
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                s3Properties.getAccessKey(), 
                s3Properties.getSecretKey());
        
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(30))
                .maxConcurrency(100)
                .build();
        
        S3AsyncClientBuilder builder = S3AsyncClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.of(s3Properties.getRegion()))
                .httpClient(httpClient);
        
        // 如果配置了自定义端点（如MinIO）
        if (s3Properties.getEndpoint() != null && !s3Properties.getEndpoint().isEmpty()) {
            builder.endpointOverride(URI.create(s3Properties.getEndpoint()))
                   .forcePathStyle(true);
        }
        
        return builder.build();
    }
    
    /**
     * 创建 S3Presigner Bean
     */
    @Bean
    public S3Presigner s3Presigner() {
        log.info("初始化 S3Presigner");
        
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                s3Properties.getAccessKey(), 
                s3Properties.getSecretKey());
        
        S3Presigner.Builder builder = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.of(s3Properties.getRegion()));
        
        // 如果配置了自定义端点（如MinIO）
        if (s3Properties.getEndpoint() != null && !s3Properties.getEndpoint().isEmpty()) {
            builder.endpointOverride(URI.create(s3Properties.getEndpoint()));
        }
        
        return builder.build();
    }
}

## Spring Boot 服务层实现

### 1. S3 服务类

```java
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * S3 服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;
    
    // 主要业务方法将在下面详细展示
}
```

### 2. REST 控制器

```java
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * S3 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {
    
    private final S3Service s3Service;
    
    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "文件不能为空");
                return ResponseEntity.badRequest().body(result);
            }
            
            String key = s3Service.uploadFile(file);
            
            result.put("success", true);
            result.put("message", "文件上传成功");
            result.put("key", key);
            result.put("originalFilename", file.getOriginalFilename());
            result.put("size", file.getSize());
            result.put("contentType", file.getContentType());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("文件上传失败", e);
            result.put("success", false);
            result.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 生成下载链接
     */
    @GetMapping("/download-url/{key}")
    public ResponseEntity<Map<String, Object>> generateDownloadUrl(@PathVariable String key) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String url = s3Service.generateDownloadUrl(key);
            
            result.put("success", true);
            result.put("key", key);
            result.put("downloadUrl", url);
            result.put("message", "下载链接生成成功");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("生成下载链接失败", e);
            result.put("success", false);
            result.put("message", "生成下载链接失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 列出文件
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listFiles(
            @RequestParam(defaultValue = "") String prefix) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<S3Object> objects = s3Service.listObjects(prefix);
            
            List<Map<String, Object>> fileList = objects.stream()
                    .map(obj -> {
                        Map<String, Object> fileInfo = new HashMap<>();
                        fileInfo.put("key", obj.key());
                        fileInfo.put("size", obj.size());
                        fileInfo.put("lastModified", obj.lastModified().toString());
                        fileInfo.put("etag", obj.eTag());
                        return fileInfo;
                    })
                    .collect(Collectors.toList());
            
            result.put("success", true);
            result.put("files", fileList);
            result.put("count", fileList.size());
            result.put("prefix", prefix);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("列出文件失败", e);
            result.put("success", false);
            result.put("message", "列出文件失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}
```

## S3 核心操作实战

### 1. 存储桶(Bucket)管理

#### 创建存储桶
```java
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.*;

@Slf4j
public class S3Service {
    
    public void createBucket(String bucketName) {
        try {
            // 检查存储桶是否存在
            if (!bucketExists(bucketName)) {
                CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .createBucketConfiguration(CreateBucketConfiguration.builder()
                        .locationConstraint(BucketLocationConstraint.US_WEST_2)
                        .build())
                    .build();
                    
                CreateBucketResponse response = s3Client.createBucket(createBucketRequest);
                log.info("存储桶创建成功: {}", response.location());
            }
        } catch (BucketAlreadyExistsException e) {
            log.warn("存储桶已存在: {}", bucketName);
            throw new RuntimeException("存储桶已存在", e);
        } catch (S3Exception e) {
            log.error("创建存储桶失败: {}", e.getMessage());
            throw new RuntimeException("创建存储桶失败", e);
        }
    }

    private boolean bucketExists(String bucketName) {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                .bucket(bucketName)
                .build();
            s3Client.headBucket(headBucketRequest);
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }
```

#### 列出存储桶
```java
public void listBuckets() {
    try {
        ListBucketsResponse response = s3Client.listBuckets();
        
        System.out.println("存储桶列表:");
        response.buckets().forEach(bucket -> {
            System.out.printf("- %s (创建时间: %s)%n", 
                bucket.name(), 
                bucket.creationDate().toString());
        });
        
    } catch (S3Exception e) {
        System.err.println("获取存储桶列表失败: " + e.getMessage());
    }
}
```

#### 删除存储桶
```java
public void deleteBucket(String bucketName) {
    try {
        // 首先清空存储桶
        deleteAllObjects(bucketName);
        
        // 然后删除存储桶
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
            .bucket(bucketName)
            .build();
            
        s3Client.deleteBucket(deleteBucketRequest);
        System.out.println("存储桶删除成功: " + bucketName);
        
    } catch (S3Exception e) {
        System.err.println("删除存储桶失败: " + e.getMessage());
    }
}
```

### 2. 对象(Object)操作

#### 上传对象
```java
import software.amazon.awssdk.core.sync.RequestBody;
import org.springframework.web.multipart.MultipartFile;

// 上传Spring MultipartFile
public String uploadFile(MultipartFile file) throws IOException {
    return uploadFile(file, null);
}

public String uploadFile(MultipartFile file, String customKey) throws IOException {
    String key = customKey != null ? customKey : generateFileKey(file.getOriginalFilename());
    String bucketName = s3Properties.getBucket();
    
    try {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(file.getContentType())
            .contentLength(file.getSize())
            .metadata(createMetadata(
                "original-filename", file.getOriginalFilename(),
                "upload-time", Instant.now().toString(),
                "file-size", String.valueOf(file.getSize())
            ))
            .build();
            
        PutObjectResponse response = s3Client.putObject(
            putObjectRequest,
            RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );
        
        log.info("文件上传成功: {} -> {}, ETag: {}", file.getOriginalFilename(), key, response.eTag());
        return key;
        
    } catch (S3Exception e) {
        log.error("上传文件失败: {}", e.getMessage());
        throw new RuntimeException("文件上传失败", e);
    }
}

// 上传字符串内容
public String uploadString(String key, String content) {
    String bucketName = s3Properties.getBucket();
    
    try {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType("text/plain; charset=utf-8")
            .contentLength((long) content.getBytes(StandardCharsets.UTF_8).length)
            .metadata(createMetadata(
                "content-type", "text",
                "upload-time", Instant.now().toString()
            ))
            .build();
            
        PutObjectResponse response = s3Client.putObject(
            putObjectRequest,
            RequestBody.fromString(content)
        );
        
        log.info("字符串内容上传成功, Key: {}, ETag: {}", key, response.eTag());
        return key;
        
    } catch (S3Exception e) {
        log.error("上传字符串内容失败: {}", e.getMessage());
        throw new RuntimeException("上传失败", e);
    }
}

// 生成文件Key
private String generateFileKey(String originalFilename) {
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String uuid = UUID.randomUUID().toString().replaceAll("-", "");
    String extension = getFileExtension(originalFilename);
    
    return String.format("uploads/%s/%s%s", timestamp, uuid, extension);
}

private String getFileExtension(String filename) {
    if (filename == null || filename.isEmpty()) {
        return "";
    }
    int lastDotIndex = filename.lastIndexOf('.');
    return lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
}

/**
 * 创建元数据Map (Java 8兼容)
 */
private Map<String, String> createMetadata(String... keyValuePairs) {
    Map<String, String> metadata = new HashMap<>();
    for (int i = 0; i < keyValuePairs.length; i += 2) {
        if (i + 1 < keyValuePairs.length) {
            metadata.put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
    }
    return metadata;
}

/**
 * 读取InputStream所有字节 (Java 8兼容)
 */
private byte[] readAllBytes(ResponseInputStream<GetObjectResponse> inputStream) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    byte[] data = new byte[4096];
    int nRead;
    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
    }
    buffer.flush();
    return buffer.toByteArray();
}
```

#### 下载对象
```java
import software.amazon.awssdk.core.ResponseInputStream;

// 下载为字符串
public String downloadAsString(String key) {
    String bucketName = s3Properties.getBucket();
    try {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
            
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);
        
        // 读取内容
        String content = new String(readAllBytes(response), StandardCharsets.UTF_8);
        
        // 打印元数据
        GetObjectResponse metadata = response.response();
        System.out.println("Content-Type: " + metadata.contentType());
        System.out.println("Content-Length: " + metadata.contentLength());
        
        return content;
        
    } catch (S3Exception | IOException e) {
        System.err.println("下载对象失败: " + e.getMessage());
        return null;
    }
}

// 下载为字节数组
public byte[] downloadAsBytes(String key) {
    String bucketName = s3Properties.getBucket();
    try {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
            
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);
        
        byte[] content = readAllBytes(response);
        log.info("文件下载成功: {}, 大小: {} bytes", key, content.length);
        
        return content;
        
    } catch (S3Exception | IOException e) {
        log.error("下载对象失败: {}", e.getMessage());
        throw new RuntimeException("下载文件失败", e);
    }
}
```

#### 列出对象
```java
public List<S3Object> listObjects(String prefix) {
    String bucketName = s3Properties.getBucket();
    try {
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
            .bucket(bucketName)
            .prefix(prefix)
            .maxKeys(10) // 每页最多10个对象
            .build();
            
        ListObjectsV2Response response = s3Client.listObjectsV2(listObjectsRequest);
        
        List<S3Object> objects = response.contents();
        log.info("列出对象成功: {} 个", objects.size());
        
        return objects;
        
    } catch (S3Exception e) {
        log.error("列出对象失败: {}", e.getMessage());
        throw new RuntimeException("列出对象失败", e);
    }
}

private void listObjectsWithToken(String bucketName, String prefix, String continuationToken) {
    ListObjectsV2Request request = ListObjectsV2Request.builder()
        .bucket(bucketName)
        .prefix(prefix)
        .continuationToken(continuationToken)
        .build();
        
    // 处理下一页...
}
```

#### 删除对象
```java
// 删除单个对象
public void deleteObject(String key) {
    String bucketName = s3Properties.getBucket();
    try {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
            
        s3Client.deleteObject(deleteObjectRequest);
        log.info("对象删除成功: {}", key);
        
    } catch (S3Exception e) {
        log.error("删除对象失败: {}", e.getMessage());
        throw new RuntimeException("删除对象失败", e);
    }
}

// 批量删除对象
public void deleteObjects(List<String> keys) {
    String bucketName = s3Properties.getBucket();
    try {
        List<ObjectIdentifier> objectsToDelete = keys.stream()
            .map(key -> ObjectIdentifier.builder().key(key).build())
            .collect(Collectors.toList());
            
        Delete delete = Delete.builder()
            .objects(objectsToDelete)
            .quiet(false) // 返回删除结果
            .build();
            
        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
            .bucket(bucketName)
            .delete(delete)
            .build();
            
        DeleteObjectsResponse response = s3Client.deleteObjects(deleteObjectsRequest);
        
        log.info("成功删除 {} 个对象", response.deleted().size());
        response.errors().forEach(error -> 
                log.error("删除失败: {} - {}", error.key(), error.message()));
            
    } catch (S3Exception e) {
        log.error("批量删除失败: {}", e.getMessage());
        throw new RuntimeException("批量删除失败", e);
    }
}
```

## 异步编程实战

### 1. 异步上传操作

```java
import java.util.concurrent.CompletableFuture;

public class S3AsyncOperations {
    
    private final S3AsyncClient s3AsyncClient;
    
    public S3AsyncOperations() {
        this.s3AsyncClient = S3AsyncClient.builder()
            .region(Region.US_EAST_1)
            .build();
    }
    
    // 异步上传
    public CompletableFuture<String> putObjectAsync(String bucketName, String key, String content) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType("text/plain")
            .build();
            
        return s3AsyncClient.putObject(putObjectRequest, AsyncRequestBody.fromString(content))
            .thenApply(response -> {
                System.out.println("异步上传成功, ETag: " + response.eTag());
                return response.eTag();
            })
            .exceptionally(throwable -> {
                System.err.println("异步上传失败: " + throwable.getMessage());
                return null;
            });
    }
    
    // 异步下载
    public CompletableFuture<String> getObjectAsync(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
            
        return s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toBytes())
            .thenApply(response -> {
                String content = response.asByteArray().asString(StandardCharsets.UTF_8);
                System.out.println("异步下载成功, 内容长度: " + content.length());
                return content;
            })
            .exceptionally(throwable -> {
                System.err.println("异步下载失败: " + throwable.getMessage());
                return null;
            });
    }
    
    // 并发操作示例
    public CompletableFuture<Void> uploadMultipleFilesAsync(String bucketName, Map<String, String> files) {
        List<CompletableFuture<String>> uploadTasks = files.entrySet().stream()
            .map(entry -> putObjectAsync(bucketName, entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
            
        return CompletableFuture.allOf(uploadTasks.toArray(new CompletableFuture[0]))
            .thenRun(() -> System.out.println("所有文件上传完成"));
    }
}
```

### 2. 响应式编程与流处理

```java
import software.amazon.awssdk.core.async.AsyncResponseTransformer;

public class S3StreamOperations {
    
    // 流式下载大文件
    public CompletableFuture<Void> downloadToFileAsync(String bucketName, String key, Path targetPath) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
            
        return s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toFile(targetPath))
            .thenAccept(response -> {
                System.out.println("流式下载完成: " + targetPath);
                System.out.println("文件大小: " + response.response().contentLength());
            });
    }
    
    // 分段下载
    public CompletableFuture<String> downloadPartialContent(String bucketName, String key, long start, long end) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .range("bytes=" + start + "-" + end)
            .build();
            
        return s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toBytes())
            .thenApply(response -> {
                String content = response.asByteArray().asString(StandardCharsets.UTF_8);
                System.out.println("分段下载完成: " + start + "-" + end);
                return content;
            });
    }
}
```

## 高级特性实战

### 1. 预签名URL生成

```java
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

public class S3PresignedUrlExample {
    
    private final S3Presigner presigner;
    
    public S3PresignedUrlExample() {
        this.presigner = S3Presigner.builder()
            .region(Region.US_EAST_1)
            .build();
    }
    
    // 生成下载链接
    public String generateDownloadUrl(String key) {
        String bucketName = s3Properties.getBucket();
        
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
                
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(s3Properties.getDownload().getUrlExpiration())
                .getObjectRequest(getObjectRequest)
                .build();
                
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            
            String url = presignedRequest.url().toString();
            log.info("生成下载链接成功: {}, 有效期: {}", 
                    key, s3Properties.getDownload().getUrlExpiration());
            return url;
            
        } catch (S3Exception e) {
            log.error("生成下载链接失败: {}", e.getMessage());
            throw new RuntimeException("生成下载链接失败", e);
        }
    }
    
    // 生成上传链接
    public String generateUploadUrl(String bucketName, String key, Duration expiration) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType("application/octet-stream")
            .build();
            
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(expiration)
            .putObjectRequest(putObjectRequest)
            .build();
            
        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        
        System.out.println("上传链接有效期: " + expiration);
        return presignedRequest.url().toString();
    }
    
    public void close() {
        presigner.close();
    }
}
```

### 2. 多部分上传 (Multipart Upload)

```java
import software.amazon.awssdk.services.s3.model.*;

public class S3MultipartUpload {
    
    private final S3Client s3Client;
    private static final long PART_SIZE = 5 * 1024 * 1024; // 5MB
    
    public S3MultipartUpload(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    
    public void uploadLargeFile(String bucketName, String key, Path filePath) throws IOException {
        long fileSize = Files.size(filePath);
        
        if (fileSize < PART_SIZE) {
            // 小文件直接上传
            uploadSmallFile(bucketName, key, filePath);
            return;
        }
        
        // 初始化多部分上传
        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(Files.probeContentType(filePath))
            .build();
            
        CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(createRequest);
        String uploadId = createResponse.uploadId();
        
        List<CompletedPart> completedParts = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
            byte[] buffer = new byte[(int) PART_SIZE];
            int partNumber = 1;
            int bytesRead;
            
            while ((bytesRead = fis.read(buffer)) > 0) {
                // 上传分片
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .build();
                    
                UploadPartResponse uploadPartResponse = s3Client.uploadPart(
                    uploadPartRequest,
                    RequestBody.fromBytes(Arrays.copyOf(buffer, bytesRead))
                );
                
                completedParts.add(CompletedPart.builder()
                    .partNumber(partNumber)
                    .eTag(uploadPartResponse.eTag())
                    .build());
                    
                System.out.println("分片 " + partNumber + " 上传完成, ETag: " + uploadPartResponse.eTag());
                partNumber++;
            }
            
            // 完成多部分上传
            CompletedMultipartUpload completedUpload = CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();
                
            CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadId)
                .multipartUpload(completedUpload)
                .build();
                
            CompleteMultipartUploadResponse completeResponse = s3Client.completeMultipartUpload(completeRequest);
            System.out.println("多部分上传完成, ETag: " + completeResponse.eTag());
            
        } catch (Exception e) {
            // 失败时中止上传
            AbortMultipartUploadRequest abortRequest = AbortMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadId)
                .build();
                
            s3Client.abortMultipartUpload(abortRequest);
            System.err.println("多部分上传失败，已中止: " + e.getMessage());
            throw e;
        }
    }
    
    private void uploadSmallFile(String bucketName, String key, Path filePath) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
            
        s3Client.putObject(putObjectRequest, RequestBody.fromFile(filePath));
        System.out.println("小文件上传完成: " + key);
    }
}
```

### 3. 对象版本控制

```java
public class S3VersioningExample {
    
    private final S3Client s3Client;
    
    public S3VersioningExample(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    
    // 启用版本控制
    public void enableVersioning(String bucketName) {
        VersioningConfiguration versioningConfig = VersioningConfiguration.builder()
            .status(BucketVersioningStatus.ENABLED)
            .build();
            
        PutBucketVersioningRequest request = PutBucketVersioningRequest.builder()
            .bucket(bucketName)
            .versioningConfiguration(versioningConfig)
            .build();
            
        s3Client.putBucketVersioning(request);
        System.out.println("版本控制已启用: " + bucketName);
    }
    
    // 列出对象版本
    public void listObjectVersions(String bucketName, String prefix) {
        ListObjectVersionsRequest request = ListObjectVersionsRequest.builder()
            .bucket(bucketName)
            .prefix(prefix)
            .build();
            
        ListObjectVersionsResponse response = s3Client.listObjectVersions(request);
        
        System.out.println("对象版本列表:");
        response.versions().forEach(version -> {
            System.out.printf("- %s (版本ID: %s, 是否最新: %b, 大小: %d)%n",
                version.key(),
                version.versionId(),
                version.isLatest(),
                version.size());
        });
        
        // 删除标记
        response.deleteMarkers().forEach(marker -> {
            System.out.printf("- 删除标记: %s (版本ID: %s)%n",
                marker.key(),
                marker.versionId());
        });
    }
    
    // 获取特定版本
    public String getObjectVersion(String bucketName, String key, String versionId) {
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .versionId(versionId)
            .build();
            
        try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request)) {
            return new String(response.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("读取对象版本失败: " + e.getMessage());
            return null;
        }
    }
}
```

## 最佳实践与性能优化

### 1. 连接池配置

```java
import software.amazon.awssdk.http.apache.ApacheHttpClient;

public class S3ClientOptimization {
    
    public S3Client createOptimizedClient() {
        return S3Client.builder()
            .region(Region.US_EAST_1)
            .httpClientBuilder(ApacheHttpClient.builder()
                .maxConnections(100)              // 最大连接数
                .connectionTimeout(Duration.ofSeconds(30))  // 连接超时
                .socketTimeout(Duration.ofSeconds(60))      // Socket超时
                .connectionTimeToLive(Duration.ofMinutes(5)) // 连接生存时间
            )
            .overrideConfiguration(ClientOverrideConfiguration.builder()
                .retryPolicy(RetryPolicy.builder()
                    .numRetries(3)                 // 重试次数
                    .build())
                .apiCallTimeout(Duration.ofMinutes(2))      // API调用超时
                .apiCallAttemptTimeout(Duration.ofSeconds(30)) // 单次尝试超时
            )
            .build();
    }
}
```

### 2. 异常处理与重试策略

```java
public class S3ErrorHandling {
    
    private final S3Client s3Client;
    private final RetryTemplate retryTemplate;
    
    public S3ErrorHandling(S3Client s3Client) {
        this.s3Client = s3Client;
        this.retryTemplate = createRetryTemplate();
    }
    
    private RetryTemplate createRetryTemplate() {
        return RetryTemplate.builder()
            .maxAttempts(3)
            .exponentialBackoff(1000, 2, 10000)
            .retryOn(S3Exception.class)
            .build();
    }
    
    public String getObjectWithRetry(String bucketName, String key) {
        return retryTemplate.execute(context -> {
            try {
                GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
                    
                ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
                return new String(response.readAllBytes(), StandardCharsets.UTF_8);
                
            } catch (NoSuchKeyException e) {
                throw new RuntimeException("对象不存在: " + key, e);
            } catch (NoSuchBucketException e) {
                throw new RuntimeException("存储桶不存在: " + bucketName, e);
            } catch (S3Exception e) {
                if (e.statusCode() == 403) {
                    throw new RuntimeException("权限不足", e);
                } else if (e.statusCode() >= 500) {
                    // 服务器错误，可重试
                    throw e;
                } else {
                    // 客户端错误，不重试
                    throw new RuntimeException("客户端错误: " + e.getMessage(), e);
                }
            } catch (IOException e) {
                throw new RuntimeException("IO错误", e);
            }
        });
    }
}
```

### 3. 监控与指标

```java
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

public class S3Metrics {
    
    private final S3Client s3Client;
    private final MeterRegistry meterRegistry;
    
    public S3Metrics(S3Client s3Client, MeterRegistry meterRegistry) {
        this.s3Client = s3Client;
        this.meterRegistry = meterRegistry;
    }
    
    public String getObjectWithMetrics(String bucketName, String key) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
                
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
            String content = new String(response.readAllBytes(), StandardCharsets.UTF_8);
            
            // 记录成功指标
            meterRegistry.counter("s3.operation.success", 
                "operation", "getObject",
                "bucket", bucketName).increment();
                
            return content;
            
        } catch (Exception e) {
            // 记录失败指标
            meterRegistry.counter("s3.operation.error",
                "operation", "getObject",
                "bucket", bucketName,
                "error", e.getClass().getSimpleName()).increment();
            throw e;
            
        } finally {
            // 记录耗时
            sample.stop(Timer.builder("s3.operation.duration")
                .tag("operation", "getObject")
                .tag("bucket", bucketName)
                .register(meterRegistry));
        }
    }
}
```

## 实战项目示例

### 文件上传下载服务

```java
import org.springframework.stereotype.Service;

@Service
public class FileStorageService {
    
    private final S3AsyncClient s3AsyncClient;
    private final String bucketName;
    
    public FileStorageService(@Value("${aws.s3.bucket}") String bucketName) {
        this.bucketName = bucketName;
        this.s3AsyncClient = S3AsyncClient.builder()
            .region(Region.US_EAST_1)
            .build();
    }
    
    // 上传文件
    public CompletableFuture<String> uploadFile(String fileName, byte[] content, String contentType) {
        String key = generateFileKey(fileName);
        
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(contentType)
            .metadata(Map.of(
                "original-filename", fileName,
                "upload-time", Instant.now().toString()
            ))
            .build();
            
        return s3AsyncClient.putObject(request, AsyncRequestBody.fromBytes(content))
            .thenApply(response -> {
                log.info("文件上传成功: {} -> {}", fileName, key);
                return key;
            })
            .exceptionally(throwable -> {
                log.error("文件上传失败: " + fileName, throwable);
                throw new RuntimeException("文件上传失败", throwable);
            });
    }
    
    // 下载文件
    public CompletableFuture<byte[]> downloadFile(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
            
        return s3AsyncClient.getObject(request, AsyncResponseTransformer.toBytes())
            .thenApply(response -> {
                log.info("文件下载成功: {}", key);
                return response.asByteArray();
            })
            .exceptionally(throwable -> {
                log.error("文件下载失败: " + key, throwable);
                throw new RuntimeException("文件下载失败", throwable);
            });
    }
    
    // 生成下载链接
    public String generateDownloadUrl(String key, Duration expiration) {
        S3Presigner presigner = S3Presigner.builder()
            .region(Region.US_EAST_1)
            .build();
            
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
                
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(getObjectRequest)
                .build();
                
            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
            
        } finally {
            presigner.close();
        }
    }
    
    private String generateFileKey(String fileName) {
        String timestamp = Instant.now().toString().replaceAll("[^0-9]", "");
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String extension = getFileExtension(fileName);
        
        return String.format("uploads/%s/%s%s", timestamp.substring(0, 8), uuid, extension);
    }
    
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }
}
```

## 从使用到源码的学习路径

通过以上实战练习，您已经掌握了AWS SDK for Java v2的核心使用方法。现在可以开始深入学习源码设计：

### 1. 理解异步编程模式
- 观察`CompletableFuture`的使用模式
- 分析异步客户端的实现原理
- 学习响应式编程在SDK中的应用

### 2. 探索建造者模式
- 分析各种Request类的建造者实现
- 理解配置类的设计思想
- 学习如何优雅地处理可选参数

### 3. 研究错误处理机制
- 分析异常层次结构设计
- 理解重试策略的实现
- 学习断路器模式的应用

### 4. 深入HTTP客户端实现
- 研究`NettyNioAsyncHttpClient`的实现
- 分析连接池管理策略
- 理解请求签名机制

### 5. 学习代码生成机制
- 研究模型文件如何转换为Java代码
- 分析服务接口的自动生成过程
- 理解一致性保证机制

想要深入学习这些源码设计，建议阅读[[AWS SDK源码设计学习笔记-完整版]]，其中详细分析了这些优秀的设计模式和架构思想。

## 相关资源

- [[AWS SDK源码设计精华]] - 核心设计模式解析
- [[大语言模型]] - AI在云服务中的应用
- [[微服务设计模式]][[设计模式]] - 相关设计模式学习

---

*通过实战练习掌握AWS SDK的使用方法，再深入学习源码中的优秀设计，这样的学习路径能够帮助您更好地理解和应用这些设计思想到自己的项目中。* 

*通过实战练习掌握AWS SDK的使用方法，再深入学习源码中的优秀设计，这样的学习路径能够帮助您更好地理解和应用这些设计思想到自己的项目中。* 