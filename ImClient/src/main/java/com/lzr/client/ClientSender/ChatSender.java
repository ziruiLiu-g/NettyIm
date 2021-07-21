package com.lzr.client.ClientSender;

import com.lzr.client.clientBuilder.ChatMsgBuilder;
import com.lzr.im.common.bean.ChatMsg;
import com.lzr.im.common.bean.msg.ProtoMsg;
import com.lzr.util.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ChatSender
 *
 * Author: zirui liu
 * Date: 2021/7/21
 */
@Slf4j
@Service("ChatSender")
public class ChatSender extends BaseSender {
    public void sendChatMsg(String touid, String content) {
        ChatMsg chatMsg = new ChatMsg(getUser());
        chatMsg.setContent(content);
        chatMsg.setMsgType(ChatMsg.MSGTYPE.TEXT);
        chatMsg.setTo(touid);
        chatMsg.setMsgId(System.currentTimeMillis());
        ProtoMsg.Message message = ChatMsgBuilder.buildChatMsg(chatMsg, getUser(), getSession());
        
        super.sendMsg(message);
    }
    
    @Override
    protected void sendSucced(ProtoMsg.Message message) {
        Logger.tcfo("send succeed:"
                + message.getMessageRequest().getContent()
                + "->"
                + message.getMessageRequest().getTo());
    }

    @Override
    protected void sendException(ProtoMsg.Message message)
    {
        Logger.tcfo("send error:"
                + message.getMessageRequest().getContent()
                + "->"
                + message.getMessageRequest().getTo());
//        commandClient.notifyCommandThread();
    }

    @Override
    protected void sendfailed(ProtoMsg.Message message)
    {
        Logger.tcfo("send failed:"
                + message.getMessageRequest().getContent()
                + "->"
                + message.getMessageRequest().getTo());
//        commandClient.notifyCommandThread();
    }
}
