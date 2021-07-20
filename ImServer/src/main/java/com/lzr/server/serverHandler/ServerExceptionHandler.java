package com.lzr.server.serverHandler;

import com.lzr.im.common.exception.InvalidFrameException;
import com.lzr.server.server.session.service.SessionManger;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ServerExceptionHandler
 * 
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Slf4j
@ChannelHandler.Sharable
@Service("ServerExceptionHandler")
public class ServerExceptionHandler extends ChannelInboundHandlerAdapter
{
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        if (cause instanceof InvalidFrameException)
        {
            log.error(cause.getMessage());

        } else
        {
            
            cause.printStackTrace();
            log.error(cause.getMessage());
        }

        SessionManger.instance().closeSession(ctx);
        ctx.close();
    }

    /**
     * ctx.flush()
     */
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)
            throws Exception
    {
        SessionManger.instance().closeSession(ctx);
    }
}