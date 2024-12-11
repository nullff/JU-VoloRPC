package com.qimoju.consumer;

import com.qimoju.common.model.User;
import com.qimoju.common.service.UserService;
import com.qimoju.jurpc.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    /**
     *简易消费者示例
     */
    public static void main(String[] args) {
        // todo 需要获取UserService的实现类对象
//        UserService userService = new UserServiceProxy();
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
    }
}
