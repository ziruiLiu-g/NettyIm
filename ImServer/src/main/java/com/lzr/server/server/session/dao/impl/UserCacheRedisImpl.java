package com.lzr.server.server.session.dao.impl;

import com.lzr.server.server.session.dao.UserCacheDAO;
import com.lzr.server.server.session.entity.SessionCache;
import com.lzr.server.server.session.entity.UserCache;
import com.lzr.util.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * UserCacheRedisImpl
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Repository("UserCacheRedisImpl")
public class UserCacheRedisImpl implements UserCacheDAO {
    public static final String REDIS_PREFIX = "UserCache:uid:";
    
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;
    
    private static final long CASHE_LONG = 60 * 4;
    
    @Override
    public void save(final UserCache s) {
        String key = REDIS_PREFIX + s.getUserId();
        String value  = JsonUtil.pojoToJson(s);
        stringRedisTemplate.opsForValue().set(key, value, CASHE_LONG, TimeUnit.MINUTES);
    }

    @Override
    public UserCache get(final String userId) {
        String key = REDIS_PREFIX + userId;
        String value  = (String) stringRedisTemplate.opsForValue().get(key);
        
        if (!StringUtils.isEmpty(value)) {
            return JsonUtil.jsonToPojo(value, UserCache.class);
        }
        return null;
    }

    @Override
    public void addSession(String uid, SessionCache session) {
        UserCache cache = get(uid);
        if (null == cache) {
            cache = new UserCache(uid);
        }

        cache.addSession(session);
        save(cache);
    }

    @Override
    public void removeSession(String uid, String sessionId) {
        UserCache cache = get(uid);
        if (null == cache) {
            cache = new UserCache(uid);
        }

        cache.removeSession(sessionId);
        save(cache);
    }
}
