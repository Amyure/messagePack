package com.messagePack.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messagePack.decode.MessagePackDecoder;
import com.messagePack.encode.MessagePackEncoder;
import com.messagePack.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class MessageClient {

    private final String host;
    private final int port;
    private ChannelFuture channelFuture;
    private Bootstrap bootstrap;
    private final EventLoopGroup group;

    public MessageClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.group = new NioEventLoopGroup(); //
    }

    public void start() throws Exception {
        new Thread(() -> {
            try {
                run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void run() throws Exception {
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host, port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(0, 0, 5, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024,0,2,0,2));
                        ch.pipeline().addLast(new MessagePackDecoder());
                        ch.pipeline().addLast(new LengthFieldPrepender(2));
                        ch.pipeline().addLast(new MessagePackEncoder());
                        ch.pipeline().addLast(new ClientHandler(MessageClient.this));
                    } });
        connect();
    }

    public void connect() throws Exception {
        channelFuture = bootstrap.connect();
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                //連線失敗進行重新連線
                if (!channelFuture.isSuccess()) {
                    channelFuture.channel().eventLoop().schedule(() -> {
                        System.out.println("Reconnect to server:" + host + ":" + port);
                        try {
                            connect();
                        } catch (Exception e) {
                            System.out.println("reconnect fail to server:" + host + ":" + port + ", error:" + e.getMessage());
                        }
                    }, 5, TimeUnit.SECONDS);
                } else {
                    System.out.println("Successfully Connected to server:" + host + ":" + port );
                }
            }
        });
        channelFuture.channel().closeFuture().sync();
    }

    public boolean isActive() {
        return channelFuture != null && channelFuture.channel().isActive();
    }

    public void shutdown() {
        System.out.println("message client shutdown");
        group.shutdownGracefully();
    }

    public void send(Object object) throws Exception {
        if (isActive()) {
            System.out.println("send object: " + new ObjectMapper().writeValueAsString(object));
            channelFuture.channel().writeAndFlush(object);
        } else {
            System.out.println("Channel is not active, message cannot be sent.");
        }
    }

}
