package com.servcer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyServer {
    public static void main(String[] args) {
        // 两个 NioEventLoopGroup，可以看做是传统 IO 编程的两大线程组
        // boss 对应 IoServer.java 中的接受新连接线程，主要负责创建新连接
        NioEventLoopGroup boss = new NioEventLoopGroup();
        // worker 对应 IOServer.java 中的负责读取数据的线程，主要用于读取数据以及业务逻辑处理
        NioEventLoopGroup worker = new NioEventLoopGroup();

        // 引导类。进行服务端的启动工作
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boss, worker) // 引导类配置两大线程组
                .channel(NioServerSocketChannel.class) // 指定 IO 类型
                .childHandler(new ChannelInitializer<NioSocketChannel>() { // 定义后续每条连接的数据读写，业务处理逻辑

                    // NioSocketChannel 是 Netty 对 Nio 类型的连接的抽象
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                System.out.println("this msg is " + msg);
                            }
                        });
                    }
                });//.bind(8003);
        // 异步方法，调用之后立即返回，返回值是一个 ChannelFuture，可以添加监听器监听是否绑定成功
//        serverBootstrap.bind(8003);
/*        serverBootstrap.bind(8003).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("端口绑定成功");
                } else {
                    System.out.println("端口绑定失败");
                }
            }
        });*/
        bind(serverBootstrap, 8003);
    }

    /**
     * 抽取绑定方法，从指定端口开始绑定，直到绑定成功
     */
    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        serverBootstrap.bind(port).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("端口 [" + port + "]绑定成功");
                } else {
                    System.out.println("端口 [" + port + "]绑定失败");
                    bind(serverBootstrap, port);
                }
            }
        });
    }

    /**
     * handler 用于指定服务端启动过程中的一些逻辑
     * 比较 childHandler() 用于指定处理新连接数据的读写处理逻辑
     */
    private static void handler(final ServerBootstrap serverBootstrap) {
        serverBootstrap.handler(new ChannelInitializer<NioServerSocketChannel>() {
            @Override
            protected void initChannel(NioServerSocketChannel ch) throws Exception {
                System.out.println("服务端启动中");
            }
        });
    }

    /**
     * 可以给服务端 channel 也就是 NioServerSocketChannel 指定一些自定义属性，然后通过 channel.attr() 取出这个属性
     */
    private static void attr(final ServerBootstrap serverBootstrap) {
        serverBootstrap.attr(AttributeKey.newInstance("serverName"), "nettyServer");
    }

    /**
     * 可以给每一条连接指定自定义属性，然后我们可以通过 channel.attr() 取出该属性
     */
    private static void childAttr(final ServerBootstrap serverBootstrap){
        serverBootstrap.childAttr(AttributeKey.newInstance("clientKey"),"clientValue");
    }

    /**
     *
     * @param serverBootstrap
     */
    private static void childOption(final ServerBootstrap serverBootstrap){

    }


}
