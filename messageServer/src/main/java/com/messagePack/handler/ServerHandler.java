package com.messagePack.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messagePack.model.AccountInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private int lossConnectTime = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  throws Exception
    {
        System.out.println("receive object:" + new ObjectMapper().writeValueAsString(msg));

        //測試回打一個
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(2L);
        System.out.println("send object:" + new ObjectMapper().writeValueAsString(accountInfo));
        ctx.writeAndFlush(accountInfo);
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
