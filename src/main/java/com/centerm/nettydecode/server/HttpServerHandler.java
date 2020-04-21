package com.centerm.nettydecode.server;

import com.alibaba.fastjson.JSONObject;
import com.centerm.nettydecode.constant.Constants;
import com.centerm.nettydecode.pojo.Response;
import com.centerm.nettydecode.pojo.ResponseBody;
import com.eidlink.idocr.sdk.constants.PublicParam;
import com.eidlink.idocr.sdk.pojo.request.IdCardCheckParam;
import com.eidlink.idocr.sdk.pojo.result.CommonResult;
import com.eidlink.idocr.sdk.service.EidlinkService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sheva
 * @date 2020/4/20 10:18
 * @description
 */

@Slf4j
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    String ip = "testnidocr.eidlink.com";
    int port = 8080;
    String cid = "1421800";
    String appId = "TESTID20200224170526";
    String appKey = "77BAA0434BE6B8AF812356850AC3C3ED";

    private Response sendInfoResp = new Response();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        String ret = "";

        try{
            String uri = httpRequest.uri();
            String data = httpRequest.content().toString(CharsetUtil.UTF_8);
            HttpMethod method = httpRequest.method();
            if (!"/query".equalsIgnoreCase(uri)){
                ret = "非法请求路径：" + uri;
                response(ret, ctx, HttpResponseStatus.BAD_REQUEST);
                return;
            }
            if (HttpMethod.GET.equals(method)){
                log.info("客户端请求数据内容： " + data);
                JSONObject object = JSONObject.parseObject(data);
                String reqId = object.getJSONObject("body").getString("req_data");
                EidlinkService.initBasicInfo(ip, port, cid, appId, appKey);
                PublicParam publicParam = new PublicParam();
                IdCardCheckParam idCardCheckParam = new IdCardCheckParam(publicParam, reqId);
                CommonResult result = EidlinkService.idCardCheck(idCardCheckParam);
                ResponseBody body = new ResponseBody();
                if ("00".equals(result.getResult())) {
                    log.info("文本信息查询返回结果正常：  " + result.getResult() + " ====  " + result.getResult_detail());
                    sendInfoResp.setRsp_code("1");
                } else {
                    log.info("文本信息查询返回结果异常： " + result.getResult() + " ==== " + result.getResult_detail());
                    sendInfoResp.setRsp_msg("查询异常");
                    sendInfoResp.setRsp_code(result.getResult_detail());
                }
                body.setRsp_data(result.getCiphertext());
                body.setPicture(result.getPicture());
                body.setTrans_code(Constants.RESULT_CODE);
                sendInfoResp.setRsp_code(result.getResult());
                sendInfoResp.setBody(body);
                ret = JSONObject.toJSONString(sendInfoResp);
                log.info("向客户端发送的数据: " + ret);
                response(ret, ctx, HttpResponseStatus.OK);
            }
            if (HttpMethod.POST.equals(method)){
                //TODO
            }
            if (HttpMethod.PUT.equals(method)){
                //TODO
            }
        }catch (Exception e){
            log.error("服务器处理失败...");
        }finally {
            httpRequest.release();
        }
    }

    private void response(String data, ChannelHandlerContext ctx, HttpResponseStatus status){
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(data, CharsetUtil.UTF_8));
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }
}
