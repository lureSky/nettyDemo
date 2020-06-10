package com.martin.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author martin
 * @email necaofeng@foxmail.com
 * @Date 2020/6/8 0008
 *
 * 主要是实现了server的业务逻辑
 * 标志一个ChannelHandler可以被多个Channel共享，
 * 如果不加：每次channel都要生成一个实例添加到ChannelPipeLine中
 */
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    //读取channel文件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf)msg;
        System.out.println("server received:" + byteBuf.toString(CharsetUtil.UTF_8));
        //将发送给服务端的信息写回给客户端
        ctx.write(byteBuf);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //将缓冲区内的数据冲刷到远程节点，并关闭该channel
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    //异常捕捉
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //打印异常信息
        cause.printStackTrace();
        //关闭channel
        ctx.close();
    }
}
