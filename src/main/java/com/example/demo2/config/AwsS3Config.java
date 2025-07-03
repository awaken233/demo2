package com.example.demo2.config;

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