package com.chat.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author martin
 *  回调处理类---继承SimpleChannelInboundHandler  主要解决入站问题，加入到pipeLine中
 * @email necaofeng@foxmail.com
 * @Date 2020/6/10 0010
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    //服务端维护一个集合，只要客户端连接就进入到该通道集合中
    //GlobalEventExecutor.INSTANCE = new GlobalEventExecutor();  底层是创建默认的线程池
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * @Description:    每当有客户端连接时，记录客户端，并加入到队列，同时告诉其他客户端，我加入了
     * @param ctx
     * @return: void
     * @author: martin
     * @date: 2020-06-10 16:21
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        //通知其他客户端，进入聊天室
        for (Channel clientChannel : channels) {
            //如果是自己重复的进入，则不会通知
            if (clientChannel != channel) {
                clientChannel.writeAndFlush("[欢迎: " + channel.remoteAddress() + "] 进入聊天室！\n");
            }
        }
        channels.add(channel);
    }


    /**
     * @Description: 离开聊天室
     * @param ctx
     * @return: void
     * @author: martin
     * @date: 2020-06-10 16:24
    */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel outChannel = ctx.channel();
        //通知其他客户端，进入聊天室
        for (Channel clientChannel : channels) {
            //如果是自己重复的进入，则不会通知
            if (clientChannel != outChannel) {
                clientChannel.writeAndFlush("[欢迎: " + outChannel.remoteAddress() + "] 离开聊天室！\n");
            }
        }
        channels.remove(outChannel);
    }


    /**
     * @Description: 客户端消息键入
     * @param ctx
     * @param msg
     * @return: void
     * @author: martin
     * @date: 2020-06-10 16:25
    */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel inComing = ctx.channel();

        for (Channel channel : channels){
            if (channel != inComing){
                channel.writeAndFlush("[用户" + inComing.remoteAddress() + " 说：]" + msg + "\n");
            }else {
                channel.writeAndFlush("[我说：]" + msg + "\n");
            }
        }

    }

    /**
     * 当服务器监听到客户端活动时
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();
        System.out.println("[" + inComing.remoteAddress() + "]: 在线");
    }

    /**
     * 离线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();
        System.out.println("[" + inComing.remoteAddress() + "]: 离线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel inComing = ctx.channel();
        System.out.println(inComing.remoteAddress() + "通讯异常！");
        ctx.close();
    }

}
