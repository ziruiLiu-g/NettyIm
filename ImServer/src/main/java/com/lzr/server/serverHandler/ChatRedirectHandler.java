package com.lzr.server.serverHandler;

import com.lzr.concurrent.FutureTaskScheduler;
import com.lzr.im.common.bean.msg.ProtoMsg;
import com.lzr.server.server.session.LocalSession;
import com.lzr.server.server.session.ServerSession;
import com.lzr.server.server.session.service.SessionManger;
import com.lzr.server.serverProcesser.ChatRedirectProcesser;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ChatRedirectHandler
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Slf4j
@Service("ChatRedirectHandler")
@ChannelHandler.Sharable
public class ChatRedirectHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    ChatRedirectProcesser redirectProcesser;

    @Autowired
    SessionManger sessionManger;

    /**
     * recv msg
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof ProtoMsg.Message))
        {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = ((ProtoMsg.Message) msg).getType();
        if (!headType.equals(redirectProcesser.op()))
        {
            super.channelRead(ctx, msg);
            return;
        }
        
        // redirect msg in async way
        FutureTaskScheduler.add(() -> {
            LocalSession session = LocalSession.getSession(ctx);
            // already login, msg is user msg
            if (null != session && session.isLogin()) {
                redirectProcesser.action(session, pkg);
                return;
            }
            
            // not login yet, msg is redirect msg
            ProtoMsg.MessageRequest request = pkg.getMessageRequest();
            List<ServerSession> toSessions = SessionManger.instance().getSessionBy(request.getTo());
            toSessions.forEach((serverSession) -> {
                if (serverSession instanceof LocalSession) {
                    serverSession.writeAndFlush(pkg);
                }
            });
        });
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LocalSession session = ctx.channel().attr(LocalSession.SESSION_KEY).get();
        
        if (null != session && session.isValid()) {
            session.close();
            sessionManger.removeSession(session.getSessionId());
        }
    }
}
