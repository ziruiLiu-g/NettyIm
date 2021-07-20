package com.lzr.server.serverProcesser;

import com.lzr.im.common.bean.msg.ProtoMsg;
import com.lzr.server.server.session.LocalSession;

/**
 * ServerReciever
 */
public interface ServerReciever
{

    ProtoMsg.HeadType op();

    Boolean action(LocalSession ch, ProtoMsg.Message proto);

}
