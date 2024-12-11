package com.qimoju.examplespringbootconsumer;

import com.qimoju.common.model.User;
import com.qimoju.common.service.UserService;
import com.qimoju.jurpc.springboot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("猪猪");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

}
