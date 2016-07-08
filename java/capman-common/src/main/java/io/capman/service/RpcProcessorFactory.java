package io.capman.service;


import com.google.protobuf.MessageLite;

/**
 * Created by flying on 7/4/16.
 */
public interface RpcProcessorFactory {


    <ReqT extends MessageLite, RespT extends MessageLite> RpcProcessor<ReqT, RespT> getProcessor(
            String rpcMethod
    );


}
