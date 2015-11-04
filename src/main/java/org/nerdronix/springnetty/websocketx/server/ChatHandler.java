/**
 * 
 */
package org.nerdronix.springnetty.websocketx.server;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.HashMap;
import java.util.Map;

import org.nerdronix.springnetty.websocketx.server.ext.LoginUser;

import com.dissidia986.service.AppService;
import com.dissidia986.util.WtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;


public class ChatHandler extends WebSocketServerHandler{
	private AppService appService;
	
	public ChatHandler() {
		super();
	}

	public ChatHandler(AppService appService) {
		super();
		this.appService = appService;
	}

	private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // Handle a bad request.
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }

        // Allow only GET methods.
        if (req.method() != GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }

        // Send the demo page and favicon.ico
        if ("/".equals(req.uri())) {
            //ByteBuf content = WebSocketServerIndexPage.getContent(getWebSocketLocation(req));
        	Map<String,Object> model = new HashMap<String,Object>();
        	model.put("websocket_location", getWebSocketLocation(req));
        	ByteBuf content = Unpooled.copiedBuffer(appService.tranlateHTML(model,"websocket_index_page.vm"),CharsetUtil.UTF_8);
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, content);

            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            HttpUtil.setContentLength(res, content.readableBytes());

            sendHttpResponse(ctx, req, res);
            return;
        }
        if ("/favicon.ico".equals(req.uri())) {
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
            sendHttpResponse(ctx, req, res);
            return;
        }

        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                getWebSocketLocation(req), null, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
                    .getName()));
        }
        String request = ((TextWebSocketFrame) frame).text();
        System.err.printf("%s received %s%n", ctx.channel(), request);
        if(request.contains("loginid")){
        	ObjectMapper mapper = WtUtils.getMapper();
        	try{
        		LoginUser user = mapper.readValue(request, LoginUser.class);
        		if(user!=null){
        			users.put(ctx.channel(), user);
        			if(appService!=null){
        				appService.chatLogin(ctx.channel().remoteAddress().toString(), user);
        			}else{
        				System.err.println("appService注入失败");
        			}
        			for (Channel c: channels){
                    	// Send the uppercase string back.
                    	if (c != ctx.channel()) {
                            c.writeAndFlush(new TextWebSocketFrame("[" + user.getUsername() + "] " + " 加入聊天"));
                        }
                    }
        		}
        	}catch(Exception e){
        		e.printStackTrace();
        		ctx.channel().writeAndFlush(new TextWebSocketFrame("[you] " + "登录失败"));
        	}
    		
        }else{
        	for (Channel c: channels){
            	// Send the uppercase string back.
            	if (c != ctx.channel()) {
            		String prefix = users.containsKey(ctx.channel()) ? users.get(ctx.channel()).getUsername():ctx.channel().remoteAddress().toString();
                    c.writeAndFlush(new TextWebSocketFrame("[" + prefix + "] " + request.toUpperCase()));
                } else {
                    c.writeAndFlush(new TextWebSocketFrame("[you] " + request.toUpperCase()));
                    if ("bye".equals(request.toLowerCase())) {
                        ctx.close();
                    }
                }
            }
        }
        
        
    }

    private static void sendHttpResponse(
            ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
    
	@Override
	public void doLogic(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
	}

	public AppService getAppService() {
		return appService;
	}

	public void setAppService(AppService appService) {
		this.appService = appService;
	}
	
}
