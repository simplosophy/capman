package io.capman.common.client.handler;

import io.capman.common.exception.CMException;
import io.capman.common.exception.CMIOException;
import io.capman.common.exception.CMRemoteException;
import io.capman.common.util.Logger;
import io.capman.protobuf.Internal;
import io.netty.channel.*;
import io.netty.util.Timeout;
import io.netty.util.Timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by flying on 7/8/16.
 */
public class InternalClientHandler extends ChannelDuplexHandler implements RequestSender {

    private static final Logger LOGGER = Logger.get(InternalClientHandler.class);

    private final Channel nettyChannel;
    private final AtomicInteger seq = new AtomicInteger(0);
    private final Map<Integer, RequestWrapper> requestMap = new HashMap<Integer, RequestWrapper>();
    private CMException error;
    private Timer timer;

    public InternalClientHandler(Channel nettyChannel, Timer timer){
        this.nettyChannel = nettyChannel;
        this.timer = timer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof Internal.InternalResponse){
            onResponseReceived((Internal.InternalResponse) msg);
        }else {
            super.channelRead(ctx, msg);
        }
    }

    private void onResponseReceived(Internal.InternalResponse msg) {
        RequestWrapper wrapper = requestMap.remove(msg.getSeq());
        if (wrapper == null) {
            onError(new CMIOException("Bad sequence id of response: " + msg.getSeq()));
        } else {
            wrapper.getListener().onResponseReceived(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(!requestMap.isEmpty()){
            onError(new CMIOException("Client Is Disconnected By server"));
        }
        super.channelInactive(ctx);
    }

    public void sendAsynchronousRequest(final Internal.InternalRequest.Builder request, final RequestSender.Listener listener) throws CMException {
        // Ensure channel listeners are always called on the channel's I/O thread
        nettyChannel.eventLoop().execute(new Runnable() {
            public void run() {
                int sequ = seq.incrementAndGet();//seq per channel
                try {
                    request.setSeq(sequ);
                    final RequestWrapper requestWrapper = requestMap.put(sequ, new RequestWrapper(request, listener));
                    if (!nettyChannel.isOpen()) {
                        fireChannelErrorCallback(listener, new CMIOException("Channel Closed"));
                        return;
                    }
                    if (hasError()) {
                        fireChannelErrorCallback(
                                listener,
                                new CMIOException("Channel is in a bad state due to failing a previous request"));
                    }

                    ChannelFuture writeFuture = nettyChannel.write(request.build());
                    writeFuture.addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            //send finished
                            messageSent(future, requestWrapper);
                        }
                    });
                }catch (Throwable e){
                    // onError calls all registered listeners in the requestMap, but this request
                    // may not be registered yet. So we try to remove it (to make sure we don't call
                    // the callback twice) and then manually make the callback for this request
                    // listener.
                    requestMap.remove(sequ);
                    fireChannelErrorCallback(listener, wrapException(e));
                    onError(e);
                }

            }
        });
    }

    private void messageSent(ChannelFuture future, RequestWrapper request)
    {
        try {
            if (future.isSuccess()) {
//                cancelRequestTimeouts(request);
                request.getListener().onRequestSent();
            } else {
                CMIOException transportException =
                        new CMIOException("Sending request failed" , future.cause());
                onError(transportException);
            }
        }
        catch (Throwable t) {
            onError(t);
        }
    }

    public void close() {
        if(nettyChannel.isOpen()){
            nettyChannel.close();
        }
    }

    public boolean hasError() {
        return error != null;
    }

    public CMException getError() {
        return error;
    }

    protected void onError(Throwable e)
    {
        CMException exception;
        if(e instanceof  CMException){
            exception = (CMException) e;
        }else {
            exception = new CMIOException(e.getMessage(), e);
        }

        if (error == null) {
            error = exception;
        }

//        cancelAllTimeouts();

        //notify all listeners
        Collection<RequestWrapper> requests = new ArrayList<RequestWrapper>();
        requests.addAll(requestMap.values());
        requestMap.clear();
        for (RequestWrapper request : requests) {
            fireChannelErrorCallback(request.getListener(), error);
        }

        //close connection
        if (nettyChannel.isOpen())
        {
            nettyChannel.close();
        }
    }

    private void fireChannelErrorCallback(Listener listener, CMException exception)
    {
        try {
            listener.onChannelError(exception);
        }
        catch (Throwable t) {
            LOGGER.warn(t, "Channel error listener callback triggered an exception");
        }
    }

    private CMException wrapException(Throwable e){
        if(e instanceof CMException){
            return (CMException) e;
        }
        return new CMIOException(e.getMessage());
    }

    private static class  RequestWrapper{
        Internal.InternalRequest.Builder request;
        Listener listener;

        public Internal.InternalRequest.Builder getRequest() {
            return request;
        }

        public void setRequest(Internal.InternalRequest.Builder request) {
            this.request = request;
        }

        public Listener getListener() {
            return listener;
        }

        public void setListener(Listener listener) {
            this.listener = listener;
        }

        public RequestWrapper(Internal.InternalRequest.Builder request, Listener listener) {
            this.request = request;
            this.listener = listener;
        }
    }



}
