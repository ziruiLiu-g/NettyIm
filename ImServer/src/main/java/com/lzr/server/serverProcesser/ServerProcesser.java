package com.lzr.server.serverProcesser;

import com.lzr.im.common.bean.msg.ProtoMsg;
import com.lzr.server.server.session.LocalSession;

/**
 * ServerProcesser
 */
public interface ServerProcesser
{

    ProtoMsg.HeadType type();

    boolean action(LocalSession ch, ProtoMsg.Message proto);

}
