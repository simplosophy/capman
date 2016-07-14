package io.capman.service.common;

import io.capman.common.app.App;
import io.capman.common.service.*;
import io.capman.protobuf.Internal;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.junit.Test;

/**
 * Created by flying on 7/5/16.
 */
public class ServiceTest {

    static RpcProcessorFactory handler;
    static {
        handler =
                new RpcProcessorFactory() {

                    public RpcProcessor getProcessor(String rpcMethod) {
                        return new RpcProcessor() {

                            public byte[] process(byte[] req) throws BizException {
                                return req;
                            }
                        };
                    }

//                    @RpcMethod
//                    public Internal.TestMessage doRpc(Internal.TestMessage request ){
//                        System.out.println(RequestContexts.current());
//                        return request;
//                    }

                };
    }

    public class TestService extends AbstractService {

        public TestService(String uri) {
            super(uri);
        }

        @Override
        public RpcProcessorFactory getProcessorFactory() {
            return handler;
        }
    }

//    public class ProtoTestService extends Abstra


    @Test
    public void runServer()throws Exception{


        Service s = new TestService("tcp://0.0.0.0:2222/?logLevel=debug");
        App.newBuilder()
                .addService(s)
                .addService(new TestService("tcp://0.0.0.0:2223"))
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
        Channel channel2 = bootstrap.connect("127.0.0.1", 2222).sync().channel();

        System.out.println(channel.id());
        System.out.println(channel2.id());

        Internal.InternalRequest.Builder req = Internal.InternalRequest.newBuilder()
                .setUin(12)
                .setMethod("doRpc")
                .setSeq(1)
                .setReqData(
                        Internal.TestMessage.newBuilder().setF1(123).setF2("test msg").build().toByteString()
                );

        channel.writeAndFlush(req.build());
        channel2.writeAndFlush(req.build());
        Thread.sleep(10000);



    }

}