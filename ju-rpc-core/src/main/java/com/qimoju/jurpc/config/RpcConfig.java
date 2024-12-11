package com.qimoju.jurpc.config;

import com.qimoju.jurpc.fault.retry.RetryStrategyKeys;
import com.qimoju.jurpc.fault.tolerant.TolerantStrategy;
import com.qimoju.jurpc.fault.tolerant.TolerantStrategyKeys;
import com.qimoju.jurpc.loadbalancer.LoadBalancerKeys;
import com.qimoju.jurpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC框架配置
 */
@Data
public class RpcConfig {
    // 框架名称
    private String name = "ju-rpc";
    // 版本
    private String version = "1.0";
    // 服务端地址
    private String serverHost = "localhost";
    // 服务端端口
    private Integer serverPort = 8080;
    // mock模拟调用策略
    private boolean mock = false;
    //序列化器配置
    private String serializer = SerializerKeys.JDK;
    // 注册中心配置
    private RegistryConfig registryConfig = new RegistryConfig();
    // 负载均衡配置
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;
    // 重试策略配置
    private String retryStrategy = RetryStrategyKeys.FIXED_INTERVAL;
    // 容错策略配置
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;
}
