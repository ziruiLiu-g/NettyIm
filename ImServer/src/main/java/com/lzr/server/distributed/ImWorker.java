package com.lzr.server.distributed;

import com.lzr.constants.ServerConstants;
import com.lzr.entity.ImNode;
import com.lzr.util.JsonUtil;
import com.lzr.zk.CuratorZKclient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * ImWorker
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Data
@Slf4j
public class ImWorker {
    // zk client
    private CuratorFramework client = null;

    // store znode path
    private String pathRegistered = null;

    //imnode
    private ImNode localNode = null;

    private static ImWorker singletonInstance = null;

    private boolean inited = false;

    // get single
    public synchronized static ImWorker getInstance() {
        if (null == singletonInstance) {
            singletonInstance = new ImWorker();
            singletonInstance.localNode = new ImNode();
        }
        return singletonInstance;
    }

    private ImWorker() {}

    // create temporary node in zk
    public synchronized void init() {
        if (inited) {
            return;
        }
        inited = true;
        if (null == client) {
            this.client = CuratorZKclient.instance.getClient();
        }

        if (null == localNode) {
            localNode = new ImNode();
        }

        createParentIfNeeded(ServerConstants.MANAGE_PATH);

        try {
            // payload here is the localNode info
            byte[] payload = JsonUtil.object2JsonBytes(localNode);

            pathRegistered = client.create()
                .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(ServerConstants.PATH_PREFIX, payload);

            localNode.setId(getId());
            log.info("localnode path={}, id={}", pathRegistered, localNode.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLocalNode(String ip, int port) {
        localNode.setHost(ip);
        localNode.setPort(port);
    }

    public long getId() {
        return getIdByPath(pathRegistered);
    }

    /**
     * getIdByPath
     *
     * @param pathRegistered pathRegistered
     * @return id
     */
    public long getIdByPath(String pathRegistered) {
        String sid = null;
        if (null ==  pathRegistered) {
            throw new RuntimeException("node error");
        }

        int index = pathRegistered.lastIndexOf(ServerConstants.PATH_PREFIX);
        if (index >= 0) {
            index += ServerConstants.PATH_PREFIX.length();
            sid = index <= pathRegistered.length() ? pathRegistered.substring(index) : null;
        }

        if (null == sid) {
            throw new RuntimeException("can not get node id");
        }

        return Long.parseLong(sid);
    }

    /**
     * add load balance when login
     *
     * @return true/false
     */
    public boolean incBalance() {
        if (null == localNode) {
            throw new RuntimeException("node not set");
        }

        while (true) {
            try {
                localNode.incrementBalance();
                byte[] payload = JsonUtil.object2JsonBytes(localNode);
                client.setData().forPath(pathRegistered, payload);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * minus load balance when login
     *
     * @return true/false
     */
    public boolean decrBalance() {
        if (null == localNode) {
            throw new RuntimeException("node not set");
        }

        while (true) {
            try {
                localNode.decrementBalance();
                byte[] payload = JsonUtil.object2JsonBytes(localNode);
                client.setData().forPath(pathRegistered, payload);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * getLocalNodeInfo
     *
     * @return ImNode
     */
    public ImNode getLocalNodeInfo() {return localNode;}

    /**
     * 建立zk父节点
     *
     * @param managePath managePath
     */
    private void createParentIfNeeded(String managePath) {
        try {
            Stat stat = client.checkExists().forPath(managePath);
            if (null == stat) {
                client.create()
                        .creatingParentsIfNeeded()
                        .withProtection()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(managePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
