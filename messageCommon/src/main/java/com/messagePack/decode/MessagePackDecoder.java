package com.messagePack.decode;

import com.messagePack.model.MessageInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

public class MessagePackDecoder extends MessageToMessageDecoder<ByteBuf>
{
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception
    {
        MessagePack messagePack=new MessagePack();

        int intLength=byteBuf.readableBytes();
        byte[] raw=new byte[intLength];
        byteBuf.getBytes(byteBuf.readerIndex(),raw,0,intLength);

        list.add(messagePack.read(raw, MessageInfo.class));
    }
}
