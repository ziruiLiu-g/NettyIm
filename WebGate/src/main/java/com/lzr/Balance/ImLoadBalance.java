package com.lzr.Balance;

import com.lzr.constants.ServerConstants;
import com.lzr.entity.ImNode;
import com.lzr.util.JsonUtil;
import com.lzr.zk.CuratorZKclient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ImLoadBalance
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Data
@Slf4j
public class ImLoadBalance {
    //zk client
    private CuratorFramework client = null;
    private String managerPath;

    public ImLoadBalance(CuratorZKclient curatorZKclient) {
        this.client = curatorZKclient.getClient();
        managerPath = ServerConstants.MANAGE_PATH;
    }

    /**
     * get the node that have least load
     *
     * @return best worker
     */
    public ImNode getBestWorker() {
        List<ImNode> workers = getWorkers();

        log.info("all workers: ");
        workers.stream().forEach(node -> {
            log.info("node info: {}", JsonUtil.pojoToJson(node));
        });

        ImNode best = balance(workers);
        return best;
    }

    /**
     * get all workers
     *
     * @return workers list
     */
    public List<ImNode> getWorkers() {
        List<ImNode> workers = new ArrayList<>();

        List<String> children = null;

        try {
            children = client.getChildren().forPath(managerPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        for (String child : children) {
            log.info("child: {}", child);
            byte[] payload = null;

            try {
                payload = client.getData().forPath(managerPath + "/" + child);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (null == payload) {
                continue;
            }
            ImNode node = JsonUtil.jsonBytes2Object(payload, ImNode.class);
            node.setId(getIdByPath(child));

            workers.add(node);
        }
        return workers;
    }

    /**
     * get id by path
     * @param path path
     * @return id
     */
    public long getIdByPath(String path) {
        String sid = null;

        if (null == path) {
            throw new RuntimeException("node path error");
        }

        int index = path.lastIndexOf(ServerConstants.PATH_PREFIX_NO_STRIP);
        if (index >= 0) {
            index += ServerConstants.PATH_PREFIX_NO_STRIP.length();
            sid = index <= path.length() ? path.substring(index) : null;
        }

        if (null == sid) {
            throw new RuntimeException("can not get node id");
        }

        return Long.parseLong(sid);
    }

    /**
     * sort workers by load
     *
     * @param workers workers
     * @return node that has least load
     */
    protected ImNode balance(List<ImNode> workers) {
        if (workers.size() > 0) {
            Collections.sort(workers);

            ImNode node = workers.get(0);

            log.info("best node: {}", JsonUtil.pojoToJson(node));
            return node;
        } else {
            return null;
        }
    }

    /**
     * del all im node from zk
     */
    public void removeWorkers()
    {
        try
        {
            client.delete().deletingChildrenIfNeeded().forPath(managerPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
