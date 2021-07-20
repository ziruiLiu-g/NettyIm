package com.lzr.server.server.session.entity;

import com.lzr.entity.ImNode;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * SessionCache
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Data
@Builder
public class SessionCache implements Serializable {
    private static final long serialVersionUID = -4652479744642665150L;
    
    private String userId;
    
    private String sessionId;
    
    private ImNode imNode;
    
    public SessionCache() {
        userId = "";
        sessionId = "";
        imNode = new ImNode("unKnown", 0);
    }
    
    public SessionCache(String sessionId, String userId, ImNode node) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.imNode = node;   
    }
}
