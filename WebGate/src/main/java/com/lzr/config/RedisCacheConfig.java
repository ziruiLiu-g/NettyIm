package com.lzr.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.cache.interceptor.KeyGenerator;

/**
 * RedisCacheConfig
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Slf4j
@Configuration
@EnableCaching
@ImportResource(locations = {"classpath:spring-redis.xml"})
public class RedisCacheConfig extends CachingConfigurerSupport {
    private volatile JedisConnectionFactory mJedisConnectionFactory;
    private volatile RedisTemplate<String, String> mRedisTemplate;
    private volatile RedisCacheManager mRedisCacheManager;

    public RedisCacheConfig() {super();}

    public RedisCacheConfig(JedisConnectionFactory mJedisConnectionFactory,
                            RedisTemplate<String, String> mRedisTemplate,
                            RedisCacheManager redisCacheManager) {
        super();
        this.mJedisConnectionFactory = mJedisConnectionFactory;
        this.mRedisTemplate = mRedisTemplate;
        this.mRedisCacheManager = redisCacheManager;
    }

    public JedisConnectionFactory redisConnectionFactory()
    {
        return mJedisConnectionFactory;
    }

    public RedisTemplate<String, String> redisTemplate(
            RedisConnectionFactory cf)
    {
        return mRedisTemplate;
    }

    public CacheManager cacheManager(RedisTemplate<?, ?> redisTemplate)
    {
        return mRedisCacheManager;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return (o, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(o.getClass().getName());
            sb.append(method.getName());
            for (Object obj : objects)
            {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }
}
