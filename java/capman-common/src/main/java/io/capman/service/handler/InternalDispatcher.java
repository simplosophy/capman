package io.capman.service.handler;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import io.capman.protobuf.Internal;
import io.capman.service.*;
import io.capman.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by flying on 7/4/16.
 */
public class InternalDispatcher extends  ChannelInboundHandlerAdapter{

//    Map<String, Method> rpcMethods = new HashMap<String, Method>();
//    Map<String, MessageLite> rpcRequestDefaultMsg = new HashMap<String, MessageLite>();
    RpcProcessorFactory processorFactory;
    private final Executor executor;
    private final Timer taskTimeoutTimer;
    private final long taskTimeoutMillis;
    private final long queueTimeoutMillis;


    public InternalDispatcher(
            RpcProcessorFactory processorFactory,
            Executor executor,
            Timer taskTimeoutTimer,
            long taskTimeoutMillis,
            long queueTimeoutMillis
            ){
        this.processorFactory = processorFactory;
        this.executor = executor;
        this.taskTimeoutTimer = taskTimeoutTimer;
        this.taskTimeoutMillis = taskTimeoutMillis;
        this.queueTimeoutMillis = queueTimeoutMillis;



//        Method[] methods = processorFactory.getClass().getMethods();
//        for (Method method : methods) {
//            RpcMethod rpcAnno = method.getAnnotation(RpcMethod.class);
//            if(rpcAnno != null){
//                String rpcName = rpcAnno.rpcName();
//                if(StringUtils.isEmpty(rpcAnno.rpcName())){
//                    rpcName = method.getName();
//                }
//                if(!MessageLite.class.isAssignableFrom( method.getReturnType())){
//                    throw new RuntimeException("RpcMethod Must Return A Protobuf Message");
//                }
//                Class[] parameterTypes = method.getParameterTypes();
//                if(parameterTypes.length != 1){
//                    throw new RuntimeException("RpcMethod Must Have 1 Parameters: ([Protobuf Message])");
//                }
//                rpcMethods.put(rpcName, method);
//                try {//put a default message there.
//                    Method getDefaultInstance = parameterTypes[0].getDeclaredMethod("getDefaultInstance");
//                    rpcRequestDefaultMsg.put(rpcName, (MessageLite) getDefaultInstance.invoke(null));
//                } catch (Exception e) {
//                    throw new RuntimeException("RpcMethod Must Have 1 Parameters: ([Protobuf Message])");
//                }
//            }
//        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HandlerState handlerState = ctx.channel().attr(HandlerState.KEY).get();
        handlerState.setBizLogicTime(System.currentTimeMillis());
        if(msg instanceof Internal.InternalRequest){
            processReq(ctx, handlerState, (Internal.InternalRequest) msg);
        }else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        super.exceptionCaught(ctx, cause);
    }

    private Internal.InternalResponse process(Internal.InternalRequest req){
        Internal.InternalResponse.Builder response = Internal.InternalResponse.newBuilder();
        response.setSeq(req.getSeq());

        RpcProcessor<MessageLite, MessageLite> processor = processorFactory.getProcessor(req.getMethod());

        int ret = 0;
        if (processor == null) {
            ret = Internal.EnumInternalRet.EnumInternalRet_METHOD_NOT_FOUND_VALUE;
            response.setMsg("Processor for RPC Method Not Found: " + req.getMethod());
        } else {
            try {
                //business
                MessageLite bizRet = processor.process(
                        processor.getRequestDefaultInstance().getParserForType().parseFrom(req.getReqData()));
                response.setRespData(bizRet.toByteString());
            } catch (BizException e) {
                ret = Internal.EnumInternalRet.EnumInternalRet_BIZ_ERROR_VALUE;
                response.setMsg(e.getMessage());
            } catch (InvalidProtocolBufferException e) {
                ret = Internal.EnumInternalRet.EnumInternalRet_PARAMETER_EXCEPTION_VALUE;
                response.setMsg( "InvalidProtocolBufferException: " + e.getMessage());
            } catch (Throwable e) {
                ret = Internal.EnumInternalRet.EnumInternalRet_INTERNAL_ERROR_VALUE;
                response.setMsg("Service Internal Error");
            }
        }
        return response.setRet(ret).build();
    }

    private void processReq(
            final ChannelHandlerContext ctx,
            final HandlerState handlerState,
            final Internal.InternalRequest req
    )
    {
        try {
            executor.execute(new Runnable() {
                public void run() {
                    ListenableFuture<Internal.InternalResponse> processFuture;
                    final AtomicBoolean responseSent = new AtomicBoolean(false);
                    // Use AtomicReference as a generic holder class to be able to mark it final
                    // and pass into inner classes. Since we only use .get() and .set(), we don't
                    // actually do any atomic operations.
                    final AtomicReference<Timeout> expireTimeout = new AtomicReference<Timeout>(null);

                    RequestContext reqContext = new RequestContext();
                    reqContext.setUin(req.getUin());
                    reqContext.setMethod(req.getMethod());
                    reqContext.setRemoteAddress(ctx.channel().remoteAddress());
                    //current thread context
                    RequestContexts.setRequestContext(reqContext);


                    try{
                        long timeRemaining = 0;
                        //queued time
                        long timeElapsed = System.currentTimeMillis() - handlerState.getBizLogicTime();
                        if(queueTimeoutMillis > 0){
                            if (timeElapsed >= queueTimeoutMillis) {
                                //queued timeout, send error
                                throw new InternalException(
                                        "Task stayed on the queue for " + timeElapsed +
                                                " milliseconds, exceeding configured queue timeout of " + queueTimeoutMillis +
                                                " milliseconds."
                                        , Internal.EnumInternalRet.EnumInternalRet_INTERNAL_QUEUE_TIMEOUT_VALUE);
                            }
                        }else if(taskTimeoutMillis > 0){
                            if(timeElapsed >= taskTimeoutMillis){
                                //task timeout
                                throw new InternalException(
                                        "Task stayed on the queue for " + timeElapsed +
                                                " milliseconds, exceeding configured task timeout of " + taskTimeoutMillis +
                                                " milliseconds."
                                        , Internal.EnumInternalRet.EnumInternalRet_INTERNAL_TASK_TIMEOUT_VALUE);
                            }else {
                                timeRemaining = taskTimeoutMillis - timeElapsed;
                            }
                        }

                        if(timeRemaining > 0){//need a timeout checker
                            expireTimeout.set(taskTimeoutTimer.newTimeout(new TimerTask() {
                                public void run(Timeout timeout) throws Exception {
                                    // The immediateFuture returned by processors isn't cancellable, cancel() and
                                    // isCanceled() always return false. Use a flag to detect task expiration.
                                    if (responseSent.compareAndSet(false, true)) {
                                        //task timeout
                                        Internal.InternalResponse resp = Internal.InternalResponse
                                                .newBuilder()
                                                .setRet(Internal.EnumInternalRet.EnumInternalRet_INTERNAL_TASK_TIMEOUT_VALUE)
                                                .setSeq(req.getSeq())
                                                .setMsg("Task Process Timeout")
                                                .build();
                                        writeResponse(ctx, resp,handlerState);
                                    }
                                }
                            }, timeRemaining, TimeUnit.MILLISECONDS));
                        }

                        //biz process future

                        processFuture = Futures.immediateFuture(
                                process(req)
                        );


                    }catch (InternalException ie){
                        //write exception
                        Internal.InternalResponse resp = Internal.InternalResponse
                                .newBuilder()
                                .setRet(ie.getCode())
                                .setSeq(req.getSeq())
                                .setMsg(ie.getMessage())
                                .build();
                        writeResponse(ctx, resp,handlerState);
                        return;
                    }
                    finally {
                        //do NOT forget. once biz logic is finished, context must be removed
                        RequestContexts.removeCurrentContext();
                    }
                    Futures.addCallback(
                            processFuture,
                            new FutureCallback<Internal.InternalResponse>() {
                                public void onSuccess(Internal.InternalResponse internalResponse) {
                                    deleteExpirationTimer(expireTimeout.get());
                                    //send response
                                    try{
                                        // Only write response if the client is still there and the task timeout
                                        // hasn't expired.
                                        if (ctx.channel().isOpen() && responseSent.compareAndSet(false, true)) {
                                            writeResponse(ctx, internalResponse,handlerState);
                                        }
                                    }catch (Throwable e){
                                        onFail(ctx, e);
                                    }

                                }
                                public void onFailure(Throwable throwable) {
                                    deleteExpirationTimer(expireTimeout.get());
                                    onFail(ctx, throwable);
                                }
                            }
                    );

                }
            });
        }catch (RejectedExecutionException ex){
            Internal.InternalResponse resp = Internal.InternalResponse
                    .newBuilder()
                    .setRet(Internal.EnumInternalRet.EnumInternalRet_INTERNAL_OVER_LOADED_VALUE)
                    .setSeq(req.getSeq())
                    .setMsg("Server Load Is Too High")
                    .build();
            writeResponse(ctx, resp,handlerState);
        }

    }

    private void writeResponse(ChannelHandlerContext ctx, Internal.InternalResponse resp, HandlerState handlerState){
        handlerState.setWriteStartTime(System.currentTimeMillis());
        ctx.writeAndFlush(resp);
        handlerState.setWriteEndTime(System.currentTimeMillis());
    }

    private void onFail(ChannelHandlerContext ctx,Throwable throwable)
    {
        ctx.fireExceptionCaught(throwable);
        if (ctx.channel().isOpen()) {
            ctx.channel().close();
        }
    }

    private void deleteExpirationTimer(Timeout timeout)
    {
        if (timeout == null) {
            return;
        }
        timeout.cancel();
    }
}
