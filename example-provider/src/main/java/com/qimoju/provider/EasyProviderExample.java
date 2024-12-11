package com.qimoju.provider;

import com.qimoju.common.service.UserService;
import com.qimoju.jurpc.registry.LocalRegistry;
import com.qimoju.jurpc.server.HttpServer;
import com.qimoju.jurpc.server.VertxHttpServer;

public class EasyProviderExample {
    /**
     * 简易服务提供者示例
     */
    public static void main(String[] args) {
        //注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        //提供web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}