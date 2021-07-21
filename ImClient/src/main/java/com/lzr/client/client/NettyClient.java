package com.lzr.client.client;

import com.lzr.client.ClientSender.ChatSender;
import com.lzr.client.ClientSender.LoginSender;
import com.lzr.client.clientBuilder.ExceptionHandler;
import com.lzr.client.clientHandler.ChatMsgHandler;
import com.lzr.client.clientHandler.LoginResponceHandler;
import com.lzr.im.common.bean.UserDTO;
import com.lzr.im.common.codec.ProtobufDecoder;
import com.lzr.im.common.codec.ProtobufEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * NettyClient
 *
 * Author: zirui liu
 * Date: 2021/7/21
 */
@Slf4j
@Data
@Service("NettyClient")
public class NettyClient {
    private String host;
    
    private int port;
    
    private Channel channel;
    
    private ChatSender sender;
    
    private LoginSender loginSender;

    private boolean initFalg = true;
    
    private UserDTO user;
    
    private GenericFutureListener<ChannelFuture> connectedListener;

    private Bootstrap b;
    
    private EventLoopGroup g;

    @Autowired
    private ChatMsgHandler chatMsgHandler;

    @Autowired
    private LoginResponceHandler loginResponceHandler;
    
    @Autowired
    private ExceptionHandler exceptionHandler;
    
    public NettyClient() {
        g = new NioEventLoopGroup();
    }
    
    public void doConnect() {
        try {
            b = new Bootstrap();
            
            b.group(g);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.remoteAddress(host, port);
            
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("decoder", new ProtobufDecoder());
                    ch.pipeline().addLast("encoder", new ProtobufEncoder());
                    ch.pipeline().addLast("loginResponseHandler", loginResponceHandler);
                    ch.pipeline().addLast("chatMsgHandler", chatMsgHandler);
                    ch.pipeline().addLast("exceptionHandler", exceptionHandler);
                }
            });
            log.info("client connecting...");
            
            ChannelFuture f = b.connect();
            f.addListener(connectedListener);
        } catch (Exception e) {
            log.info("client conn failed!" + e.getMessage());
        }
    }

    public void close()
    {
        g.shutdownGracefully();
    }
}
