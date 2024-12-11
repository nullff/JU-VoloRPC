package com.qimoju.jurpc.proxy;

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
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class<?>[]{serviceClass},
                new ServiceProxy()
        );
    }
}
