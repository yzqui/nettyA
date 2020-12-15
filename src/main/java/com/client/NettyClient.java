package com.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Date;

public class NettyClient {

    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        // group 对应 IOClient.java 中 main 函数中的线程
        NioEventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                });
        Channel channel = bootstrap.connect("127.0.0.1", 8003).channel();

        while (true){
            channel.writeAndFlush(new Date() + ": Hello world");
            Thread.sleep(3000);
        }

    }

}
