package com.lzr.server.protoBuilder;

import com.lzr.im.common.bean.msg.ProtoMsg;

/**
 * NotificationMsgBuilder
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
public class NotificationMsgBuilder {
    public static ProtoMsg.Message buildNotification(String json)
    {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.MESSAGE_NOTIFICATION);

        ProtoMsg.MessageNotification.Builder rb =
                ProtoMsg.MessageNotification.newBuilder()
                        .setJson(json);
        mb.setNotification(rb.build());
        return mb.build();
    }
}
