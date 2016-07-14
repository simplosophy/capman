package io.capman.common.service;

/**
 * Created by flying on 7/8/16.
 */
public interface RpcProcessor {

    byte[] process(byte[] req)throws BizException;

}
