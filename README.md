# JU-VoloRPC

JU-VoloRPC 是一个仿照 Dubbo 开发的高性能、轻量化的 RPC 框架。它基于 Java、Etcd 和 Vert.x 构建，并提供以下特性：

- 底层使用 TCP 通信，保证了高性能
- 基于 Vert.x 自定义协议头，实现了轻量化
- 支持 SPI，可以自定义配置多种编解码器及传输协议
- 支持切换 TCP/HTTP
- 支持 Spring Boot Starter 和注解驱动

## 技术选型

### 后端

后端技术以 Java 为主，但所有的思想和设计都是可以复用到其他语言的，代码不同罢了。

- Vert.x 框架
- Etcd 云原生存储中间件（jetcd 客户端）
- ZooKeeper 分布式协调工具（curator 客户端）
- SPI 机制
- 多种序列化器
    - JSON 序列化
    - Kryo 序列化
    - Hessian 序列化
- 多种设计模式
    - 双检锁单例模式
    - 工厂模式
    - 代理模式
    - 装饰者模式
- Spring Boot Starter 开发
- 反射和注解驱动
- Guava Retrying 重试库
- JUnit 单元测试
- Logback 日志库
- Hutool、Lombok 工具库

## 源码目录

- `ju-rpc-core`：框架核心代码
- `example-common`：示例代码公用模块
- `example-consumer`：示例服务消费者
- `example-provider`：示例服务提供者
- `example-springboot-consumer`：示例服务消费者（Spring Boot 框架）
- `example-springboot-provider`：示例服务提供者（Spring Boot 框架）
- `ju-rpc-spring-boot-starter`：注解驱动的 RPC 框架，可在 Spring Boot 项目中快速使用
