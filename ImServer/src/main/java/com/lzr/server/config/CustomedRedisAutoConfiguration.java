package com.lzr.server.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * CustomedRedisAutoConfiguration
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Configuration
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class CustomedRedisAutoConfiguration {
    @Value("${spring.redis.maxTotal}")
    private int maxTotal;

    @Value("${spring.redis.maxIdle}")
    private int maxIdle;

    @Value("${spring.redis.maxWaitMillis}")
    private long maxWaitMillis;

    @Value("${spring.redis.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.connTimeout}")
    private int connTimeout;
    @Value("${spring.redis.readTimeout}")
    private int readTimeout;

    @Value("${spring.redis.password:}")
    private String password;

    @Value("${spring.redis.database}")
    private int database;

    @Value("${spring.redis.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.redis.softMinEvictableIdleTimeMillis}")
    private int softMinEvictableIdleTimeMillis;

    @Value("${spring.redis.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.redis.numTestsPerEvictionRun}")
    private int numTestsPerEvictionRun;

    @Value("${spring.redis.blockWhenExhausted}")
    private boolean blockWhenExhausted;

    @Value("${spring.redis.testWhileIdle}")
    private boolean testWhileIdle;

    /**
     * build config pool
     *
     * @return pool config
     */
    @Bean
    public JedisPoolConfig poolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        poolConfig.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
        poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        poolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        poolConfig.setBlockWhenExhausted(blockWhenExhausted);
        poolConfig.setTestWhileIdle(testWhileIdle);
        return poolConfig;
    }

    /**
     * get redis conn factory
     *
     * @param jedisPoolConfig jedisPoolConfig
     * @return factory
     */
    @Bean
    public RedisConnectionFactory connectionFactory(JedisPoolConfig jedisPoolConfig) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);

        if (StringUtils.isNotEmpty(password)) {
            configuration.setPassword(RedisPassword.of(password));
        }
        configuration.setDatabase(database);

        JedisClientConfiguration.DefaultJedisClientConfigurationBuilder builder =
                (JedisClientConfiguration.DefaultJedisClientConfigurationBuilder) JedisClientConfiguration.builder();

        builder.usePooling();
        builder.poolConfig(jedisPoolConfig);
        builder.connectTimeout(Duration.ofMillis(connTimeout));
        builder.readTimeout(Duration.ofMillis(readTimeout));

        JedisClientConfiguration clientConfiguration = builder.build();
        return new JedisConnectionFactory(configuration, clientConfiguration);
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        RedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // define redisTemplate
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();

        // key -> stringRedisSerializer
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(stringRedisSerializer);

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setEnableTransactionSupport(false);
        return redisTemplate;
    }

    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate(connectionFactory);
        RedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setDefaultSerializer(stringRedisSerializer);
        redisTemplate.setEnableTransactionSupport(false);
        return redisTemplate;
    }

    @Bean
    public CacheManager initRedisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
                .RedisCacheManagerBuilder.fromConnectionFactory(connectionFactory);
        return builder.build();
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                StringBuilder sb = new StringBuilder();
                sb.append(o.getClass().getName());
                sb.append(method.getName());
                for (Object obj : objects) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }
}
