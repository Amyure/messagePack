package com.messagePack.socket;

import com.messagePack.decode.MessagePackDecoder;
import com.messagePack.encode.MessagePackEncoder;
import com.messagePack.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class MessageServer {

    private final int port;

    public MessageServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new IdleStateHandler(0, 0, 5, TimeUnit.SECONDS));
                            socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024,0,2,0,2));
                            socketChannel.pipeline().addLast(new MessagePackDecoder());
                            socketChannel.pipeline().addLast(new LengthFieldPrepender(2));
                            socketChannel.pipeline().addLast(new MessagePackEncoder());
                            socketChannel.pipeline().addLast(new ServerHandler());
                        }
                    });
            System.out.println("MessageServer starting...");
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
            System.out.println("MessageServer Listening on port " + port);
        } finally {
            workerGroup.shutdownGracefully().sync();
            bossGroup.shutdownGracefully().sync();
        }
    }

}
