package io.capman.common.service;


/**
 * Created by flying on 7/4/16.
 */
public interface RpcProcessorFactory {


     RpcProcessor getProcessor(
            String rpcMethod
    );


}
