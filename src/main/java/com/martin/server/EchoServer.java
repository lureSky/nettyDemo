package com.martin.server;

import com.martin.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author martin
 * @email necaofeng@foxmail.com
 * @Date 2020/6/8 0008
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port){
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        //如果没有端口号则直接报错
       /* if (args.length != 1) {
            System.err.println("Usage:" + EchoServer.class.getSimpleName() + "<Port>");
            return;
        }*/

//        int port = Integer.parseInt(args[0]);
        int port = 9999;
        new EchoServer(port).start();
    }

    public void start() throws Exception{
        //创建channelHandler，通道处理器
        final EchoServerHandler serverHandler = new EchoServerHandler();
        //创建EventLoopGroup  ---- 本身一个线程，处理一个channel钟所有的Io事件，并在该group的整个生命周期都不改变，解决了所有的同步操作顾虑，同一个对象进行操作
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建ServerBootStrap
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)          //使用指定NIo传输channel NioServerSocketChannel
                    .localAddress(new InetSocketAddress(port))       //绑定实时的端口
                    .childHandler(new ChannelInitializer<SocketChannel>() {//添加一个EchoHandler添加到字Channel的ChannelPipe中
                        //当一个新的连接被接受的时候，一个新的childhannel会被创建，
                        //初始化的时候放入一个serverHandler，这个主要是放入到channel的channelPipeLine中，主要处理入站消息的相关操作
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(serverHandler);        //因为被@Sharable，因此可以使用同样的实例
                        }
                    });
            ChannelFuture future = b.bind().sync();     //异步绑定服务器，调用sync确保每次阻塞保持绑定成功在开放
            future.channel().closeFuture().sync();      //阻塞当前线程直到完成再获取closeFuture
        }finally {
            group.shutdownGracefully().sync();     //关闭eventLoopGroup 释放所有资源，也需要异步阻塞进行
        }

    }

}
