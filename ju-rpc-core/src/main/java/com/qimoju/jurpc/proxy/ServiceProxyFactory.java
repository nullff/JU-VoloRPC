package com.qimoju.jurpc.proxy;

import com.qimoju.jurpc.RpcApplication;
import com.qimoju.jurpc.config.RpcConfig;
import com.qimoju.jurpc.proxy.ServiceProxy;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂（用于创建代理对象）
 */
public class ServiceProxyFactory {

    /**
     * 根据服务类生成代理对象
     * 该方法使用Java的动态代理功能来创建一个指定接口的代理实例
     * 代理实例可以用于远程过程调用（RPC），拦截方法调用等场景
     *
     * @param serviceClass 服务接口的类对象，指定了代理对象需要实现的接口
     * @return 返回一个实现了指定接口的代理对象，通过这个代理对象可以进行远程方法调用等操作
     */
    public static <T> T getProxy(Class<T> serviceClass){
        if (RpcApplication.getRpcConfig().isMock()){
            return getMockProxy(serviceClass);
        }
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class<?>[]{serviceClass},
                new ServiceProxy()
        );
    }

    /**
     * 创建并返回一个服务类的模拟代理实例
     * 该方法用于生成一个指定服务接口的代理对象，通过该代理对象可以对服务进行模拟和测试
     * 主要应用在单元测试中，以便于隔离测试环境和真实的服
     *
     * @param serviceClass 服务接口的类对象，指定需要创建代理实例的接口类型
     * @param <T> 泛型参数，表示服务接口的类型
     * @return 返回一个指定服务接口类型的代理实例，通过这个实例可以调用接口方法
     */
    public static <T> T getMockProxy(Class<T> serviceClass){
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class<?>[]{serviceClass},
                new MockServiceProxy()
        );
    }
}
