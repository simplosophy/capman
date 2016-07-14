package io.capman.common.exception;

/**
 * Created by flying on 7/9/16.
 *  Remote Server Exception
 */
public class CMRemoteException extends CMException{
    private int code;

    private CMRemoteException(int code, String message){
        super(message);
        this.code = code;
    }
}
