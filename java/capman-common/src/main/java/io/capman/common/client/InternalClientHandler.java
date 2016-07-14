package io.capman.common.client;

import io.capman.protobuf.Internal;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by flying on 7/6/16.
 */
public class InternalClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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
