package io.capman.service;


import io.capman.app.App;
import io.capman.protobuf.Internal;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.List;

/**
 * Created by flying on 5/17/16.
 */
public abstract class AbstractService implements Service {

    ServiceUriConfig config;

    Thread serveThread;

    ChannelFuture channelFuture;

    boolean isOpen = false;

    public AbstractService(String uri){
        config = new ServiceUriConfig(uri);
    }

    public ServiceUriConfig getConfig() {
        return config;
    }

    public EventLoopGroup getBossGroup(){
        return null;
    }

    public EventLoopGroup getWorkerGroup(){
        return null;
    }

    public Class<? extends ServerChannel> getChannelClass(){
        return null;
    }

    public List<ChannelHandler> getChannelHandlers(){
        return null;
    }

    public abstract ServiceRpcHandler getServiceRpcHandler();


    public void open() {
        synchronized (this){
            if(isOpen){
                synchronized (this){
                    return;
                }
            }
        }
        new InternalRpcHandler(getServiceRpcHandler());//check handler first
        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(
                        getBossGroup() == null? App.getInstance().getBossGroup(): getBossGroup(),
                        getWorkerGroup() == null? App.getInstance().getWorkerGroup():getWorkerGroup())
                .channel(getChannelClass() == null? NioServerSocketChannel.class: getChannelClass())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        InternalRpcHandler internalRpcHandler = new InternalRpcHandler(getServiceRpcHandler());
                        ch.pipeline()
                                //decode
                                .addLast("frameDecoder",new ProtobufVarint32FrameDecoder())
                                .addLast("protobufDecoder", new ProtobufDecoder(Internal.InternalRequest.getDefaultInstance()))
                                //encode
                                .addLast("frameEncoder",new ProtobufVarint32LengthFieldPrepender())
                                .addLast("protobufEncoder", new ProtobufEncoder())
                                .addLast("rpcHandler", internalRpcHandler)
                        ;
                        if(getChannelHandlers() != null){
                            for (ChannelHandler handler : getChannelHandlers()) {
                                ch.pipeline().addLast("handler-" + handler.getClass().getSimpleName(),handler);
                            }
                        }
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, config.isSockKeepAlive())//server socket
                .option(ChannelOption.TCP_NODELAY, config.isSockTcpNoDelay())
                .childOption(ChannelOption.SO_KEEPALIVE, config.isSockKeepAlive())//incoming connections
                .childOption(ChannelOption.TCP_NODELAY, config.isSockTcpNoDelay())//incoming connections
        ;
        serveThread = new Thread(new Runnable() {
            public void run() {
                try {
                    channelFuture = bootstrap
                            .bind(config.getListenHost(), config.getPort())
                            .sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Service-Thread-" + this.getClass().getSimpleName());
        serveThread.start();
        isOpen = true;
    }

    public void close() {
        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(getBossGroup() != null){
            getBossGroup().shutdownGracefully();
        }
        if(getWorkerGroup() != null){
            getWorkerGroup().shutdownGracefully();
        }
    }
}
