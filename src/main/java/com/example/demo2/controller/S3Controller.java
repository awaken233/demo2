package com.example.demo2.controller;

import com.example.demo2.service.S3Service;
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
     * 上传文本内容
     */
    @PostMapping("/upload-text")
    public ResponseEntity<Map<String, Object>> uploadText(
            @RequestParam("key") String key,
            @RequestParam("content") String content) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String uploadedKey = s3Service.uploadString(key, content);
            
            result.put("success", true);
            result.put("message", "文本上传成功");
            result.put("key", uploadedKey);
            result.put("contentLength", content.length());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("文本上传失败", e);
            result.put("success", false);
            result.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 下载文件内容（文本）
     */
    @GetMapping("/download/{key}")
    public ResponseEntity<Map<String, Object>> downloadFile(@PathVariable String key) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String content = s3Service.downloadAsString(key);
            
            result.put("success", true);
            result.put("key", key);
            result.put("content", content);
            result.put("contentLength", content.length());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("文件下载失败", e);
            result.put("success", false);
            result.put("message", "下载失败: " + e.getMessage());
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
    
    /**
     * 删除文件
     */
    @DeleteMapping("/delete/{key}")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable String key) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            s3Service.deleteObject(key);
            
            result.put("success", true);
            result.put("message", "文件删除成功");
            result.put("key", key);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("文件删除失败", e);
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 批量删除文件
     */
    @DeleteMapping("/delete-batch")
    public ResponseEntity<Map<String, Object>> deleteFiles(@RequestBody List<String> keys) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            s3Service.deleteObjects(keys);
            
            result.put("success", true);
            result.put("message", "批量删除成功");
            result.put("deletedKeys", keys);
            result.put("count", keys.size());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("批量删除失败", e);
            result.put("success", false);
            result.put("message", "批量删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 检查存储桶是否存在
     */
    @GetMapping("/bucket/{bucketName}/exists")
    public ResponseEntity<Map<String, Object>> checkBucketExists(@PathVariable String bucketName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean exists = s3Service.bucketExists(bucketName);
            
            result.put("success", true);
            result.put("bucketName", bucketName);
            result.put("exists", exists);
            result.put("message", exists ? "存储桶存在" : "存储桶不存在");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("检查存储桶失败", e);
            result.put("success", false);
            result.put("message", "检查存储桶失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 创建存储桶
     */
    @PostMapping("/bucket/{bucketName}")
    public ResponseEntity<Map<String, Object>> createBucket(@PathVariable String bucketName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            s3Service.createBucket(bucketName);
            
            result.put("success", true);
            result.put("bucketName", bucketName);
            result.put("message", "存储桶创建成功");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("创建存储桶失败", e);
            result.put("success", false);
            result.put("message", "创建存储桶失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
} 