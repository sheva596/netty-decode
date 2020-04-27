package com.centerm.nettydecode.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sheva
 * @date 2020/4/20 10:12
 * @description
 */

@Slf4j
public class HttpServer {

    public static final int PORT = 8888;

    public static EventLoopGroup group = new NioEventLoopGroup();
    public static ServerBootstrap serverBootstrap = new ServerBootstrap();

    public void start() throws Exception {
        log.info("服务启动...");
        try {
            serverBootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("responseEncoder", new HttpResponseEncoder());
                            pipeline.addLast("requestDecode", new HttpRequestDecoder());
                            pipeline.addLast("objectAggregator", new HttpObjectAggregator(1024));
                            pipeline.addLast("contentCompressor", new HttpContentCompressor());
                            pipeline.addLast("httpServerHandler", new HttpServerHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
