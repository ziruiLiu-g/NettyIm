package com.lzr.im.common.codec;

import com.lzr.im.common.ProtoInstant;
import com.lzr.im.common.bean.msg.ProtoMsg;
import com.lzr.im.common.exception.InvalidFrameException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * ProtobufEncoder
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
@Slf4j
public class ProtobufDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf,
                          List<Object> out) throws Exception {
        // store current index
        byteBuf.markReaderIndex();

        // smaller than the length mark of msg
        if (byteBuf.readableBytes() < 8) {
            return;
        }

        short magic = byteBuf.readShort();
        if (magic != ProtoInstant.MAGIC_CODE) {
            String error = "users token wrong: " + ctx.channel().remoteAddress();
            throw new InvalidFrameException(error);
        }

        // read version
        short version = byteBuf.readShort();
        // read length
        int length = byteBuf.readInt();

        if (length < 0) {
            ctx.close();
        }

        if (length > byteBuf.readableBytes()) {
            // if readable bytes length less than length
            // reset position
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] array;
        // hasArray can find out that whether it is heap buf or direct buf
        if (byteBuf.hasArray()) {
            ByteBuf slice = byteBuf.slice(byteBuf.readerIndex(), length);
            array = slice.array();
            byteBuf.retain();
        } else {
            array = new byte[length];
            byteBuf.readBytes(array, 0 ,length);
        }


        // convert bytes to ProtoMsg
        ProtoMsg.Message outMsg = ProtoMsg.Message.parseFrom(array);
        if (byteBuf.hasArray()) {
            byteBuf.release();
        }
        if (outMsg != null) {
            out.add(outMsg);
        }
    }
}
