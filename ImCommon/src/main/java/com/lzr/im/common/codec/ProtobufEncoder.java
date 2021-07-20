package com.lzr.im.common.codec;

import com.lzr.im.common.ProtoInstant;
import com.lzr.im.common.bean.msg.ProtoMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * ProtobufEncoder
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
@Slf4j
public class ProtobufEncoder extends MessageToByteEncoder<ProtoMsg.Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProtoMsg.Message message,
                          ByteBuf byteBuf) throws Exception {
        byteBuf.writeShort(ProtoInstant.MAGIC_CODE);
        byteBuf.writeShort(ProtoInstant.VERSION_CODE);

        byte[] payload = message.toByteArray();

        // length of msg
        int length = payload.length;

        // write len first
        byteBuf.writeInt(length);
        // then write the msg
        byteBuf.writeBytes(message.toByteArray());
    }
}
