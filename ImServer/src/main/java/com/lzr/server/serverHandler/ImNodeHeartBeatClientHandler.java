package com.lzr.server.serverHandler;

import com.lzr.im.common.bean.msg.ProtoMsg;
import com.lzr.server.distributed.ImWorker;
import com.lzr.util.JsonUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * WorkerRouter
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Slf4j
@ChannelHandler.Sharable
public class ImNodeHeartBeatClientHandler extends ChannelInboundHandlerAdapter {
    String from = null;
    
    int seq = 0;
    
    private static final int HEARTBEAT_INTERVAL = 50;
    
    public ProtoMsg.Message buildMessageHeartBeat() {
        if (null == from) {
            from = JsonUtil.pojoToJson(ImWorker.getInstance().getLocalNode());
        }
        
        ProtoMsg.Message.Builder mb = ProtoMsg.Message.newBuilder()
                .setType(ProtoMsg.HeadType.HEART_BEAT)
                .setSequence(++seq);
        
        ProtoMsg.MessageHeartBeat.Builder heartBeat = ProtoMsg.MessageHeartBeat.newBuilder()
                .setSeq(seq)
                .setJson(from)
                .setUid("-1");
        
        mb.setHeartBeat(heartBeat.build());
        return mb.build();
    }
    
    // send heartbeat when handler was added to pipiline
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        heartBeat(ctx);
    }

    public void heartBeat(ChannelHandlerContext ctx) {

        ProtoMsg.Message message = buildMessageHeartBeat();

        ctx.executor().schedule(() ->
        {

            if (ctx.channel().isActive()) {
                log.info(" send ImNode HEART_BEAT  msg");
                ctx.writeAndFlush(message);
                
                heartBeat(ctx);
            }

        }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * receive hb msg and
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }
        
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType type = pkg.getType();
        if (type.equals(ProtoMsg.HeadType.HEART_BEAT)) {
            ProtoMsg.MessageHeartBeat messageHeartBeat = pkg.getHeartBeat();
            log.info("  revc imNode HEART_BEAT  msg from: " + messageHeartBeat.getJson());
            log.info("  revc imNode HEART_BEAT seq: " + messageHeartBeat.getSeq());
            return;
        } else {
            super.channelRead(ctx, msg);
        }
    }
}
