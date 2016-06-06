package io.capman.srv;

import io.capman.conf.URIConfig;
import io.netty.channel.EventLoopGroup;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by flying on 5/17/16.
 */
public class ServiceUriConfig extends URIConfig {


    EventLoopGroup workerGroup;


    public ServiceUriConfig(String u) throws  URISyntaxException {
        super(u);
    }


    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }
}
