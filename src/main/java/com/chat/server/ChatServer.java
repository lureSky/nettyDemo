package com.chat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author martin
 * @email necaofeng@foxmail.com
 * @Date 2020/6/10 0010
 */
public class ChatServer {

    private int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public void run () {
        //配置NIO的服务端
        /**
         * 实际EventLoopGROUP就是Reactor线程组
         * 两个Reactor
         * 一个用于服务端接受客户端的链接
         * 一个用于SocketChannel的网络读写
         *
        */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            //创建引导类，降低开发复杂度，全往引导类内丢
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //添加相关数据    1.group 2.channel   3.childHandler  4.option
            //grop(group,group)  第一个参数是父级别group，第二个参数是子级别group

            //option两个选项，第一个是ChannelOption.SO_BACKLOG对应的是tcp/ip协议listen函数中的backlog参数  指定的tcp队列大小   第二个是保持长连接，
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatServerInitialize())
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .option(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            System.out.println("服务器启动了！！");


            /*channelFuture.channel().closeFuture().sync();
            System.out.println("服务器退出了");*/

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            //优雅退出group
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ChatServer(8888).run();
    }
}
