package com.lzr.config;

import com.lzr.Balance.ImLoadBalance;
import com.lzr.util.SpringContextUtil;
import com.lzr.zk.CuratorZKclient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ZKClientConfig
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Configuration
public class ZKClientConfig implements ApplicationContextAware {
    @Value("${zookeeper.connect.url}")
    private String zkConnect;

    @Value("${zookeeper.connect.SessionTimeout}")
    private String zkSessionTimeOut;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.setContext(applicationContext);
    }

    @Bean(name = "curatorZKClient")
    public CuratorZKclient curatorZKclient() {
        return new CuratorZKclient(zkConnect, zkSessionTimeOut);
    }

    @Bean(name = "imLoadBalance")
    public ImLoadBalance imLoadBalance(CuratorZKclient curatorZKclient) {
        return new ImLoadBalance(curatorZKclient);
    }
}
