package com.lzr.server.distributed;

import com.lzr.entity.ImNode;
import com.lzr.im.common.bean.Notification;
import com.lzr.im.common.bean.UserDTO;
import com.lzr.im.common.bean.msg.ProtoMsg;
import com.lzr.im.common.codec.ProtobufDecoder;
import com.lzr.im.common.codec.ProtobufEncoder;
import com.lzr.server.protoBuilder.NotificationMsgBuilder;
import com.lzr.server.serverHandler.ImNodeExceptionHandler;
import com.lzr.server.serverHandler.ImNodeHeartBeatClientHandler;
import com.lzr.util.JsonUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * PeerSender
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Slf4j
@Data
public class PeerSender {
    private int reConnectCount = 0;
    
    private Channel channel;
    
    private ImNode rmNode;

    /**
     * unique flag
     */
    private boolean connectFlag = false;
    
    private UserDTO user;

    private Bootstrap b;
    
    private EventLoopGroup g;

    public PeerSender(ImNode n) {
        this.rmNode = n;

        b = new Bootstrap();
    }
    
    GenericFutureListener<ChannelFuture> closeListener = (ChannelFuture f) -> {
        log.warn("distribute conn close...{}", rmNode.toString());
        channel = null;
        connectFlag = false;
    };
    
    private GenericFutureListener<ChannelFuture> connectedListener = (ChannelFuture f) -> {
        final EventLoop eventLoop = f.channel().eventLoop();
        if (!f.isSuccess() && ++reConnectCount < 3) {
            log.warn("conn fail, {} retry after 10s..", reConnectCount);
            eventLoop.schedule(() -> PeerSender.this.doConnect(), 10, TimeUnit.SECONDS);
            
            connectFlag = false;
        } else {
            connectFlag = true;
            
            log.info(new Date() + " distributed note connected: {}", rmNode.toString());
            
            channel = f.channel();
            channel.closeFuture().addListener(closeListener);
            
            // send notification
            Notification<ImNode> notification = new Notification<>(ImWorker.getInstance().getLocalNodeInfo());
            notification.setType(Notification.CONNECT_FINISHED);
            String json = JsonUtil.pojoToJson(notification);
            ProtoMsg.Message pkg = NotificationMsgBuilder.buildNotification(json);
            writeAndFlush(pkg);
        }
    };

    /**
     * reconnect
     */
    public void doConnect() {
        String host = rmNode.getHost();
        
        int port = rmNode.getPort();
        
        try {
            if (b != null && b.group() == null) {
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
                        ch.pipeline().addLast("imNodeHeartBeatClientHandler", new ImNodeHeartBeatClientHandler());
                        ch.pipeline().addLast("exceptionHandler", new ImNodeExceptionHandler());
                    }
                });
                log.info(new Date() + "begin to conn dist node: {}", rmNode.toString());
                
                ChannelFuture f = b.connect();
                f.addListener(closeListener);
            } else if (b.group() != null) {
                log.info(new Date() + "conn again, {}", rmNode.toString());
                ChannelFuture f = b.connect();
                f.addListener(closeListener);
            }
        } catch (Exception e) {
            log.error("client conn fail, {}", e.getMessage());
        }
    }

    public void stopConnecting()
    {
        g.shutdownGracefully();
        connectFlag = false;
    }

    public void writeAndFlush(Object pkg)
    {
        if (connectFlag == false)
        {
            log.error("can not conn node: {}", rmNode.toString());
            return;
        }
        channel.writeAndFlush(pkg);
    }
}
