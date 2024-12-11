package com.qimoju.common.service;

import com.qimoju.common.model.User;

/**
 * 用户服务
 */
public interface UserService {
    /**
     * 获取用户
     * @param user
     * @return User
     */
    User getUser(User user);

    default short getNumber()
    {
        return 1;
    }
}
