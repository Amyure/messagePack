package com.messagePack.decode;

import com.messagePack.socket.MessageInfo;
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
        MessageInfo messageInfo = messagePack.read(raw, MessageInfo.class);
        list.add(messagePack.read(messageInfo.getObject(), Class.forName(messageInfo.getClassPath())));
    }
}
