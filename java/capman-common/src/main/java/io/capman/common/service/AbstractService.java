package io.capman.common.service;


import io.capman.common.app.App;
import io.capman.protobuf.Internal;
import io.capman.common.service.handler.ConnectionHandler;
import io.capman.common.service.handler.InternalDispatcher;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.Timer;

/**
 * Created by flying on 5/17/16.
 */
public abstract class AbstractService implements Service {

    ServiceUriConfig config;

    ChannelFuture channelFuture;

    volatile boolean isOpen = false;

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


    public abstract RpcProcessorFactory getProcessorFactory();

    private LogLevel getLogLevel(String level){
        if("debug".equalsIgnoreCase(level)){
            return LogLevel.DEBUG;
        }
        if("info".equalsIgnoreCase(level)){
            return LogLevel.INFO;
        }
        if("warn".equalsIgnoreCase(level)){
            return LogLevel.WARN;
        }
        if("error".equalsIgnoreCase(level)){
            return LogLevel.ERROR;
        }
        if("trace".equalsIgnoreCase(level)){
            return LogLevel.TRACE;
        }
        return LogLevel.INFO;
    }


    protected void checkProcessorFactory(RpcProcessorFactory processorFactory){
        if(processorFactory == null){
            throw new RuntimeException("RpcProcessorFactory is NULL");
        }
    }

    /**
     * a timer to check task timeout
     * @return
     */
    public Timer getTimer(){
        return null;
    }


    public void open() {
        synchronized (this){
            if(isOpen){
                synchronized (this){
                    return;
                }
            }
        }
        checkProcessorFactory(getProcessorFactory());
        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(
                        getBossGroup() == null? App.getInstance().getBossGroup(): getBossGroup(),
                        getWorkerGroup() == null? App.getInstance().getWorkerGroup():getWorkerGroup())
                .channel(getChannelClass() == null? NioServerSocketChannel.class: getChannelClass())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addFirst("capmanConnectionHandler", new ConnectionHandler());
                        //decode
                        p.addLast("frameDecoder",new ProtobufVarint32FrameDecoder());
                        p.addLast("protobufDecoder", new ProtobufDecoder(Internal.InternalRequest.getDefaultInstance()));
                        //encode
                        p.addLast("frameEncoder",new ProtobufVarint32LengthFieldPrepender());
                        p.addLast("protobufEncoder", new ProtobufEncoder());

                        if(config.getClientIdleSeconds() > 0){
                            p.addLast("idleTimeoutHandler", new IdleStateHandler(config.getClientIdleSeconds(), 0,0 ));
                        }

                        p.addLast("capmanServiceDispatcher", new InternalDispatcher(
                                getProcessorFactory(),
                                config.getExecutor(),
                                getTimer()==null?App.getInstance().getTimer():getTimer(),
                                config.getTaskTimeoutMillis(),
                                config.getQueueTimeoutMillis()
                        ));
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, config.isSockKeepAlive())//server socket
                .option(ChannelOption.TCP_NODELAY, config.isSockTcpNoDelay())
                .childOption(ChannelOption.SO_KEEPALIVE, config.isSockKeepAlive())//incoming connections
                .childOption(ChannelOption.TCP_NODELAY, config.isSockTcpNoDelay())//incoming connections
        ;

        if(config.getLogLevel() != null){
            bootstrap.handler(new LoggingHandler("logHandler",getLogLevel(config.getLogLevel())));
        }
        //biz executor run service.
        config.getExecutor().execute(
                new Runnable() {
                    public void run() {
                        channelFuture = bootstrap
                                .bind(config.getHost(), config.getPort())
                                .syncUninterruptibly();
                    }
                }
        );
        isOpen = true;
    }



    public void close() {
        channelFuture.channel().closeFuture()
                .addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        System.err.println("Service Stopped: " + config.getURI());
                    }
                })
                .syncUninterruptibly();
    }
}
