package com.lzr.server.server.session.dao;

import com.lzr.server.server.session.entity.SessionCache;

/**
 * SessionCacheDAO
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
public interface SessionCacheDAO {
    void save(SessionCache s);

    SessionCache get(String sessionId);

    void remove(String sessionId);
}
