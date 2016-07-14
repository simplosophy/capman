package io.capman.common.client;

import io.capman.common.app.App;
import io.capman.protobuf.Internal;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by flying on 5/30/16.
 */
public abstract class AbstractClient implements Client {

    //seq -> req
    private final Map<Integer, Internal.InternalRequest> requestMap = new HashMap<Integer,Internal.InternalRequest>();
    private ChannelGroup channels;
    private ClientUriConfig config;
    Bootstrap bootstrap = new Bootstrap();

    public EventLoopGroup getWorkerGroup(){
        return null;
    }


    public AbstractClient(ClientUriConfig config){
        this.config = config;
    }

    protected Bootstrap initBootstrap(Bootstrap bootstrap){
        bootstrap
                .group(getWorkerGroup()==null? App.getInstance().getWorkerGroup(): getWorkerGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, config.isSockKeepAlive())
                .option(ChannelOption.TCP_NODELAY, config.isSockTcpNoDelay())
                .option(ChannelOption.SO_TIMEOUT, config.getSockTimeout())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("frameDecoder",new ProtobufVarint32FrameDecoder())
                                .addLast("protobufDecoder", new ProtobufDecoder(Internal.InternalResponse.getDefaultInstance()))
                                //encode
                                .addLast("frameEncoder",new ProtobufVarint32LengthFieldPrepender())
                                .addLast("protobufEncoder", new ProtobufEncoder())
                        ;

                    }
                });
        return bootstrap.remoteAddress(config.getHost(), config.getPort());
    }

    public void open() {
        bootstrap = initBootstrap(new Bootstrap());
    }

    public void connect(){
        for (int i = 0; i < config.getConnectionNum(); i++) {
            channels.add( bootstrap.connect().syncUninterruptibly().channel());
        }
    }

    public void close() {
        channels.close().syncUninterruptibly();
        channels.clear();
    }

    public boolean isAlive() {
        for (Channel channel : channels) {
            if(channel == null || (!channel.isOpen())){
                return false;
            }
        }
        return true;
    }


    private static class RequestWrapper{

    }
}
