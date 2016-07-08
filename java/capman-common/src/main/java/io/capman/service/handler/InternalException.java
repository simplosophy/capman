package io.capman.service.handler;

import io.capman.protobuf.Internal;

/**
 * Created by flying on 7/7/16.
 */
public class InternalException extends Exception{

    private int code;

    public static final InternalException BIZ_ERROR =
            new InternalException("SERVER BIZ PROCESS ERROR", Internal.EnumInternalRet.EnumInternalRet_BIZ_ERROR_VALUE);
    public static final InternalException METHOD_NOT_FOUND =
            new InternalException("RPC METHOD NOT FOUND", Internal.EnumInternalRet.EnumInternalRet_METHOD_NOT_FOUND_VALUE);
    public static final InternalException PARAMETER_EXCEPTION =
            new InternalException("PARAMETER ERROR", Internal.EnumInternalRet.EnumInternalRet_PARAMETER_EXCEPTION_VALUE);
    public static final InternalException INTERNAL_ERROR_EXCEPTION =
            new InternalException("INTERNAL ERROR", Internal.EnumInternalRet.EnumInternalRet_INTERNAL_ERROR_VALUE);

    public InternalException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
