package com.qimoju.jurpc.config;

import lombok.Data;

/**
 * RPC框架注册中心配置
 */
@Data
public class RegistryConfig {
    // 注册中心类型
    private String registry = "etcd";
    // 注册中心地址
    private String address = "http://localhost:2380";
    // 用户名
    private String username;
    // 密码
    private String password;
    // 超时时间（毫秒）
    private Long timeout = 1000L;
}
