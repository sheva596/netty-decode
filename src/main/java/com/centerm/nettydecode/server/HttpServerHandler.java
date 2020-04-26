package com.centerm.nettydecode.server;

import com.alibaba.fastjson.JSONObject;
import com.centerm.nettydecode.constant.Constants;
import com.centerm.nettydecode.pojo.ReqRecord;
import com.centerm.nettydecode.pojo.Response;
import com.centerm.nettydecode.pojo.ResponseBody;
import com.centerm.nettydecode.service.SysService;
import com.eidlink.idocr.sdk.constants.PublicParam;
import com.eidlink.idocr.sdk.pojo.request.IdCardCheckParam;
import com.eidlink.idocr.sdk.pojo.result.CommonResult;
import com.eidlink.idocr.sdk.service.EidlinkService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

/**
 * @author Sheva
 * @date 2020/4/20 10:18
 * @description
 */
@Slf4j
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private Response sendInfoResp = new Response();
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Autowired
    SysService sysService;
    private static HttpServerHandler mServerHandler;


    @PostConstruct
    public void init() {
        mServerHandler = this;
        mServerHandler.sysService = this.sysService;
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        log.info("在线连接数： " + channels.size());
        log.info("管道id： " + ctx.channel().id());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("管道id： " + ctx.channel().id());
        log.info("在线连接数： " + channels.size());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        String ret = "";
        ReqRecord reqRecord = new ReqRecord();
        Long begin = System.currentTimeMillis();

        String clientIP = httpRequest.headers().get("X-Forwarded-For");
        if (clientIP == null) {
            InetSocketAddress insocket = (InetSocketAddress) ctx.channel()
                    .remoteAddress();
            clientIP = insocket.getAddress().getHostAddress();
        }
        log.info("客户端ip为：　" + clientIP);
        reqRecord.setIp(clientIP);
        try {
            String uri = httpRequest.uri();
            String data = httpRequest.content().toString(CharsetUtil.UTF_8);
            HttpMethod method = httpRequest.method();
            if (!"/query".equalsIgnoreCase(uri)) {
                ret = "非法请求路径：" + uri;
                response(ret, ctx, HttpResponseStatus.BAD_REQUEST);
                return;
            }
            if (HttpMethod.GET.equals(method)) {
                reqRecord.setReqData(data.getBytes());
                JSONObject object = JSONObject.parseObject(data);
                log.info(object.toJSONString());
                String sn = object.getString("sn");
                log.info("客户端请求数据内容： " + object.toJSONString());
//                log.info("sysService: " + mServerHandler.sysService.getClass());
//                Long terId = mServerHandler.sysService.findBySn(sn);
//                log.info("   " + terId);
//                if (null == terId) {
//                    mServerHandler.sysService.addTerminal(sn);
//                } else {
//                    mServerHandler.sysService.updateReqTimes(terId);
//                }
                String reqId = object.getJSONObject("body").getString("req_data");
                EidlinkService.initBasicInfo(Constants.IP, Constants.PORT, Constants.CID, Constants.APPID, Constants.APPKEY);
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
                reqRecord.setRspData(ret.getBytes());
                reqRecord.setExecuteTime(System.currentTimeMillis() - begin);
                reqRecord.setSn(sn);
                log.info(reqRecord.toString());
//                mServerHandler.sysService.addReqRecord(reqRecord);
//                SpringContextUtil.destroy();
                response(ret, ctx, HttpResponseStatus.OK);
            }
            if (HttpMethod.POST.equals(method)) {
                //TODO
            }
            if (HttpMethod.PUT.equals(method)) {
                //TODO
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            httpRequest.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("管道id： " + ctx.channel().id(), "发生错误,  在线连接数： " + channels.size());
    }

    private void response(String data, ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(data, CharsetUtil.UTF_8));
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }
}
