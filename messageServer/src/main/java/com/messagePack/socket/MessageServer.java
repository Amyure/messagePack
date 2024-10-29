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
        EventLoopGroup bossGroup = new NioEventLoopGroup(); //accept thread pool 默認核心執行緒數量 * 2
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //work thread pool
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .option(ChannelOption.SO_BACKLOG, 128) //等待連接上限
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 20000) //連線超時限制
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("heartBeat", new IdleStateHandler(0, 0, 5, TimeUnit.SECONDS)); //heartbeat
                            socketChannel.pipeline().addLast("lengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(1024,0,2,0,2)); //拆解數據
                            socketChannel.pipeline().addLast("messageDecoder", new MessagePackDecoder());
                            socketChannel.pipeline().addLast("lengthFieldPrepender", new LengthFieldPrepender(2)); //添加數據長度
                            socketChannel.pipeline().addLast("messageEncoder", new MessagePackEncoder());
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
