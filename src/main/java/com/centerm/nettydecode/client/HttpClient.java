package com.centerm.nettydecode.client;

import com.alibaba.fastjson.JSON;
import com.centerm.nettydecode.pojo.Request;
import com.centerm.nettydecode.pojo.RequestBody;
import com.centerm.nettydecode.server.HttpServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import java.net.URI;

/**
 * @author Sheva
 * @date 2020/4/20 10:31
 * @description
 */

@Slf4j
public class HttpClient {

    private Request decodeReq = new Request();

    public static void main(String[] args) throws Exception {
        HttpClient client = new HttpClient();
        client.connect("127.0.0.1", HttpServer.PORT);
    }

    public void connect(String host, int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true);

            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {

                    ch.pipeline().addLast(new HttpClientCodec());

                    ch.pipeline().addLast("objectAggregator", new HttpObjectAggregator(2014));
                    ch.pipeline().addLast("contentDecompressor", new HttpContentDecompressor());

                    ch.pipeline().addLast("httpClientHandler", new HttpClientHandler());
                }
            });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            URI uri = new URI("/query");
            RequestBody body = new RequestBody();
            body.setReq_data("test");
            decodeReq.setBody(body);
            String data = JSON.toJSONString(decodeReq);
            log.info("客户端建立成功...");
            DefaultFullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                    uri.toASCIIString(), Unpooled.wrappedBuffer(data.getBytes("UTF-8")));

            req.headers().set(HttpHeaderNames.HOST, host);
            req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            req.headers().set(HttpHeaderNames.CONTENT_LENGTH, req.content().readableBytes());

            channelFuture.channel().write(req);
            channelFuture.channel().flush();
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
