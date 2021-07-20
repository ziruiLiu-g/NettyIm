package com.lzr.server.server.session.dao.impl;

import com.lzr.server.server.session.dao.SessionCacheDAO;
import com.lzr.server.server.session.entity.SessionCache;
import com.lzr.util.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * SessionCacheRedisImpl
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Repository("SessionCacheRedisImpl")
public class SessionCacheRedisImpl implements SessionCacheDAO {
    public static final String REDIS_PREFIX = "SessionCache:id:";
    
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;
    
    private static final long CASHE_LONG = 60 * 4;
    
    @Override
    public void save(SessionCache s) {
        String key = REDIS_PREFIX + s.getSessionId();
        String value = JsonUtil.pojoToJson(s);
        stringRedisTemplate.opsForValue().set(key, value, CASHE_LONG, TimeUnit.MINUTES);
    }

    @Override
    public SessionCache get(String sessionId) {
        String key = REDIS_PREFIX + sessionId;
        String value = (String) stringRedisTemplate.opsForValue().get(key);
        
        if (!StringUtils.isEmpty(value)) {
            return JsonUtil.jsonToPojo(value, SessionCache.class);
        }
        return null;
    }

    @Override
    public void remove(String sessionId) {
        String key = REDIS_PREFIX + sessionId;
        stringRedisTemplate.delete(key);
    }
}
