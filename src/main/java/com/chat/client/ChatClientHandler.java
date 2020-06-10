package com.chat.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author martin
 * @email necaofeng@foxmail.com
 * @Date 2020/6/10 0010
 */
public class ChatClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //直接输出信息
        System.out.println(msg);
    }
}
