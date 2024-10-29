package com.messagePack.encode;

import com.messagePack.model.MessageInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

public class MessagePackEncoder extends MessageToByteEncoder<Object>
{
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception
    {
        MessagePack messagePack = new MessagePack();
        String path = object.getClass().getName();
        byte[] raw = messagePack.write(object);
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setClassPath(path);
        messageInfo.setObject(raw);
        byteBuf.writeBytes(messagePack.write(messageInfo));
    }
}
