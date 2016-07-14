package io.capman.common.service;

import io.capman.common.Closeable;
import io.capman.common.Openable;

/**
 * Created by flying on 5/30/16.
 */
public interface Service extends Closeable, Openable {

    void open();
    void close();
    ServiceUriConfig getConfig();

}
