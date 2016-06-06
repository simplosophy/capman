package io.capman.cli;

import io.capman.conf.URIConfig;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * Created by flying on 5/17/16.
 */
public class ClientUriConfig extends URIConfig{

    public final static String Connections = "connections" ;

    public ClientUriConfig(String u) throws UnsupportedEncodingException, URISyntaxException {
        super(u);
    }

}
