# AWS S3 Demo 使用指南

这是一个基于Spring Boot和AWS SDK for Java v2的S3对象存储服务演示项目。

## 功能特性

- ✅ 文件上传（支持MultipartFile）
- ✅ 文本内容上传
- ✅ 文件下载（文本和二进制）
- ✅ 预签名URL生成
- ✅ 文件列表查询
- ✅ 文件删除（单个和批量）
- ✅ 存储桶管理
- ✅ 错误处理和日志记录

## 环境准备

### 1. 依赖要求

- Java 8+
- Maven 3.6+
- AWS账号或MinIO等兼容S3的服务

### 2. 配置AWS凭证

在 `src/main/resources/application.yaml` 中配置您的AWS凭证：

```yaml
aws:
  s3:
    access-key: your-access-key
    secret-key: your-secret-key
    region: us-east-1
    bucket: your-bucket-name
    endpoint: # 可选，用于MinIO等自定义端点
```

或者使用环境变量：

```bash
export AWS_ACCESS_KEY=your-access-key
export AWS_SECRET_KEY=your-secret-key
export AWS_REGION=us-east-1
export AWS_S3_BUCKET=your-bucket-name
export AWS_S3_ENDPOINT=http://localhost:9000  # 可选，MinIO端点
```

## 运行项目

### 1. 编译项目

```bash
mvn clean compile
```

### 2. 启动应用

```bash
mvn spring-boot:run
```

或者：

```bash
java -jar target/demo2-0.0.1-SNAPSHOT.jar
```

应用将在 `http://localhost:8080` 启动。

## API 接口说明

### 1. 文件上传

**POST** `/api/s3/upload`

- 参数：`file` (MultipartFile)
- 响应：
  ```json
  {
    "success": true,
    "message": "文件上传成功",
    "key": "uploads/20240120/abc123.jpg",
    "originalFilename": "example.jpg",
    "size": 1024,
    "contentType": "image/jpeg"
  }
  ```

### 2. 文本上传

**POST** `/api/s3/upload-text`

- 参数：
  - `key`: 文件键名
  - `content`: 文本内容
- 响应：
  ```json
  {
    "success": true,
    "message": "文本上传成功",
    "key": "test.txt",
    "contentLength": 100
  }
  ```

### 3. 文件下载

**GET** `/api/s3/download/{key}`

- 路径参数：`key` - 文件键名
- 响应：
  ```json
  {
    "success": true,
    "key": "test.txt",
    "content": "文件内容",
    "contentLength": 100
  }
  ```

### 4. 生成下载链接

**GET** `/api/s3/download-url/{key}`

- 路径参数：`key` - 文件键名
- 响应：
  ```json
  {
    "success": true,
    "key": "test.txt",
    "downloadUrl": "https://s3.amazonaws.com/...",
    "message": "下载链接生成成功"
  }
  ```

### 5. 列出文件

**GET** `/api/s3/list?prefix=uploads/`

- 查询参数：`prefix` - 文件前缀（可选）
- 响应：
  ```json
  {
    "success": true,
    "files": [
      {
        "key": "uploads/20240120/abc123.jpg",
        "size": 1024,
        "lastModified": "2024-01-20T10:00:00Z",
        "etag": "d41d8cd98f00b204e9800998ecf8427e"
      }
    ],
    "count": 1,
    "prefix": "uploads/"
  }
  ```

### 6. 删除文件

**DELETE** `/api/s3/delete/{key}`

- 路径参数：`key` - 文件键名
- 响应：
  ```json
  {
    "success": true,
    "message": "文件删除成功",
    "key": "test.txt"
  }
  ```

### 7. 批量删除

**DELETE** `/api/s3/delete-batch`

- 请求体：`["key1", "key2", "key3"]`
- 响应：
  ```json
  {
    "success": true,
    "message": "批量删除成功",
    "deletedKeys": ["key1", "key2", "key3"],
    "count": 3
  }
  ```

### 8. 存储桶管理

**检查存储桶是否存在**

**GET** `/api/s3/bucket/{bucketName}/exists`

**创建存储桶**

**POST** `/api/s3/bucket/{bucketName}`

## 测试使用

### 1. 使用curl测试上传

```bash
# 上传文件
curl -X POST -F "file=@example.txt" http://localhost:8080/api/s3/upload

# 上传文本
curl -X POST \
  -d "key=test.txt" \
  -d "content=Hello World" \
  http://localhost:8080/api/s3/upload-text
```

### 2. 使用curl测试下载

```bash
# 下载文件内容
curl http://localhost:8080/api/s3/download/test.txt

# 生成下载链接
curl http://localhost:8080/api/s3/download-url/test.txt
```

### 3. 使用curl测试文件列表

```bash
# 列出所有文件
curl http://localhost:8080/api/s3/list

# 列出指定前缀的文件
curl http://localhost:8080/api/s3/list?prefix=uploads/
```

## 配置说明

### application.yaml 完整配置

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
    endpoint: ${AWS_S3_ENDPOINT:}
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

## MinIO 本地测试

如果您想使用MinIO进行本地测试：

### 1. 启动MinIO

```bash
docker run -p 9000:9000 -p 9001:9001 \
  -e "MINIO_ROOT_USER=admin" \
  -e "MINIO_ROOT_PASSWORD=password123" \
  minio/minio server /data --console-address ":9001"
```

### 2. 配置连接MinIO

```yaml
aws:
  s3:
    access-key: admin
    secret-key: password123
    region: us-east-1
    bucket: demo-bucket
    endpoint: http://localhost:9000
```

### 3. 创建存储桶

访问 `http://localhost:9001` 进入MinIO控制台，创建名为 `demo-bucket` 的存储桶。

## 故障排除

### 1. 常见错误

**AWS凭证错误**
```
The AWS Access Key Id you provided does not exist in our records
```
解决：检查 access-key 和 secret-key 是否正确。

**存储桶不存在**
```
NoSuchBucket: The specified bucket does not exist
```
解决：确保存储桶已创建，或使用 `/api/s3/bucket/{bucketName}` 接口创建。

**权限不足**
```
Access Denied
```
解决：确保AWS用户有S3操作权限。

### 2. 调试日志

启用详细日志：

```yaml
logging:
  level:
    com.example.demo2: DEBUG
    software.amazon.awssdk: DEBUG
    software.amazon.awssdk.request: DEBUG
```

## 扩展功能

这个demo基于指南实现了基础功能，您可以进一步扩展：

1. **多部分上传** - 支持大文件上传
2. **版本控制** - 文件版本管理
3. **元数据管理** - 自定义文件元数据
4. **异步操作** - 使用S3AsyncClient
5. **文件加密** - 服务端加密配置
6. **生命周期管理** - 自动删除过期文件

## 参考资料

- [AWS SDK for Java v2 官方文档](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/)
- [Amazon S3 API 参考](https://docs.aws.amazon.com/AmazonS3/latest/API/)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot) 