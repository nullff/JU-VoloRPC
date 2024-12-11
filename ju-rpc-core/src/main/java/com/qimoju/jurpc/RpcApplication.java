package com.qimoju.jurpc;

import com.qimoju.jurpc.config.RegistryConfig;
import com.qimoju.jurpc.config.RpcConfig;
import com.qimoju.jurpc.constant.RpcConstant;
import com.qimoju.jurpc.registry.Registry;
import com.qimoju.jurpc.registry.RegistryFactory;
import com.qimoju.jurpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC 框架应用
 * 相当于 holder，存放了项目全局用到的变量。双检锁单例模式实现
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，支持传入自定义配置
     *
     * @param newRpcConfig
     */
//    public static void init(RpcConfig newRpcConfig){
//        rpcConfig = newRpcConfig;
//        log.info("ju-rpc init, config = {}",newRpcConfig.toString());
//    }

    /**
     * 初始化方法，负责加载RPC配置并初始化相关设置
     * 此方法首先尝试从配置文件中加载RpcConfig配置如果加载失败，则使用默认配置进行初始化
     * 之所以这样做，是为了确保即使在配置文件缺失或损坏的情况下，系统也能正常运行
     */
    public static void init(){
        RpcConfig newRpcConfig;
        RegistryConfig newRegistryConfig;
        try {
            // 尝试从配置文件中加载RpcConfig配置
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 配置如果加载失败，使用默认配置
            log.warn("load config failed, use default config");
            newRpcConfig = new RpcConfig();
        }
        // 使用加载或默认的配置进行初始化
        init(newRpcConfig);
    }

    /**
     * 框架初始化，支持传入自定义配置
     *
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);

        //创建并注册 shutdown hook,在JVM退出时执行操作

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 关闭注册中心
            registry.destroy();
            log.info("registry destroy");
        }));
    }



    /**
     * 获取Rpc配置实例
     *
     * 该方法实现了一个懒汉式单例模式的 RpcConfig 实例的获取
     * 它确保了在多线程环境下的线程安全，并且只有在第一次调用时才初始化实例
     *
     * @return RpcConfig 实例
     */
    public static RpcConfig getRpcConfig(){
        // 检查是否已经创建了 RpcConfig 实例
        if (rpcConfig == null){
            // 同步块确保在多线程环境下安全地初始化实例
            synchronized (RpcApplication.class) {
                // 再次检查，防止在等待队列中的线程重复初始化
                if (rpcConfig == null){
                    // 调用初始化方法创建实例
                    init();
                }
            }
        }
        // 返回 RpcConfig 实例
        return rpcConfig;
    }
}
