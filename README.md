# SpringBoot RocketMQ 示例

这是一个基于Spring Boot和RocketMQ的简单示例项目，包含生产者和消费者。

## 配置信息

- 消费者组：`test_consumer_group`
- 生产者组：`test_producer_group`
- 消息主题：`test_topic`

## 前提条件

- JDK 1.8+
- Maven 3.0+
- RocketMQ 4.5+

## RocketMQ安装和启动

1. 下载RocketMQ: [https://rocketmq.apache.org/download](https://rocketmq.apache.org/download)

2. 解压后启动NameServer:

```bash
sh bin/mqnamesrv
```

3. 启动Broker:

```bash
sh bin/mqbroker -n localhost:9876
```

## 运行项目

1. 启动Spring Boot应用:

```bash
mvn spring-boot:run
```

2. 发送消息:

访问以下URL发送同步消息:
```
http://localhost:8080/message/send?content=测试消息
```

访问以下URL发送异步消息:
```
http://localhost:8080/message/send-async?content=测试异步消息
```

## 代码结构

- `MessageProducer`: 消息生产者，负责发送消息到RocketMQ
- `MessageConsumer`: 消息消费者，监听并处理从RocketMQ接收的消息
- `MessageController`: 提供HTTP接口用于发送测试消息
- `RocketMQConfig`: RocketMQ相关配置

## 注意事项

- 确保RocketMQ服务已启动并且可以连接
- 默认的RocketMQ连接地址是`127.0.0.1:9876`，可以在`application.properties`中修改 