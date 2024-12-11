package com.qimoju.provider;

import com.qimoju.common.model.User;
import com.qimoju.common.service.UserService;

public class UserServiceImpl implements UserService {
    /**
     * 重写getUser方法
     *
     * 此方法接收一个User对象作为参数，并在控制台打印出该User对象的用户名
     * 主要用途是展示如何获取用户信息并进行简单处理
     *
     * @param user 一个User对象，包含用户信息
     * @return 返回传入的User对象
     */
    @Override
    public User getUser(User user) {
        // 打印用户名称
        System.out.println("用户名：" + user.getName());
        // 返回传入的用户对象
        return user;
    }
}
