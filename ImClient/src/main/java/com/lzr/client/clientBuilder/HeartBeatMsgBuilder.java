package com.lzr.client.clientBuilder;

import com.lzr.client.client.ClientSession;
import com.lzr.im.common.bean.UserDTO;
import com.lzr.im.common.bean.msg.ProtoMsg;

/**
 * HeartBeatMsgBuilder
 *
 * Author: zirui liu
 * Date: 2021/7/21
 */
public class HeartBeatMsgBuilder extends BaseBuilder {
    private final UserDTO user;

    public HeartBeatMsgBuilder(UserDTO user, ClientSession session) {
        super(ProtoMsg.HeadType.HEART_BEAT, session);
        this.user = user;
    }

    public ProtoMsg.Message buildMsg() {
        ProtoMsg.Message message = buildCommon(-1);
        ProtoMsg.MessageHeartBeat.Builder lb =
                ProtoMsg.MessageHeartBeat.newBuilder()
                        .setSeq(0)
                        .setJson("{\"from\":\"client\"}")
                        .setUid(user.getUserId());
        return message.toBuilder().setHeartBeat(lb).build();
    }
}
