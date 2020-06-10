package com.chat.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author martin
 * ChatServerInitialize 继承自 ChannelInitializer -> ChannelInboundHandlerAdapter -> ChannelInboundHandler -> ChannelHandler
 * 主要是用于创建子channel的时候的初始化工作
 *
 * 使用场景：
 *  a.在bootstrap初始化的时候，为监听端口accept事件的Channel添加ServerBootstrapAcceptor（引导接收器）
 *  b.新连接进入的时候，添加channelHandler
 * @email necaofeng@foxmail.com
 * @Date 2020/6/10 0010
 */
public class ChatServerInitialize extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        System.out.println("客户端连接了："+socketChannel.remoteAddress());

        //放到pipeChannel中，
        ChannelPipeline pipeline = socketChannel.pipeline();
        //添加需要的channelHandler

        /**
         * 发送的数据在管道里是无缝流动的，在数据量很大时，为了分割数据，采用以下几种方法
         * 定长方法
         * 固定分隔符
         * 将消息分成消息体和消息头，在消息头中用一个数组说明消息体的长度
         *
         * 在此是定长方式
         */
        pipeline.addLast("frame",new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decode",new StringDecoder());//解码器
        pipeline.addLast("encode",new StringEncoder());//编码器
        pipeline.addLast("handler",new ChatServerHandler());

    }
}
