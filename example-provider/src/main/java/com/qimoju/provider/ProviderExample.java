package com.qimoju.provider;

import com.qimoju.common.service.UserService;
import com.qimoju.jurpc.RpcApplication;
import com.qimoju.jurpc.config.RegistryConfig;
import com.qimoju.jurpc.config.RpcConfig;
import com.qimoju.jurpc.model.ServiceMetaInfo;
import com.qimoju.jurpc.registry.LocalRegistry;
import com.qimoju.jurpc.registry.Registry;
import com.qimoju.jurpc.registry.RegistryFactory;
import com.qimoju.jurpc.server.HttpServer;
import com.qimoju.jurpc.server.VertxHttpServer;
import com.qimoju.jurpc.server.tcp.VertxTcpServer;

/**
 * 简易服务提供者示例
 */
public class ProviderExample {
    public static void main(String[] args) {
        // RPC 框架初始化
        RpcApplication.init();

        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        // 启动 web 服务
//        HttpServer httpServer = new VertxHttpServer();
//        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
        //启动tcp服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(8888);
    }
}
