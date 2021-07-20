package com.lzr.server.server.session;

/**
 * ServerSession
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
public interface ServerSession {
    void writeAndFlush(Object pkg);

    String getSessionId();

    boolean isValid();

    /**
     * 获取用户id
     * @return  用户id
     */
    String getUserId();
}
