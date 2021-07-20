package com.lzr.server.serverHandler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * ImNodeExceptionHandler
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Slf4j
@ChannelHandler.Sharable
public class ImNodeExceptionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        // ..

        //catch
        cause.printStackTrace();
        log.error(cause.getMessage());
        ctx.close();
    }

    /**
     * channel Read Complete
     * ctx.flush()
     */
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
        ctx.flush();
    }

}