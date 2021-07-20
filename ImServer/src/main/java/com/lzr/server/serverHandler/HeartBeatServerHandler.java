package com.lzr.server.serverHandler;

import com.lzr.concurrent.FutureTaskScheduler;
import com.lzr.constants.ServerConstants;
import com.lzr.im.common.bean.msg.ProtoMsg;
import com.lzr.server.server.session.service.SessionManger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * HeartBeatServerHandler
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Slf4j
public class HeartBeatServerHandler extends IdleStateHandler {
    private static final int READ_IDLE_GAP = 1500;
    
    public HeartBeatServerHandler()
    {
        super(READ_IDLE_GAP, 0, 0, TimeUnit.SECONDS);

    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }
        
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = pkg.getType();
        
        if (headType.equals(ProtoMsg.HeadType.HEART_BEAT)) {
            FutureTaskScheduler.add(() -> {
                if (ctx.channel().isActive()) {
                    ctx.writeAndFlush(msg);
                }
            });
        }
        
        super.channelRead(ctx, msg);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception
    {
        log.info(READ_IDLE_GAP + "s no heartbeat, close conn, {}", ctx.channel().attr(ServerConstants.CHANNEL_NAME).get());
        SessionManger.instance().closeSession(ctx);
    }
}
