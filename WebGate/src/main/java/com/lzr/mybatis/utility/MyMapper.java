package com.lzr.mybatis.utility;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author zirui liu
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T>
{

}
