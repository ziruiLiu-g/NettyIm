package com.lzr.server.serverHandler;

import com.google.gson.reflect.TypeToken;
import com.lzr.constants.ServerConstants;
import com.lzr.entity.ImNode;
import com.lzr.im.common.bean.Notification;
import com.lzr.im.common.bean.msg.ProtoMsg;
import com.lzr.server.server.session.service.SessionManger;
import com.lzr.util.JsonUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * RemoteNotificationHandler
 * 
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Slf4j
@Service("RemoteNotificationHandler")
@ChannelHandler.Sharable
public class RemoteNotificationHandler extends ChannelInboundHandlerAdapter {
    /**
     * recv msg
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (null == msg || !(msg instanceof ProtoMsg.Message))
        {
            super.channelRead(ctx, msg);
            return;
        }
        
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        ProtoMsg.HeadType headType = pkg.getType();
        if (!headType.equals(ProtoMsg.HeadType.MESSAGE_NOTIFICATION))
        {
            super.channelRead(ctx, msg);
            return;
        }

        // handle msg
        ProtoMsg.MessageNotification notificationPkg = pkg.getNotification();
        String json = notificationPkg.getJson();
        
        log.info("get notification: {}", json);
        Notification<Notification.ContentWrapper> notification =
                JsonUtil.jsonToPojo(json, new TypeToken<Notification<Notification.ContentWrapper>>() {
                    
                }.getType());
        
        // offline
        if (notification.getType() == Notification.SESSION_OFF) {
            String sid = notification.getWrapperContent();
            log.info("user offline, sid={}", sid);
            SessionManger.instance().removeRemoteSession(sid);
        }
        
        // online
        if (notification.getType() == Notification.SESSION_ON) {
            String sid = notification.getWrapperContent();
            log.info("user online, sid={}", sid);
                
            // wait for complement
            // SessionManger.inst().addRemoteSession(remoteSession);
        }
        
        // conn node success
        if (notification.getType() == Notification.CONNECT_FINISHED) {
            Notification<ImNode> nodInfo =
                    JsonUtil.jsonToPojo(json, new TypeToken<Notification<ImNode>>()
                    {
                    }.getType());
            
            log.info("recv dist node conn: {}", json);
            
            ctx.pipeline().remove("login");
            ctx.channel().attr(ServerConstants.CHANNEL_NAME).set(JsonUtil.pojoToJson(nodInfo));
        }
    }
}
