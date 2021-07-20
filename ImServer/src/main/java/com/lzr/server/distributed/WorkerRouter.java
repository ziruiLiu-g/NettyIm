package com.lzr.server.distributed;

import com.lzr.constants.ServerConstants;
import com.lzr.entity.ImNode;
import com.lzr.im.common.bean.msg.ProtoMsg;
import com.lzr.server.protoBuilder.NotificationMsgBuilder;
import com.lzr.util.JsonUtil;
import com.lzr.util.ObjectUtil;
import com.lzr.zk.CuratorZKclient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * WorkerRouter
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Data
@Slf4j
public class WorkerRouter {
    private CuratorFramework client = null;

    private String pathRegistered = null;
    
    private ImNode node = null;

    private boolean inited=false;
    
    private static WorkerRouter singletonInstance = null;
    
    private static final String path = ServerConstants.MANAGE_PATH;
    
    private ConcurrentHashMap<Long, PeerSender> workerMap = new ConcurrentHashMap<>();
    
    private BiConsumer<ImNode, PeerSender> runAfterAdd = (node, relaySender) -> {
        doAfterAdd(node, relaySender);
    };
    
    private Consumer<ImNode> runAfterRemove = (node) -> {
        doAfterRemove(node);
    };

    public synchronized static WorkerRouter getInstance() {
        if (null == singletonInstance) {
            singletonInstance = new WorkerRouter();
        }
        return singletonInstance;
    }

    private WorkerRouter() { }

    /**
     * init node
     */
    public void init() {
        if (inited) {
            return;
        }
        inited = true;
        
        try {
            if (null == client) {
                client = CuratorZKclient.instance.getClient();
            }
            
            // subscribe
            PathChildrenCache childrenCache = new PathChildrenCache(client, path, true);
            PathChildrenCacheListener childrenCacheListener = (client, event) -> {
                log.info("begin to listen other nodes");
                ChildData data = event.getData();
                
                switch (event.getType()) {
                    case CHILD_ADDED:
                        log.info("CHILD_ADDED : " + data.getPath() + "  data:" + data.getData());
                        processNodeAdded(data);
                        break;
                    case CHILD_REMOVED:
                        log.info("CHILD_REMOVED : " + data.getPath() + "  data:" + data.getData());
                        processNodeRemoved(data);
                        break;
                    case CHILD_UPDATED:
                        log.info("CHILD_UPDATED : " + data.getPath() + "  data:" + new String(data.getData()));
                        break;
                    default:
                        log.debug("[PathChildrenCache]node empty, path={}", data == null ? "null" : data.getPath());
                        break;
                }
            };
            
            childrenCache.getListenable().addListener(childrenCacheListener);
            log.info("Register zk watcher successfully!");
            childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * action when receive del node msg
     * 
     * @param data data
     */
    private void processNodeRemoved(ChildData data) {
        byte[] payload = data.getData();
        ImNode node = ObjectUtil.JsonBytes2Object(payload, ImNode.class);
        
        long id = ImWorker.getInstance().getIdByPath(data.getPath());
        node.setId(id);
        
        log.info("[Cache] node del, path={}, data={}", data.getData(), JsonUtil.pojoToJson(node));
        
        if (runAfterRemove != null) {
            runAfterRemove.accept(node);
        }
    }
    
    private void doAfterRemove(ImNode node) {
        PeerSender peerSender = workerMap.get(node.getId());

        if (null != peerSender) {
            peerSender.stopConnecting();
            workerMap.remove(node.getId());
        }
    }

    /**
     * action when receive add node msg
     *
     * @param data data
     */
    private void processNodeAdded(ChildData data) {
        byte[] payload = data.getData();
        ImNode node = ObjectUtil.JsonBytes2Object(payload, ImNode.class);

        long id = ImWorker.getInstance().getIdByPath(data.getPath());
        node.setId(id);
        
        log.info("[Cache] node port update, path={}, data={}", data.getPath(), JsonUtil.pojoToJson(node));
        
        if (node.equals(getLocalNode())) {
            log.info("[Cache] new node is the localnode");
            return;
        }

        PeerSender relaySender = workerMap.get(node.getId());
        // repeated msg
        if (null != relaySender && relaySender.getRmNode().equals(node)) {
            log.info("[Cache] node msg repeated, path={}, data={}",
                    data.getPath(), JsonUtil.pojoToJson(node));
            return;
        }
        
        if (runAfterAdd != null) {
            runAfterAdd.accept(node, relaySender);
        }
    }

    private void doAfterAdd(ImNode n, PeerSender relaySender) {
        if (null != relaySender) {
            // close old conn
            relaySender.stopConnecting();
        }
        
        // create sender
        relaySender = new PeerSender(n);
        // create sender connection
        relaySender.doConnect();

        workerMap.put(n.getId(), relaySender);
    }

    public PeerSender route(long nodeId) {
        PeerSender peerSender = workerMap.get(nodeId);
        
        if (null != peerSender) {
            return peerSender;
        }
        return null;
    }

    public ImNode getLocalNode() {
        return ImWorker.getInstance().getLocalNodeInfo();
    }

    public void sendNotification(String json) {
        workerMap.keySet().stream().forEach(
                key ->
                {
                    if (!key.equals(getLocalNode().getId())) {
                        PeerSender peerSender = workerMap.get(key);
                        ProtoMsg.Message pkg = NotificationMsgBuilder.buildNotification(json);
                        peerSender.writeAndFlush(pkg);
                    }
                }
        );

    }
}
