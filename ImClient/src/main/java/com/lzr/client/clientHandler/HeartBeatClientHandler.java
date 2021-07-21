package com.lzr.client.clientHandler;

import com.lzr.client.client.ClientSession;
import com.lzr.client.clientBuilder.HeartBeatMsgBuilder;
import com.lzr.im.common.bean.UserDTO;
import com.lzr.im.common.bean.msg.ProtoMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * HeartBeatClientHandler
 *
 * Author: zirui liu
 * Date: 2021/7/21
 */
@Slf4j
@ChannelHandler.Sharable
@Service("HeartBeatClientHandler")
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {
    private static final int HEARTBEAT_INTERVAL = 50;
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        ClientSession session = ClientSession.getSession(ctx);

        UserDTO user = session.getUser();
        HeartBeatMsgBuilder builder = new HeartBeatMsgBuilder(user, session);

        ProtoMsg.Message message = builder.buildMsg();

        heartBeat(ctx, message);
    }
    
    private void heartBeat(ChannelHandlerContext ctx, ProtoMsg.Message heartbeatMsg) {
        ctx.executor().schedule(() -> {
            if (ctx.channel().isActive()) {
                log.info("send heart_beat msg to server");
                
                ctx.writeAndFlush(heartbeatMsg);

                heartBeat(ctx, heartbeatMsg);
            }
        }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = pkg.getType();
        if (headType.equals(ProtoMsg.HeadType.HEART_BEAT)) {
            log.info(" 收到回写的 HEART_BEAT  消息 from server");
        } else {
            super.channelRead(ctx, msg);
        }
    }
}
