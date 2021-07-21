package com.lzr.client.ClientSender;

import com.lzr.client.clientBuilder.LoginMsgBuilder;
import com.lzr.im.common.bean.msg.ProtoMsg;
import com.lzr.util.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * LoginSender
 *
 * Author: zirui liu
 * Date: 2021/7/21
 */
@Slf4j
@Service("LoginSender")
public class LoginSender extends BaseSender {
    public void sendLoginMsg() {
        if (!isConnected()) {
            log.info("not connected yet!");
            return;
        }
        Logger.tcfo("send login msg");
        ProtoMsg.Message message =
                LoginMsgBuilder.buildLoginMsg(getUser(), getSession());
        super.sendMsg(message);
    }
}
