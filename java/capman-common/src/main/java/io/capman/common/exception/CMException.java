package io.capman.common.exception;

/**
 * Created by flying on 7/9/16.
 * base class of Capman Exceptions
 */
public class CMException extends Exception{

    public CMException(){
        super();
    }

    public CMException(String message){
        super(message);
    }

    public CMException(String message,Throwable cause){
        super(message,cause);
    }

    public CMException(Throwable cause){
        super(cause);
    }
}
