package com.lzr.server.serverProcesser;

import com.lzr.im.common.bean.msg.ProtoMsg;
import com.lzr.server.server.session.LocalSession;
import com.lzr.server.server.session.ServerSession;
import com.lzr.server.server.session.service.SessionManger;
import com.lzr.util.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ChatRedirectProcesser
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Slf4j
@Service("ChatRedirectProcesser")
public class ChatRedirectProcesser extends AbstractServerProcesser {
    public static final int RE_DIRECT = 1;

    @Override
    public ProtoMsg.HeadType op() {
        return  ProtoMsg.HeadType.MESSAGE_REQUEST;
    }

    @Override
    public Boolean action(LocalSession fromSession, ProtoMsg.Message proto) {
        ProtoMsg.MessageRequest messageRequest = proto.getMessageRequest();
        Logger.tcfo("chatMsg | from="
                + messageRequest.getFrom()
                + " , to =" + messageRequest.getTo()
                + " , MsgType =" + messageRequest.getMsgType()
                + " , content =" + messageRequest.getContent());
        
        // get receiver chatId
        String to = messageRequest.getTo();

        List<ServerSession> toSession = SessionManger.instance().getSessionBy(to);
        if (toSession == null) {
            Logger.tcfo("[" + to + "] offline, please save msg to db, like mysql or mongo");
        } else {
            toSession.forEach((session) -> {
                session.writeAndFlush(proto);
            });
        }
        
        return null;
    }
}
