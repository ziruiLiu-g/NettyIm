package com.lzr.server.server.session.service;

import com.lzr.entity.ImNode;
import com.lzr.server.distributed.PeerSender;
import com.lzr.server.distributed.WorkerRouter;
import com.lzr.server.server.session.ServerSession;
import com.lzr.server.server.session.entity.SessionCache;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * RemoteSession
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Data
@Builder
@AllArgsConstructor
public class RemoteSession implements ServerSession, Serializable {
    private static final long serialVersionUID = 546354235103802640L;
    
    SessionCache cache;

    private boolean valid = true;
    
    public RemoteSession(SessionCache cache)
    {
        this.cache = cache;
    }
    
    @Override
    public void writeAndFlush(Object pkg) {
        ImNode imNode = cache.getImNode();
        long nodeId = imNode.getId();

        PeerSender sender = WorkerRouter.getInstance().route(nodeId);
        
        if (null != sender) {
            sender.writeAndFlush(pkg);
        }
    }

    @Override
    public String getSessionId() {
        return cache.getSessionId();
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid)
    {
        this.valid = valid;
    }

    @Override
    public String getUserId() {
        return cache.getUserId();
    }
}
