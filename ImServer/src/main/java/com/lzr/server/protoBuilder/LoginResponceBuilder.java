package com.lzr.server.protoBuilder;

import com.lzr.im.common.ProtoInstant;
import com.lzr.im.common.bean.msg.ProtoMsg;
import org.springframework.stereotype.Service;

/**
 * LoginResponceBuilder
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Service("LoginResponceBuilder")
public class LoginResponceBuilder {
    public ProtoMsg.Message loginResponse(
            ProtoInstant.ResultCodeEnum en,
            long seqId,
            String sessionId) {
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.LOGIN_RESPONSE)
                .setSequence(seqId)
                .setSessionId(sessionId);

        ProtoMsg.LoginResponse.Builder rb = ProtoMsg.LoginResponse
                .newBuilder()
                .setCode(en.getCode())
                .setInfo(en.getDesc())
                .setExpose(1);
        mb.setLoginResponse(rb.build());

        return mb.build();
    }
}
