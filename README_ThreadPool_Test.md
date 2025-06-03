# 线程池测试配置说明

## 概述1

本项目已配置两个独立的线程池来测试同步和异步（模拟协程）接口的性能差异：

- **syncTestPool**: 同步测试线程池，大小为100，已预热
- **asyncTestPool**: 异步测试线程池，大小为100，已预热

## 配置详情

### 线程池配置 (ThreadPoolConfig.java)

```yaml
syncTestPool:
  核心线程数: 100
  最大线程数: 100
  队列容量: 400
  线程名前缀: "syncTest-"
  预热: prestartAllCoreThreads() # 启动时预热所有核心线程
  
asyncTestPool:
  核心线程数: 100
  最大线程数: 100
  队列容量: 400
  线程名前缀: "asyncTest-"
  预热: prestartAllCoreThreads() # 启动时预热所有核心线程
```

## 测试接口（由用户的FastAPI项目提供）

### 1. 同步接口 - health1

**URL**: `http://127.0.0.1:8080/actuator/health1`  
**方法**: GET  
**特点**: 
- FastAPI同步接口
- 用于测试同步阻塞性能

### 2. 异步接口 - health2 (协程)

**URL**: `http://127.0.0.1:8080/actuator/health2`  
**方法**: GET  
**特点**:
- FastAPI异步协程接口
- 用于测试协程性能

## 性能测试类 (FastApiPerformanceTest.java)

### 测试方法

1. **testSyncApiPerformance()**: 测试同步接口性能
   - 使用syncTestPool线程池发起100个并发请求
   - 统计响应时间、QPS、成功率等指标
   
2. **testAsyncApiPerformance()**: 测试异步接口性能
   - 使用asyncTestPool线程池发起100个并发请求
   - 统计响应时间、QPS、成功率等指标

3. **comparePerformance()**: 对比测试
   - 先预热接口
   - 依次测试同步和异步接口
   - 输出对比结果

### 统计指标

- **总请求数/成功数/失败数**
- **总耗时/QPS**
- **平均响应时间**
- **响应时间统计**: 最小值、最大值、P50、P90、P95
- **线程池状态**: 活跃线程数、总线程数、队列大小

## 运行测试

### 前提条件

1. 确保您的FastAPI项目已启动并运行在 `http://127.0.0.1:8080`
2. 确保接口 `/actuator/health1` 和 `/actuator/health2` 可访问

### 执行测试

```bash
# 运行同步接口测试
mvn test -Dtest=FastApiPerformanceTest#testSyncApiPerformance

# 运行异步接口测试
mvn test -Dtest=FastApiPerformanceTest#testAsyncApiPerformance

# 运行对比测试
mvn test -Dtest=FastApiPerformanceTest#comparePerformance

# 运行所有测试
mvn test -Dtest=FastApiPerformanceTest
```

### 测试日志示例

```
2025-06-03 15:30:31 INFO  [main] FastApiPerformanceTest - 开始测试同步接口性能，请求数：100
2025-06-03 15:30:32 INFO  [main] FastApiPerformanceTest - 同步接口测试完成！
2025-06-03 15:30:32 INFO  [main] FastApiPerformanceTest - 总请求数：100，成功数：100，失败数：0
2025-06-03 15:30:32 INFO  [main] FastApiPerformanceTest - 总耗时：1.23秒
2025-06-03 15:30:32 INFO  [main] FastApiPerformanceTest - QPS：81.30
2025-06-03 15:30:32 INFO  [main] FastApiPerformanceTest - 平均响应时间：105.20ms
2025-06-03 15:30:32 INFO  [main] FastApiPerformanceTest - 响应时间统计 - 最小：98ms，最大：125ms，P50：104ms，P90：115ms，P95：120ms
2025-06-03 15:30:32 INFO  [main] FastApiPerformanceTest - 线程池状态 - 活跃：0，总数：100，队列：0
```

## 线程池预热功能

### 预热说明

- 两个线程池在Bean初始化时会自动调用 `prestartAllCoreThreads()`
- 这会预先创建所有100个核心线程，避免首次请求的线程创建开销
- 预热完成后会在日志中显示实际预热的线程数量

### 预热日志示例

```
2025-06-03 15:30:31 INFO  [main] ThreadPoolConfig - 同步测试线程池预热完成，预热线程数：100
2025-06-03 15:30:31 INFO  [main] ThreadPoolConfig - 异步测试线程池预热完成，预热线程数：100
```

## 性能对比预期

### 预期差异

1. **同步接口测试**:
   - 使用syncTestPool线程池
   - 模拟Java传统同步调用FastAPI的同步接口
   - 性能受FastAPI同步接口处理能力限制

2. **异步接口测试**:
   - 使用asyncTestPool线程池  
   - 调用FastAPI的协程接口
   - 预期在高并发下性能更优

### 注意事项

1. 测试前确保FastAPI服务正常运行
2. 可以根据实际需要调整测试请求数量（当前默认100个）
3. 线程池配置可以根据实际场景调整
4. 建议多次运行测试取平均值以获得更准确的结果

## 日志监控

应用运行时可以通过日志观察线程池的工作情况：

```bash
# 启动应用并查看日志
mvn spring-boot:run

# 或查看特定日志
tail -f logs/application.log | grep -E "(同步|异步)"
```

## 单元测试

运行包含的单元测试来验证配置：

```bash
mvn test -Dtest=ThreadPoolTest
```

测试将验证：
- 线程池配置是否正确
- 并发任务执行是否正常
- 线程命名是否符合预期

## 注意事项

1. 异步接口返回的是`CompletableFuture`，Spring会自动处理JSON序列化
2. 线程池配置可以根据实际需求调整大小
3. 生产环境建议增加线程池监控和告警
4. 异步操作需要注意异常处理和超时设置 