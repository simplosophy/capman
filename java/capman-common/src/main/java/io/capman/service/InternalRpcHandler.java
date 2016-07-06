package io.capman.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import io.capman.protobuf.Internal;
import io.capman.util.StringUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by flying on 7/4/16.
 */
@ChannelHandler.Sharable
public class InternalRpcHandler extends  ChannelInboundHandlerAdapter{

    Map<String, Method> rpcMethods = new HashMap<String, Method>();
    Map<String, MessageLite> rpcRequestDefaultMsg = new HashMap<String, MessageLite>();

    ServiceRpcHandler bizHandler;


    public InternalRpcHandler(ServiceRpcHandler bizHandler){
        this.bizHandler = bizHandler;
        Method[] methods = bizHandler.getClass().getMethods();
        for (Method method : methods) {
            RpcMethod rpcAnno = method.getAnnotation(RpcMethod.class);
            if(rpcAnno != null){
                String rpcName = rpcAnno.rpcName();
                if(StringUtils.isEmpty(rpcAnno.rpcName())){
                    rpcName = method.getName();
                }
                if(!MessageLite.class.isAssignableFrom( method.getReturnType())){
                    throw new RuntimeException("RpcMethod Must Return A Protobuf Message");
                }
                Class[] parameterTypes = method.getParameterTypes();
                if(parameterTypes.length != 2){
                    throw new RuntimeException("RpcMethod Must Have 2 Parameters: (RpcRequestContext, [Protobuf Message])");
                }
                if(! (RpcRequestContext.class == (parameterTypes[0]))){
                    throw new RuntimeException("RpcMethod Must Have 2 Parameters: (RpcRequestContext, [Protobuf Message])");
                }
                if(!MessageLite.class.isAssignableFrom(parameterTypes[1])){
                    throw new RuntimeException("RpcMethod Must Have 2 Parameters: (RpcRequestContext, [Protobuf Message])");
                }

                rpcMethods.put(rpcName, method);
                try {
                    Method getDefaultInstance = parameterTypes[1].getDeclaredMethod("getDefaultInstance");
                    rpcRequestDefaultMsg.put(rpcName, (MessageLite) getDefaultInstance.invoke(null));
                } catch (Exception e) {
                    throw new RuntimeException("RpcMethod Must Have 2 Parameters: (RpcRequestContext, [Protobuf Message])");
                }
            }
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Internal.InternalResponse.Builder response = Internal.InternalResponse.newBuilder();
        MessageLite bizRet = null;
        int ret = 0;
        try {
            Internal.InternalRequest req = (Internal.InternalRequest) msg;
            RpcRequestContext context = new RpcRequestContext();
            context.setUin(req.getUin());
            context.setRemoteAddress(ctx.channel().remoteAddress());
            context.setMethod(req.getMethod());

            Method method = rpcMethods.get(req.getMethod());
            if (method == null) {
                ret = Internal.EnumInternalRet.EnumInternalRet_METHOD_NOT_FOUND_VALUE;
            } else {
                //business
                try {
                    bizRet = (MessageLite) method.invoke(
                            bizHandler,
                            context,
                            rpcRequestDefaultMsg.get(req.getMethod()).getParserForType()
                                    .parseFrom(req.getReqData()));

                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    ret = Internal.EnumInternalRet.EnumInternalRet_BIZ_ERROR_VALUE;
                    if(cause != null && cause.getMessage() != null){
                        response.setMsg(cause.getMessage());
                    }
                }
            }
        }catch (InvalidProtocolBufferException e){
            ret = Internal.EnumInternalRet.EnumInternalRet_PARAMETER_EXCEPTION_VALUE;
            response.setMsg("InvalidProtocolBufferException: " + e.getMessage());
        }
        catch (Throwable e){
            ret = Internal.EnumInternalRet.EnumInternalRet_INTERNAL_ERROR_VALUE;
            response.setMsg("Service Internal Error");
        }
        finally {
            if (bizRet == null) {
                response.setRespData(ByteString.EMPTY);
            } else {
                response.setRespData(bizRet.toByteString());
            }
            ctx.writeAndFlush(response
                    .setRet(ret)
                    .build());
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active.");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel registered.");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel unregistered.");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
