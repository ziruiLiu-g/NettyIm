package com.lzr.server.server;

import com.lzr.concurrent.FutureTaskScheduler;
import com.lzr.im.common.codec.ProtobufDecoder;
import com.lzr.im.common.codec.ProtobufEncoder;
import com.lzr.server.distributed.ImWorker;
import com.lzr.server.distributed.WorkerRouter;
import com.lzr.server.serverHandler.*;
import com.lzr.util.IOUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import javafx.concurrent.Worker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.FutureTask;

/**
 * the starter of server
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Data
@Slf4j
@Service("ChatServer")
public class ChatServer {
    @Value("${server.port}")
    private int port;

    // loop groups
    private EventLoopGroup bg;
    private EventLoopGroup wg;

    private ServerBootstrap b = new ServerBootstrap();

    @Autowired
    private LoginRequestHandler loginRequestHandler;

    @Autowired
    private ServerExceptionHandler serverExceptionHandler;

    @Autowired
    private RemoteNotificationHandler remoteNotificationHandler;

    @Autowired
    private ChatRedirectHandler chatRedirectHandler;

    public void run() {
        // conn to bg
        bg = new NioEventLoopGroup(1);
        // conn to worker group
        wg = new NioEventLoopGroup();

        // set reactor to bootstrap
        b.group(bg, wg);
        // set nio
        b.channel(NioServerSocketChannel.class);
        // set ip port
        String ip = IOUtil.getHostAddress();
        b.localAddress(new InetSocketAddress(ip, port));
        // set channel option
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        // setup pipeline
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("deCoder", new ProtobufDecoder());
                ch.pipeline().addLast("enCoder", new ProtobufEncoder());

//                ch.pipeline().addLast("heartBeat", new HeartBeatServerHandler());

                // delete after triggered
                ch.pipeline().addLast("login", loginRequestHandler);
                ch.pipeline().addLast("remoteNotificationHandler", remoteNotificationHandler);
                ch.pipeline().addLast("chatRedirect", chatRedirectHandler);
                ch.pipeline().addLast("serverException", serverExceptionHandler);
            }
        });

        // bind server
        ChannelFuture channelFuture = null;
        boolean isStart = false;
        while (!isStart) {
            try {
                channelFuture = b.bind().sync();
                log.info("im server start, port: {}", channelFuture.channel().localAddress());
                isStart = true;
            } catch (Exception e) {
                log.error("error when start server, ", e);
                port++;
                log.error("try new port, port: {}", port);
                b.localAddress(new InetSocketAddress(port));
            }
        }

        ImWorker.getInstance().setLocalNode(ip, port);
        FutureTaskScheduler.add(() -> {
            // start node
            ImWorker.getInstance().init();

            // start node manager
            WorkerRouter.getInstance().init();
        });

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    wg.shutdownGracefully();
                    bg.shutdownGracefully();
                })
        );

        try {
            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();
        } catch (Exception e) {
            log.error("fail when close channel", e);
        } finally {
            wg.shutdownGracefully();
            bg.shutdownGracefully();
        }
    }
}
