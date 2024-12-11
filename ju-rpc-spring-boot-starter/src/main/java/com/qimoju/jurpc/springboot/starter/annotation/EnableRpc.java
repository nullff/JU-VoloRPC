package com.qimoju.jurpc.springboot.starter.annotation;
import com.qimoju.jurpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.qimoju.jurpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.qimoju.jurpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 Rpc 注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
//仅在用户使用@EnableRpc注解时，才启动RPC框架。通过给EnableRpc增加@Import注解，来注册自定义的启动类，实现灵活的可选加载。
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 需要启动 server
     *
     * @return
     */
    boolean needServer() default true;
}
