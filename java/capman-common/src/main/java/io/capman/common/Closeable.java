package io.capman.common;

import java.io.IOException;

/**
 * Created by flying on 7/3/16.
 */
public interface Closeable {
    void close()throws IOException;
}
