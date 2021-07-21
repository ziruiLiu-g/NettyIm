package com.lzr.client.clientBuilder;

import com.lzr.client.client.ClientSession;
import com.lzr.im.common.bean.UserDTO;
import com.lzr.im.common.bean.msg.ProtoMsg;

/**
 * login msg builder
 *
 * Author: zirui liu
 * Date: 2021/7/21
 */
public class LoginMsgBuilder extends BaseBuilder {
    private final UserDTO user;

    public LoginMsgBuilder(UserDTO user, ClientSession session) {
        super(ProtoMsg.HeadType.LOGIN_REQUEST, session);
        this.user = user;
    }
    
    public ProtoMsg.Message build() {
        ProtoMsg.Message message = buildCommon(-1);
        
        ProtoMsg.LoginRequest.Builder builder = ProtoMsg.LoginRequest.newBuilder()
                .setDeviceId(user.getDevId())
                .setPlatform(user.getPlatform().ordinal())
                .setToken(user.getToken())
                .setUid(user.getUserId());
        
        return message.toBuilder().setLoginRequest(builder).build();
    }
    
    public static ProtoMsg.Message buildLoginMsg(UserDTO user, ClientSession session) {
        LoginMsgBuilder builder = new LoginMsgBuilder(user, session);
        return builder.build();
    }
}
