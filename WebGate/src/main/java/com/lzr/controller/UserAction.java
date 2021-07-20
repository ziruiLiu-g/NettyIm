package com.lzr.controller;

import com.lzr.Balance.ImLoadBalance;
import com.lzr.entity.ImNode;
import com.lzr.entity.LoginBack;
import com.lzr.im.common.bean.UserDTO;
import com.lzr.mybatis.entity.User;
import com.lzr.service.UserService;
import com.lzr.util.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * UserAction
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api("user actions")
public class UserAction {
    @Resource
    private UserService userServer;

    @Resource
    private ImLoadBalance imLoadBalance;

    @ApiOperation(value = "login", notes = "user login")
    @RequestMapping(value = "/login/{username}/{password}", method = RequestMethod.GET)
    public String loginAction (
            @PathVariable("username") String username,
            @PathVariable("password") String password
    ) {
        User user = new User();
        user.setUserName(username);
        user.setPassWord(password);
        user.setUserId(user.getUserName());

        // real login
//         User loginUser = userServer.login();

        LoginBack back = new LoginBack();
        /**
         * get best work (netty server)
         */
        List<ImNode> allworkers = imLoadBalance.getWorkers();
        back.setImNodeList(allworkers);

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        back.setUserDTO(userDTO);
        back.setToken(user.getUserId().toString());
        String r = JsonUtil.pojoToJson(back);

        return r;
    }

    /**
     * del all im node from zk
     *
     * @return result
     */
    @ApiOperation(value = "del node", notes = "del all im node from zk")
    @RequestMapping(value = "/removeWorkers", method = RequestMethod.GET)
    public String removeWorkers()
    {
        imLoadBalance.removeWorkers();
        return "del successfully";
    }
}
