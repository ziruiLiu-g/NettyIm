package com.lzr.server.serverProcesser;

import com.lzr.im.common.bean.msg.ProtoMsg;

import java.util.HashMap;
import java.util.Map;

/**
 * ProcFactory
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
public class ProcFactory {
    private static ProcFactory instance;
    
    public static Map<ProtoMsg.HeadType, ServerReciever> factory
            = new HashMap<>();
    
    static {
        instance = new ProcFactory();
    }
    
    private ProcFactory() {
        try {
            ServerReciever proc = new LoginProcesser();
            factory.put(proc.op(), proc);
            
            proc = new ChatRedirectProcesser();
            factory.put(proc.op(), proc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ProcFactory getInstance()
    {
        return instance;
    }

    public ServerReciever getOperation(ProtoMsg.HeadType type)
    {
        return factory.get(type);
    }
}
