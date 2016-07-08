package io.capman.service;

import com.google.protobuf.MessageLite;

/**
 * Created by flying on 7/8/16.
 */
public interface RpcProcessor<ReqT extends MessageLite, RespT extends MessageLite> {

    ReqT getRequestDefaultInstance();

    RespT process(ReqT req)throws BizException;
}
