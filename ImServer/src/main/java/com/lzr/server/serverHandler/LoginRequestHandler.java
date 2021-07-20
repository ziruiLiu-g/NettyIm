package com.lzr.server.serverHandler;

import com.lzr.concurrent.CallbackTask;
import com.lzr.concurrent.CallbackTaskScheduler;
import com.lzr.im.common.bean.msg.ProtoMsg;
import com.lzr.server.server.session.LocalSession;
import com.lzr.server.server.session.service.SessionManger;
import com.lzr.server.serverProcesser.LoginProcesser;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * LoginRequestHandler
 * <p>
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Slf4j
@Service("LoginRequestHandler")
@ChannelHandler.Sharable
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    LoginProcesser loginProcesser;

    @Autowired
    private ChatRedirectHandler chatRedirectHandler;

    /**
     * recv msg
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = pkg.getType();
        if (!headType.equals(loginProcesser.op()))
        {
            super.channelRead(ctx, msg);
            return;
        }

        LocalSession session = new LocalSession(ctx.channel());
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {

            @Override
            public Boolean execute() throws Exception {
                return loginProcesser.action(session, pkg);
            }

            @Override
            public void onBack(Boolean r) {
                if (r) {
                    log.info("login!: {}", session.getUser());
                    
                    ctx.pipeline().addAfter("login", "chat", chatRedirectHandler);
                    ctx.pipeline().addAfter("login", "heartBeat", new HeartBeatServerHandler());
                    ctx.pipeline().remove("login");
                } else {
                    SessionManger.instance().closeSession(ctx);
                    
                    log.info("login fail: {}", session.getUser());
                }
            }

            @Override
            public void onException(Throwable t)
            {
                t.printStackTrace();
                log.info("login fail: {}", session.getUser());
                SessionManger.instance().closeSession(ctx);
            }
        });
    }
}
