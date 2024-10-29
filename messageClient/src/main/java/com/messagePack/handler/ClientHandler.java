package com.messagePack.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messagePack.model.AccountInfo;
import com.messagePack.model.MessageInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.msgpack.MessagePack;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class ClientHandler extends SimpleChannelInboundHandler<Object> {

    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat", StandardCharsets.UTF_8));

    private static final int TRY_TIMES = 3;

    private int currentTime = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object info) throws Exception {
        System.out.println("receive object:"+ new ObjectMapper().writeValueAsString(info));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        // 处理I/O事件的异常
        cause.printStackTrace();
        ctx.close();
        System.out.println("channel closed");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        System.out.println("client connected");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("send heart beat:" + new Date());
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.WRITER_IDLE.equals(event.state())) {
                if (currentTime <= TRY_TIMES) {
                    System.out.println("current time is:"+currentTime);
                    currentTime++;
                    ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
                }
            }
        }
    }
}
