package com.centerm.nettydecode.client;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sheva
 * @date 2020/4/20 10:33
 * @description
 */

@Slf4j
public class HttpClientHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse httpResponse = (FullHttpResponse)msg;

        ByteBuf content = httpResponse.content();
        String resp = content.toString(CharsetUtil.UTF_8);
        JSONObject object = JSONObject.parseObject(resp);
        log.info("客户端接收到服务端返回的数据: " + object.toJSONString());
        content.release();
    }
}
