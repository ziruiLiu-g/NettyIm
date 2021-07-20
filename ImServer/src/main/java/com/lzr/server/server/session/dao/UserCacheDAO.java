package com.lzr.server.server.session.dao;

import com.lzr.server.server.session.entity.SessionCache;
import com.lzr.server.server.session.entity.UserCache;

/**
 * UserCacheDAO
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
public interface UserCacheDAO {
    void save(UserCache s);

    UserCache get(String userId);
    
    void addSession(String uid, SessionCache session);
    
    void removeSession(String uid, String sessionId);
}
