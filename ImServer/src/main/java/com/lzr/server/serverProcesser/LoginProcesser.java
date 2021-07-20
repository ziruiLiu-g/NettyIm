package com.lzr.server.serverProcesser;

import com.lzr.im.common.ProtoInstant;
import com.lzr.im.common.bean.UserDTO;
import com.lzr.im.common.bean.msg.ProtoMsg;
import com.lzr.server.protoBuilder.LoginResponceBuilder;
import com.lzr.server.server.session.LocalSession;
import com.lzr.server.server.session.service.SessionManger;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * LoginProcesser
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Data
@Slf4j
@Service("LoginProcesser")
public class LoginProcesser extends AbstractServerProcesser {
    @Autowired
    LoginResponceBuilder loginResponceBuilder;
    
    @Autowired
    SessionManger sessionManger;
    
    @Override
    public ProtoMsg.HeadType op() {
        return ProtoMsg.HeadType.LOGIN_REQUEST;
    }

    @Override
    public Boolean action(LocalSession session, ProtoMsg.Message proto) {
        // get token
        ProtoMsg.LoginRequest info = proto.getLoginRequest();
        long seqNo = proto.getSequence();
        
        UserDTO user = UserDTO.fromMsg(info);
        
        // check user
        boolean isValidUser = checkUser(user);
        if (!isValidUser) {
            ProtoInstant.ResultCodeEnum resultCodeEnum = ProtoInstant.ResultCodeEnum.NO_TOKEN;
            
            ProtoMsg.Message response = loginResponceBuilder.loginResponse(resultCodeEnum, seqNo, "-1");
            
            // send and close conn
            session.writeAndClose(response);
            return false;
        }
        
        session.setUser(user);

        /**
         * bind session
         */
        session.bind();
        sessionManger.addLocalSession(session);

        /**
         * notify client, login success
         */
        ProtoInstant.ResultCodeEnum resultCodeEnum = ProtoInstant.ResultCodeEnum.SUCCESS;
        ProtoMsg.Message response = loginResponceBuilder.loginResponse(resultCodeEnum, seqNo, session.getSessionId());
        session.writeAndFlush(response);
        return true;
    }

    private boolean checkUser(UserDTO user)
    {
        // chech auth, cost >= 100ms
        // method 1：use restful api
        // method 2：user database api

//        List<ServerSession> l = sessionManger.getSessionsBy(user.getUserId());
//
//
//        if (null != l && l.size() > 0)
//        {
//            return false;
//        }

        return true;
    }
}
