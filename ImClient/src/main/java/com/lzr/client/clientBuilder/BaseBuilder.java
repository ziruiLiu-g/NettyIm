package com.lzr.client.clientBuilder;

import com.lzr.client.client.ClientSession;
import com.lzr.im.common.bean.msg.ProtoMsg;

/**
 * BaseBuilder
 *
 * Author: zirui liu
 * Date: 2021/7/21
 */
public class BaseBuilder {
    protected ProtoMsg.HeadType type;
    
    private long seqId;
    
    private ClientSession session;
    
    public BaseBuilder(ProtoMsg.HeadType type, ClientSession session) {
        this.type = type;
        this.session = session;
    }
    
    public ProtoMsg.Message buildCommon(long seqId) {
        this.seqId = seqId;
        
        ProtoMsg.Message.Builder mb = ProtoMsg.Message
                .newBuilder()
                .setType(type)
                .setSessionId(session.getSessionId())
                .setSequence(seqId);
        
        return mb.buildPartial();
    }
}
