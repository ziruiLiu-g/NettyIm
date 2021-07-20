package com.lzr.zk;

import com.lzr.util.SpringContextUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * zk ClientFactory
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
@Slf4j
@Data
public class CuratorZKclient {
    private final String zkSessionTimeout;
    private CuratorFramework client;


    //Zk cluster addr
    private String zkAddress = "127.0.0.1:2181";
    public static CuratorZKclient instance = null;



    private static CuratorZKclient singleton = null;

    public static CuratorZKclient getSingleton()
    {
        if (null == singleton)
        {
            singleton = SpringContextUtil.getBean("curatorZKClient");

        }
        return singleton;
    }

    public CuratorZKclient(String zkConnect, String zkSessionTimeout)
    {
        this.zkAddress = zkConnect;
        this.zkSessionTimeout = zkSessionTimeout;
        init();
    }

    public void init()
    {

        if (null != client)
        {
            return;
        }
        // create zkcleint
        client = ClientFactory.createSimple(zkAddress,zkSessionTimeout);

        // start client
        client.start();

        instance = this;
    }

    public void destroy()
    {
        CloseableUtils.closeQuietly(client);
    }


    /**
     * creat node
     *
     * @param zkPath zkpath
     * @param data init data
     */
    public void createNode(String zkPath, String data)
    {
        try
        {
            byte[] payload = "to set content".getBytes("UTF-8");
            if (data != null)
            {
                payload = data.getBytes("UTF-8");
            }
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(zkPath, payload);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * del node
     *
     * @param zkPath node path
     */
    public void deleteNode(String zkPath)
    {
        try
        {
            if (!isNodeExist(zkPath))
            {
                return;
            }
            client.delete()
                    .forPath(zkPath);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * check if exists
     *
     * @param zkPath node path
     * @return true/false
     */
    public boolean isNodeExist(String zkPath)
    {
        try
        {

            Stat stat = client.checkExists().forPath(zkPath);
            if (null == stat)
            {
                log.info("node does not exists:", zkPath);
                return false;
            } else
            {

                log.info("node stat is:", stat.toString());
                return true;

            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * create ephemeral seq node
     *
     * @param srcpath source path
     * @return path
     */
    public String createEphemeralSeqNode(String srcpath)
    {
        try
        {

            // 创建一个 ZNode 节点
            String path = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(srcpath);

            return path;

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
