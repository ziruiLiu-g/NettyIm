package com.lzr.server.server.session;

import com.lzr.constants.ServerConstants;
import com.lzr.im.common.bean.UserDTO;
import com.lzr.server.server.session.service.SessionManger;
import com.lzr.util.JsonUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * LocalSession
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Data
@Slf4j
public class LocalSession implements ServerSession {
    public static final AttributeKey<String> KEY_USER_ID =
            AttributeKey.valueOf("key_user_id");

    public static final AttributeKey<LocalSession> SESSION_KEY =
            AttributeKey.valueOf("SESSION_KEY");
    
    private Channel channel;
    
    private UserDTO user;
    
    private final String sessionId;
    
    private boolean isLogin = false;

    /**
     * store session attributes
     */
    private Map<String, Object> map = new HashMap<>();
    
    public LocalSession(Channel channel) {
        this.channel = channel;
        this.sessionId = buildNewSessionId();
    }

    public static LocalSession getSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        return channel.attr(LocalSession.SESSION_KEY).get();
    }
    
    // session and channel bind each other
    public LocalSession bind() {
        log.info(" LocalSession bind " + channel.remoteAddress());
        channel.attr(LocalSession.SESSION_KEY).set(this);
        channel.attr(ServerConstants.CHANNEL_NAME).set(JsonUtil.pojoToJson(user));
        isLogin = true;
        return this;
    }

    public LocalSession unbind() {
        isLogin = false;
        SessionManger.instance().removeSession(getSessionId());
        this.close();
        return this;
    }

    private static String buildNewSessionId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }
    
    public synchronized void set(String key, Object value) {
        map.put(key, value);
    }

    public synchronized <T> T get(String key) {
        return (T) map.get(key);
    }
    
    @Override
    public void writeAndFlush(Object pkg) {
        if (channel.isWritable()) {
            channel.writeAndFlush(pkg);
        } else { 
            log.debug("channel busy, store msg");
            // store in distribute storage system, like mongo
            // send out when available
        }
    }

    public synchronized void close() {
        // user offline notify other nodes
        ChannelFuture future = channel.close();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    log.error("CHANNEL_CLOSED error ");
                }
            }
        });
    }

    public synchronized void writeAndClose(Object pkg) {
        channel.writeAndFlush(pkg);
        close();
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
        user.setSessionId(sessionId);
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public boolean isValid() {
        return getUser() != null ? true : false;
    }

    @Override
    public String getUserId() {
        return user.getUserId();
    }
}
