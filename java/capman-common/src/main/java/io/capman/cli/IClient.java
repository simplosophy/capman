package io.capman.cli;

/**
 * Created by flying on 5/30/16.
 */
public interface IClient {


    void init(ClientUriConfig config);

    void destroy();

}
