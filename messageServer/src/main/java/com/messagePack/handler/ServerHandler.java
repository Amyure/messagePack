package com.messagePack.handler;

import com.messagePack.model.AccountInfo;
import com.messagePack.service.AccountInfoService;
import com.messagePack.socket.MessageInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private int lossConnectTime = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  throws Exception
    {
        MessageInfo in = (MessageInfo) msg;
        System.out.println("receive message:" + msg.toString());
        Object returnObject = switch (in.getType()) {
            case MessageInfo.ACCOUNT_INFO_TYPE -> new AccountInfoService().getInfo(in);
            default -> "";
        };
        ctx.writeAndFlush(new MessageInfo(returnObject));

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
