package com.lzr.client.ClientSender;

import com.lzr.client.client.ClientSession;
import com.lzr.client.client.CommandController;
import com.lzr.concurrent.CallbackTask;
import com.lzr.concurrent.CallbackTaskScheduler;
import com.lzr.im.common.bean.UserDTO;
import com.lzr.im.common.bean.msg.ProtoMsg;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * BaseSender
 *
 * Author: zirui liu
 * Date: 2021/7/21
 */
@Data
@Slf4j
public abstract class BaseSender
{
    private UserDTO user;
    
    private ClientSession session;

    @Autowired
    protected CommandController commandClient;


    public boolean isConnected() {
        if (null == session) {
            log.info("session is null");
            return false;
        }

        return session.isConnected();
    }

    public boolean isLogin() {
        if (null == session) {
            log.info("session is null");
            return false;
        }

        return session.isLogin();
    }

    public void sendMsg(ProtoMsg.Message message) {
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                if (null == getSession()) {
                    throw new Exception("session is null");
                }

                if (!isConnected()) {
                    log.info("conn fail");
                    throw new Exception("conn fail");
                }

                final Boolean[] isSuccess = {false};

                ChannelFuture f = getSession().witeAndFlush(message);
                f.addListener(future -> {
                    if (future.isSuccess()) {
                        isSuccess[0] = true;
                        log.info("opr success");
                    }
                });


                try {
                    f.sync();
                } catch (InterruptedException e) {
                    isSuccess[0] = false;
                    e.printStackTrace();
                    throw new Exception("error occur");
                }
                return isSuccess[0];
            }

            @Override
            public void onBack(Boolean b) {
                if (b) {
                    BaseSender.this.sendSucced(message);
                } else {
                    BaseSender.this.sendfailed(message);
                }

            }

            @Override
            public void onException(Throwable t) {
                BaseSender.this.sendException(message);
            }
        });
    }

    protected void sendSucced(ProtoMsg.Message message) {
        log.info("sent");
    }

    protected void sendfailed(ProtoMsg.Message message)
    {
        log.info("send failed");
    }

    protected void sendException(ProtoMsg.Message message) {
        log.info("send error");
    }
}
