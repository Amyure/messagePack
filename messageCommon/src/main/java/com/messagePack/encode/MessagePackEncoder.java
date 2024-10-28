package com.messagePack.encode;

import com.messagePack.model.MessageInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

public class MessagePackEncoder extends MessageToByteEncoder<MessageInfo>
{
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageInfo messageInfo, ByteBuf byteBuf) throws Exception
    {
        MessagePack messagePack=new MessagePack();
        byte[] raw=messagePack.write(messageInfo);
        byteBuf.writeBytes(raw);
    }
}
