package com.lzr.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * zk ClientFactory
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
public class ClientFactory {
    /**
     * create in simple way
     *
     * @param connectionString zk addr
     * @return CuratorFramework instance
     */
    public static CuratorFramework createSimple(String connectionString,String timeout)
    {
        // retry, first retry wait 1s, second 2s, third 4s
        ExponentialBackoffRetry retryPolicy =
                new ExponentialBackoffRetry(1000, 3);

        // arg2: session timeout  arg3: connection timeout
        return CuratorFrameworkFactory.newClient(connectionString,
                Integer.parseInt(timeout)  ,  Integer.parseInt(timeout)  , retryPolicy);

    }

    /**
     * create with options
     *
     * @param connectionString zk addr
     * @param retryPolicy retry policy
     * @param connectionTimeoutMs con timeout
     * @param sessionTimeoutMs session timeout
     * @return CuratorFramework instance
     */
    public static CuratorFramework createWithOptions(
            String connectionString, RetryPolicy retryPolicy,
            int connectionTimeoutMs, int sessionTimeoutMs)
    {

        return CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(connectionTimeoutMs)
                .sessionTimeoutMs(sessionTimeoutMs)
                .build();
    }
}
