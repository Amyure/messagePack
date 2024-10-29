package com.messagePack.handler;

import com.messagePack.model.MessageInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private int lossConnectTime = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  throws Exception
    {

        MessageInfo in=(MessageInfo)msg;
        System.out.println("receive message:" +in.getContent());

        MessageInfo returnInfo=new MessageInfo();
//        returnInfo.setHeader((short)0x3C3C);
//        returnInfo.setMsgType((byte)0x01);
        returnInfo.setContent("Hello User!");

        ctx.writeAndFlush(returnInfo);

    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE == event.state()) {
                lossConnectTime++;
                System.out.println(ctx.channel().remoteAddress() + " lost connection");
                if (lossConnectTime > 5) {
                    System.out.println("close channel, lossConnectTime: " + lossConnectTime);
                    ctx.channel().close().sync();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
