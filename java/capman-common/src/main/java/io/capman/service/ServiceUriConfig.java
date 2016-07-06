package io.capman.service;

import io.capman.conf.URIConfig;

/**
 * Created by flying on 5/17/16.
 */
public class ServiceUriConfig extends URIConfig {


    private boolean useZookeeper=true;
    private String listenHost;
    private int port;
    private boolean sockKeepAlive;
    private boolean sockTcpNoDelay;

    public ServiceUriConfig(String u)  {
        super(u);
        useZookeeper = super.uri.getScheme().equalsIgnoreCase("zookeeper");
        if(useZookeeper){
            listenHost = getParamString("listenHost", "0.0.0.0");
            port = getParamInt("port", 0);
        }else {
            listenHost = super.uri.getHost();
            port = super.uri.getPort();
        }

        sockKeepAlive = getParamBoolean("sockKeepAlive", true);
        sockTcpNoDelay = getParamBoolean("sockTcpNoDelay", true);

    }

    public boolean isUseZookeeper() {
        return useZookeeper;
    }

    public String getListenHost() {
        return listenHost;
    }

    public int getPort() {
        return port;
    }

    public boolean isSockKeepAlive() {
        return sockKeepAlive;
    }

    public boolean isSockTcpNoDelay() {
        return sockTcpNoDelay;
    }
}
