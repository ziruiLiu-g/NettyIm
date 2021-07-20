package com.lzr.server.protoBuilder;

import com.lzr.im.common.ProtoInstant;
import com.lzr.im.common.bean.msg.ProtoMsg;

/**
 * ChatMsgBuilder
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
public class ChatMsgBuilder {
    public static ProtoMsg.Message buildChatResponse(
            long seqId,
            ProtoInstant.ResultCodeEnum en) {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.MESSAGE_NOTIFICATION)
                .setSequence(seqId);
        ProtoMsg.MessageResponse.Builder rb = ProtoMsg.MessageResponse
                .newBuilder()
                .setCode(en.getCode())
                .setInfo(en.getDesc())
                .setExpose(1);
        mb.setMessageResponse(rb.build());

        return mb.build();
    }

    public static ProtoMsg.Message  buildLoginResponse(
            ProtoInstant.ResultCodeEnum en,
            long seqId
    ) {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.MESSAGE_RESPONSE)
                .setSequence(seqId);

        ProtoMsg.LoginResponse.Builder rb = ProtoMsg.LoginResponse.newBuilder()
                .setCode(en.getCode())
                .setInfo(en.getDesc())
                .setExpose(1);

        mb.setLoginResponse(rb.build());

        return mb.build();
    }
}
