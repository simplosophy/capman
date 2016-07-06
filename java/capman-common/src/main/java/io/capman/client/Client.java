package io.capman.client;

import io.capman.Closeable;
import io.capman.Openable;

/**
 * Created by flying on 5/30/16.
 */
public interface Client extends Closeable, Openable{


    ClientUriConfig getConfig();

    void open();
    void close();
    boolean isAlive();

}
