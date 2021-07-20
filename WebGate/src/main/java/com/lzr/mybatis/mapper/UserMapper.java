package com.lzr.mybatis.mapper;

import com.lzr.mybatis.entity.User;
import com.lzr.mybatis.utility.MyMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends MyMapper<User> {
}