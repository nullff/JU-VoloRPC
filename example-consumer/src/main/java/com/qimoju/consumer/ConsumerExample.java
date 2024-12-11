package com.qimoju.consumer;

import com.qimoju.common.model.User;
import com.qimoju.common.service.UserService;
import com.qimoju.jurpc.config.RpcConfig;
import com.qimoju.jurpc.proxy.ServiceProxyFactory;
import com.qimoju.jurpc.utils.ConfigUtils;

/**
 * 简易服务消费者示例
 */
public class ConsumerExample {
    public static void main(String[] args) {
        //获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("猪猪");
        //调用
        User newUser = userService.getUser(user);
        if (newUser != null){
            System.out.println(newUser.getName());
        }else {
            System.out.println("没有找到用户");
        }
        short number = userService.getNumber();
        System.out.println(number);
    }
}
