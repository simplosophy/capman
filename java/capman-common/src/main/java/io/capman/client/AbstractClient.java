package io.capman.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by flying on 5/30/16.
 */
public abstract class AbstractClient implements Client {


    Bootstrap bootstrap;

    EventLoopGroup workerGroup;
    ChannelFuture channelFuture;



    public void open() {

        bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);



    }

    public void close() {

    }
}
