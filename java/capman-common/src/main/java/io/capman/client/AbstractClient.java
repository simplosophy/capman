package io.capman.client;

import io.capman.app.App;
import io.capman.protobuf.Internal;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * Created by flying on 5/30/16.
 */
public abstract class AbstractClient implements Client {



    EventLoopGroup workerGroup;
    ChannelFuture channelFuture;

    public EventLoopGroup getWorkerGroup(){
        return null;
    }


    public void open() {

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(getWorkerGroup()==null? App.getInstance().getWorkerGroup(): getWorkerGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("frameDecoder",new ProtobufVarint32FrameDecoder())
                                .addLast("protobufDecoder", new ProtobufDecoder(Internal.InternalResponse.getDefaultInstance()))
                                //encode
                                .addLast("frameEncoder",new ProtobufVarint32LengthFieldPrepender())
                                .addLast("protobufEncoder", new ProtobufEncoder())
//                                .addLast("clientHandler", new TestClientHandler())
                        ;

                    }
                });

//        Channel channel = bootstrap.connect("127.0.0.1", 2222).sync().channel();


    }

    public void close() {

    }
}
