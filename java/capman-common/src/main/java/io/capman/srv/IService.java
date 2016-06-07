package io.capman.srv;

import io.capman.cli.ClientUriConfig;

/**
 * Created by flying on 5/30/16.
 */
public interface IService {


    void startUp(ServiceUriConfig config);

    void shutdown();

}
