package com.lzr.client.feignClient;

import com.lzr.util.JsonUtil;
import com.lzr.constants.ServerConstants;
import com.lzr.entity.LoginBack;
import feign.Feign;
import feign.codec.StringDecoder;

/**
 * WebOperator
 *
 * Author: zirui liu
 * Date: 2021/7/21
 */
public class WebOperator {
    public static LoginBack login(String userName, String password)
    {
        UserAction action = Feign.builder()
//                .decoder(new GsonDecoder())
                .decoder(new StringDecoder())
                .target(UserAction.class, ServerConstants.WEB_URL);

        String s = action.loginAction(userName, password);

        LoginBack back = JsonUtil.jsonToPojo(s, LoginBack.class);
        return back;
    }
}
