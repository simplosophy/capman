package io.capman.service;

import io.capman.Closeable;
import io.capman.Openable;

/**
 * Created by flying on 5/30/16.
 */
public interface Service extends Closeable, Openable {

    void open();
    void close();
    ServiceUriConfig getConfig();

}
