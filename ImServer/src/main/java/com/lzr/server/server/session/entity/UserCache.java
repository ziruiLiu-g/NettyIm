package com.lzr.server.server.session.entity;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * UserCache
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Data
public class UserCache {
    private String userId;
    
    private Map<String, SessionCache> map = new LinkedHashMap<>(10);
    
    public UserCache(String userId) {
        this.userId = userId;
    }
    
    public void addSession(SessionCache sessionCache) {
        map.put(sessionCache.getSessionId(), sessionCache);
    }
    
    public void removeSession(String sessionId) {
        map.remove(sessionId);
    }
}
