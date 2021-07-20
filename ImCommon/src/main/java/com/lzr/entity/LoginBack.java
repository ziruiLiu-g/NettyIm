package com.lzr.entity;

import com.lzr.im.common.bean.UserDTO;
import lombok.Data;

import java.util.List;

/**
 * LoginBack
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
@Data
public class LoginBack
{

    List<ImNode> imNodeList;

    private String token;

    private UserDTO userDTO;

}