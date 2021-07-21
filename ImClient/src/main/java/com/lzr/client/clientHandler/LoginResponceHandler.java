package com.lzr.client.clientHandler;

import com.lzr.client.client.ClientSession;
import com.lzr.client.client.CommandController;
import com.lzr.im.common.ProtoInstant;
import com.lzr.im.common.bean.msg.ProtoMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * LoginResponceHandler
 *
 * Author: zirui liu
 * Date: 2021/7/21
 */
@Slf4j
@ChannelHandler.Sharable
@Service("LoginResponceHandler")
public class LoginResponceHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    CommandController commandController;
    
    @Autowired
    HeartBeatClientHandler heartBeatClientHandler;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = ((ProtoMsg.Message) msg).getType();
        if (!headType.equals(ProtoMsg.HeadType.LOGIN_RESPONSE)) {
            super.channelRead(ctx, msg);
            return;
        }
        
        ProtoMsg.LoginResponse info = pkg.getLoginResponse();
        ProtoInstant.ResultCodeEnum resultCodeEnum = ProtoInstant.ResultCodeEnum.values()[info.getCode()];
        
        if (!resultCodeEnum.equals(ProtoInstant.ResultCodeEnum.SUCCESS)) {
            log.info(resultCodeEnum.getDesc());
            log.error("login to netty server failed");
        } else {
            ClientSession session = ctx.channel().attr(ClientSession.SESSION_KEY).get();
            session.setSessionId(pkg.getSessionId());
            session.setLogin(true);
            
            commandController.notifyCommandThread();
            
            ctx.channel().pipeline()
                    .addAfter("loginResponseHandler", "heartBeatClientHandler", heartBeatClientHandler);
            heartBeatClientHandler.channelActive(ctx);
            ctx.channel().pipeline().remove("loginResponseHandler");
        }
    }
}
