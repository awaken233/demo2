package com.example.demo2.service;

import com.example.demo2.config.S3Properties;
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
    
    /**
     * 检查存储桶是否存在
     */
    public boolean bucketExists(String bucketName) {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.headBucket(headBucketRequest);
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        } catch (S3Exception e) {
            log.error("检查存储桶失败: {}", e.getMessage());
            throw new RuntimeException("检查存储桶失败", e);
        }
    }
    
    /**
     * 创建存储桶
     */
    public void createBucket(String bucketName) {
        try {
            if (!bucketExists(bucketName)) {
                CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build();
                
                CreateBucketResponse response = s3Client.createBucket(createBucketRequest);
                log.info("存储桶创建成功: {}", response.location());
            } else {
                log.info("存储桶已存在: {}", bucketName);
            }
        } catch (BucketAlreadyExistsException e) {
            log.warn("存储桶已存在: {}", bucketName);
        } catch (S3Exception e) {
            log.error("创建存储桶失败: {}", e.getMessage());
            throw new RuntimeException("创建存储桶失败", e);
        }
    }
    
    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file) throws IOException {
        return uploadFile(file, null);
    }
    
    /**
     * 上传文件（指定Key）
     */
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
            
            log.info("文件上传成功: {} -> {}, ETag: {}", 
                    file.getOriginalFilename(), key, response.eTag());
            return key;
            
        } catch (S3Exception e) {
            log.error("上传文件失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    /**
     * 上传字符串内容
     */
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
    
    /**
     * 下载文件为字符串
     */
    public String downloadAsString(String key) {
        String bucketName = s3Properties.getBucket();
        
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);
            
            String content = new String(readAllBytes(response), StandardCharsets.UTF_8);
            
            // 打印元数据
            GetObjectResponse metadata = response.response();
            log.info("文件下载成功: {}, Content-Type: {}, Content-Length: {}", 
                    key, metadata.contentType(), metadata.contentLength());
            
            return content;
            
        } catch (S3Exception | IOException e) {
            log.error("下载对象失败: {}", e.getMessage());
            throw new RuntimeException("下载文件失败", e);
        }
    }
    
    /**
     * 下载文件为字节数组
     */
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
    
    /**
     * 生成下载链接
     */
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
    
    /**
     * 列出对象
     */
    public List<S3Object> listObjects(String prefix) {
        String bucketName = s3Properties.getBucket();
        
        try {
            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .maxKeys(100)
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
    
    /**
     * 删除对象
     */
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
    
    /**
     * 批量删除对象
     */
    public void deleteObjects(List<String> keys) {
        String bucketName = s3Properties.getBucket();
        
        try {
            List<ObjectIdentifier> objectsToDelete = keys.stream()
                    .map(key -> ObjectIdentifier.builder().key(key).build())
                    .collect(Collectors.toList());
            
            Delete delete = Delete.builder()
                    .objects(objectsToDelete)
                    .quiet(false)
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
    
    /**
     * 生成文件Key
     */
    private String generateFileKey(String originalFilename) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String extension = getFileExtension(originalFilename);
        
        return String.format("uploads/%s/%s%s", timestamp, uuid, extension);
    }
    
    /**
     * 获取文件扩展名
     */
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
} 