package com.lzr.client.feignClient;

import feign.Param;
import feign.RequestLine;

/**
 * rpc local proxy
 * 
 * Author: zirui liu
 * Date: 2021/7/21
 */
public interface UserAction {
    /**
     * login proxy
     * 
     * @param username
     * @param password
     * @return
     */
    @RequestLine("GET /user/login/{username}/{password}")
    public String loginAction(@Param("username") String username, @Param("password") String password);

    /**
     * get user info (not used yet)
     *
     * @param userid uid
     * @return user info
     */
    @RequestLine("GET /{userid}")
    public String getById(@Param("userid") Integer userid);
}
