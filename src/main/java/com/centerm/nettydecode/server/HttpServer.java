package com.centerm.nettydecode.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        try{
            serverBootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerHandlerInit());

            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully().sync();
        }
    }
}
