package io.capman.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import io.capman.app.App;
import io.capman.protobuf.Internal;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by flying on 7/5/16.
 */
public class ServiceTest {

    private class TestService extends AbstractService{

        public TestService() {
            super("tcp://0.0.0.0:2222/");
        }

        @Override
        public ServiceRpcHandler getServiceRpcHandler() {
            return new ServiceRpcHandler() {

                @RpcMethod
                public Internal.TestMessage doRpc(RpcRequestContext context, Internal.TestMessage request ){
                    System.out.println(context);
                    return request;
                }

            };
        }
    }


    @Test
    public void runServer()throws Exception{


        Service s = new TestService();
        App.newBuilder()
                .addService(s)
                .build().start();
        System.out.println("service started...");



        while (true){
            Thread.sleep(1000);
        }
    }


    private class TestClientHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            System.out.println("client read msg: " + msg);
            Internal.InternalResponse resp = (Internal.InternalResponse) msg;
            if((resp).getRet() != 0){
                System.err.println("error code: " + resp.getRet() + "  msg: " + resp.getMsg());
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    @Test
    public void runClient()throws Exception{

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
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
                                .addLast("handler", new TestClientHandler())
                        ;

                    }
                });

        Channel channel = bootstrap.connect("127.0.0.1", 2222).sync().channel();

        Internal.InternalRequest.Builder req = Internal.InternalRequest.newBuilder()
                .setUin(12)
                .setMethod("doRpc")
                .setReqData(
                        Internal.TestMessage.newBuilder().setF1(123).setF2("test msg").build().toByteString()
                );

        channel.writeAndFlush(req.build());
        Thread.sleep(10000);



    }

}