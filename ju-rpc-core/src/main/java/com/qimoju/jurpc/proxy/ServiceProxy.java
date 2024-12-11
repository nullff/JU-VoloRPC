package com.qimoju.jurpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.qimoju.jurpc.RpcApplication;
import com.qimoju.jurpc.config.RpcConfig;
import com.qimoju.jurpc.constant.RpcConstant;
import com.qimoju.jurpc.fault.retry.RetryStrategy;
import com.qimoju.jurpc.fault.retry.RetryStrategyFactory;
import com.qimoju.jurpc.fault.tolerant.TolerantStrategy;
import com.qimoju.jurpc.fault.tolerant.TolerantStrategyFactory;
import com.qimoju.jurpc.loadbalancer.LoadBalancer;
import com.qimoju.jurpc.loadbalancer.LoadBalancerFactory;
import com.qimoju.jurpc.model.RpcRequest;
import com.qimoju.jurpc.model.RpcResponse;
import com.qimoju.jurpc.model.ServiceMetaInfo;
import com.qimoju.jurpc.registry.Registry;
import com.qimoju.jurpc.registry.RegistryFactory;
import com.qimoju.jurpc.serializer.Serializer;
import com.qimoju.jurpc.serializer.SerializerFactory;
import com.qimoju.jurpc.server.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态代理 （JDK动态代理）
 */
public class ServiceProxy implements InvocationHandler {
    /**
     * 当代理对象调用方法时，该方法会被调用
     * 它负责构造RPC请求，发现服务地址，并发送请求以获取响应
     *
     * @param proxy 代理对象
     * @param method 被调用的方法
     * @param args 方法参数
     * @return 方法调用的结果
     * @throws Throwable 如果调用过程中发生错误
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        RpcConfig rpcConfig = null;

        RpcResponse rpcResponse;

        try {
            // 从注册中心获取服务提供者请求地址
            rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }


            // 负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            // 将调用方法名（请求路径）作为负载均衡参数
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

            // rpc 请求
            // 使用重试机制
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(() ->
                    VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
            );

        } catch (Exception e) {
            // 容错
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            rpcResponse = tolerantStrategy.doTolerant(null, e);
        }
        return rpcResponse.getData();
    }
}
