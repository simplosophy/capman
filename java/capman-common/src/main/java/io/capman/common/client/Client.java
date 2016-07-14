package io.capman.common.client;

import io.capman.common.Closeable;
import io.capman.common.Openable;
import io.capman.common.exception.CMException;

import java.io.IOException;

/**
 * Created by flying on 5/30/16.
 */
public interface Client extends Closeable, Openable{

    ClientUriConfig getConfig();

    void open();
    void close();
    void connect();

    boolean isAlive();

    byte[] syncCall(String method, byte[] req) throws CMException;

    void asyncCall(String method, byte[] req, Callback callback);


    public interface Callback{
        void onSuccess(byte[] resp);
        void onFail(CMException e);
    }
}
