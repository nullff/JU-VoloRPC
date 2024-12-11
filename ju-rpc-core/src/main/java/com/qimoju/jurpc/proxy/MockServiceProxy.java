package com.qimoju.jurpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * mock服务代理-JDK动态代理
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
    /**
     * 当代理对象调用方法时，此方法被调用.
     *
     * @param proxy 代理对象的引用.
     * @param method 被调用的方法对象.
     * @param args 方法的参数.
     * @return 返回默认的对象.
     * @throws Throwable 如果方法调用过程中抛出异常.
     *
     * 此方法重写了InvocationHandler接口的invoke方法，用于处理代理对象的方法调用.
     * 它通过获取方法的返回类型，并返回一个默认值，而不执行实际的方法逻辑.
     * 这主要用于模拟服务调用的场景，例如在测试环境中.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 获取方法的返回类型
        Class<?> methodReturnType = method.getReturnType();
        // 记录日志，显示正在调用的方法名称
        log.info("mock service proxy, mock invoke: {}", method.getName());
        // 根据方法的返回类型，返回一个默认的对象
        return getDefaultObject(methodReturnType);
    }

    private Object getDefaultObject(Class<?> methodReturnType) {
        //基本类型
        if (methodReturnType.isPrimitive()){
            if (methodReturnType == int.class){
                return 0;
            }
            if (methodReturnType == long.class){
                return 0L;
            }
            if (methodReturnType == float.class){
                return 0.0f;
            }
            if (methodReturnType == double.class){
                return 0.0d;
            }
            if (methodReturnType == boolean.class) {
                return false;
            }
            if (methodReturnType == short.class) {
                return (short) 0;
            }
        }
        //对象类型
        return null;
    }
}
