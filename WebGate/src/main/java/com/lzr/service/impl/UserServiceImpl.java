package com.lzr.service.impl;

import com.lzr.mybatis.entity.User;
import com.lzr.mybatis.mapper.UserMapper;
import com.lzr.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * UserServiceImpl
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public User login(User user) {
        /*
        User sample = new User();
        sample.setUserName(user.getUserName());
        User u = userMapper.selectOne(sample);
        if (null == u) {
            log.info("找不到用户信息 username={}", user.getUserName());

            return null;
        }
        */

        return user;
    }

    @Override
    @Cacheable(value = "IMKey:User:", key = "#userId")
    public User getById(String userId) {
        /*
        User u = userMapper.selectByPrimaryKey(Integer.valueOf(userid));
        if (null == u) {
            log.info("找不到用户信息 userid={}", userid);
        }
        return u;
        */
        return null;
    }

    @Override
    @CacheEvict(value = "IMKey:User:", key = "#userId")
    public int deleteById(String userId) {
        /*
        int u = userMapper.deleteByPrimaryKey(Integer.valueOf(userid));
        if (0 == u) {
            log.info("找不到用户信息 userid={}", userid);
        }
        return u;
        */
        return 0;
    }
}
