package com.lzr.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * sprint context tool, for bean and servletrequest
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
@Component
public class SpringContextUtil implements ApplicationContextAware
{

    /**
     * context instance
     */
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static void setContext(ApplicationContext applicationContext)
    {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    /**
     * get bean by name
     */
    public static <T> T getBean(String name)
    {
        return (T) applicationContext.getBean(name);
    }

    /**
     * get class by name
     */
    public static <T> T getBean(Class<T> clazz)
    {
        if (null == applicationContext)
        {
            return null;
        }
        return applicationContext.getBean(clazz);
    }

    /**
     * get bean by name and class
     */
    public static <T> T getBean(String name, Class<T> clazz)
    {
        return applicationContext.getBean(name, clazz);
    }


    /**
     * get node ip
     */
    public static String getLocalIP()
    {
        return applicationContext.getEnvironment()
                .getProperty("zookeeper.distribute.local-node-host", "127.0.0.1");
    }

}
