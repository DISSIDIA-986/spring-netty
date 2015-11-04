/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.nerdronix.springnetty.websocketx.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nerdronix.springnetty.websocketx.server.ext.LoginUser;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Handles handshakes and messages
 */
public abstract class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> implements BaseService{

    protected static final String WEBSOCKET_PATH = "/websocket";

    protected WebSocketServerHandshaker handshaker;
    
    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    
    static Map<Channel,LoginUser> users = new ConcurrentHashMap<Channel,LoginUser>();
    
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
    	channels.add(ctx.channel());
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	channels.remove(ctx.channel());
    	if(users.containsKey(ctx.channel()))
    		users.remove(ctx.channel());
    };
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        doLogic(ctx,msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    protected static String getWebSocketLocation(FullHttpRequest req) {
        String location =  req.headers().get(HttpHeaderNames.HOST) + WEBSOCKET_PATH;
        return "ws://" + location;
    }
}
