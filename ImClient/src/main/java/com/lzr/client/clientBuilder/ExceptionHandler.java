package com.lzr.client.clientBuilder;

import com.lzr.client.client.CommandController;
import com.lzr.im.common.exception.BusinessException;
import com.lzr.im.common.exception.InvalidFrameException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ExceptionHandler
 *
 * Author: zirui liu
 * Date: 2021/7/21
 */
@Slf4j
@ChannelHandler.Sharable
@Service("ExceptionHandler")
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private CommandController commandController;
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof BusinessException) {
            // bussiness error, note client
        } else if (cause instanceof InvalidFrameException) {
            log.error(cause.getMessage());
            // server handle the msg
        } else {
            log.error(cause.getMessage());
            ctx.close();
            
            commandController.setConnectFlag(false);
            commandController.startConnectServer();
        }
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
