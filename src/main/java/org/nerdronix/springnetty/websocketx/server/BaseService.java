/**
 * 
 */
package org.nerdronix.springnetty.websocketx.server;

import io.netty.channel.ChannelHandlerContext;


/**
 * @author Jason.Zhu
 * @date   2014-5-29
 * @email jasonzhu@augmentum.com.cn
 */
public interface BaseService{

	public void doLogic(ChannelHandlerContext ctx, Object msg);
}
