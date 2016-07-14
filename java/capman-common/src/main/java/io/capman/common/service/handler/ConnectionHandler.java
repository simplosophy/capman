package io.capman.common.service.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by flying on 7/7/16.
 */
public class ConnectionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        HandlerState state = new HandlerState();
        state.setIncomeTime(System.currentTimeMillis());
        ctx.channel().attr(HandlerState.KEY).set(state);//new handler state
        super.channelActive(ctx);
    }

}
