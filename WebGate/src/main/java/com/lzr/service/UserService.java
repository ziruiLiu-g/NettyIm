package com.lzr.service;

import com.lzr.mybatis.entity.User;

/**
 * UserService
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
public interface UserService {
    User login(User user);

    User getById(String userId);

    int deleteById(String userId);
}
