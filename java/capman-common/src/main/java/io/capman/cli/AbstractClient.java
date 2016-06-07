package io.capman.cli;

import io.capman.srv.AppContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by flying on 5/30/16.
 */
public abstract class AbstractClient implements  IClient{


    Bootstrap bootstrap;

    EventLoopGroup workerGroup;
    ChannelFuture channelFuture;



    public void init(ClientUriConfig config) {

        bootstrap = new Bootstrap();
        if(AppContext.getServiceUriConfig() != null){
            workerGroup = AppContext.getInstance().getClientWorkerGroup();//默认使用同一个EventLoopGroup
        }else {
            workerGroup = new NioEventLoopGroup( );
        }
        bootstrap.channel(NioSocketChannel.class);
//        ByteBufAllocator allocator = new PooledByteBufAllocator(true);
//        ByteBuf buffer = allocator.buffer(1024);
//
//        ReferenceCountUtil.release(buffer);



    }

    public void destroy() {

    }
}
