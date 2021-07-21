package com.lzr.client.client;

import com.lzr.im.common.bean.UserDTO;
import com.lzr.im.common.bean.msg.ProtoMsg;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * ClientSession
 *
 * Author: zirui liu
 * Date: 2021/7/21
 */
@Slf4j
@Data
public class ClientSession {
    public static final AttributeKey<ClientSession> SESSION_KEY =
            AttributeKey.valueOf("SESSION_KEY");
    
    private Channel channel;
    
    private UserDTO user;

    /**
     * session after login
     */
    private String sessionId;

    private boolean isConnected = false;
    
    private boolean isLogin = false;

    /**
     * session
     */
    private Map<String, Object> map = new HashMap<String, Object>();

    public ClientSession(Channel channel)
    {
        this.channel = channel;
        this.sessionId = String.valueOf(-1);
        channel.attr(ClientSession.SESSION_KEY).set(this);
    }
    
    public static void loginSuccess(ChannelHandlerContext ctx, ProtoMsg.Message pkg) {
        ClientSession session = getSession(ctx);
        session.setSessionId(pkg.getSessionId());
        session.setLogin(true);
        log.info("login success");
    }

    public static ClientSession getSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        ClientSession session = channel.attr(ClientSession.SESSION_KEY).get();
        return session;
    }

    public String getRemoteAddress()
    {
        return channel.remoteAddress().toString();
    }

    public ChannelFuture witeAndFlush(Object pkg) {
        ChannelFuture f = channel.writeAndFlush(pkg);
        return f;
    }

    public void writeAndClose(Object pkg) {
        ChannelFuture future = channel.writeAndFlush(pkg);
        future.addListener(ChannelFutureListener.CLOSE);
    }

    public void close() {
        isConnected = false;

        ChannelFuture future = channel.close();
        future.addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                log.error("conn closed");
            }
        });
    }
}
