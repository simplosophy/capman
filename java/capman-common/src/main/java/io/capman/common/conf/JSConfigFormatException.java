package io.capman.common.conf;

/**
 * Created by flying on 6/7/16.
 */
public class JSConfigFormatException extends Exception{
    public JSConfigFormatException(String msg){
        super(msg);
    }
    public JSConfigFormatException(String msg, Throwable cause){
        super(msg,cause);
    }
}
