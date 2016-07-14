package io.capman.common.exception;

/**
 * Created by flying on 7/9/16.
 * IO Exception
 */
public class CMIOException extends CMException{


    public CMIOException(String msg){
        super(msg);
    }

    public CMIOException(String msg, Throwable cause){
        super(msg,cause);
    }
}
