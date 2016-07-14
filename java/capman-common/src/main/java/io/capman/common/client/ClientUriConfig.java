package io.capman.common.client;

import io.capman.common.conf.URIConfig;

/**
 * Created by flying on 5/17/16.
 */
public class ClientUriConfig extends URIConfig{

    private boolean useZookeeper=true;
    private String host;
    private int port;
    private boolean autoReconnect;
    private boolean sockKeepAlive;
    private boolean sockTcpNoDelay;
    private int sockTimeout;
    private int connectionNum;

    public ClientUriConfig(String u)  {
        super(u);
        useZookeeper = super.uri.getScheme().equalsIgnoreCase("zookeeper");
        if(!useZookeeper){
            host = super.uri.getHost();
            port = super.uri.getPort();
        }else {
            //use zookeeper to initialize clientgroup
        }
        sockKeepAlive = getParamBoolean("sockKeepAlive", true);
        sockTcpNoDelay = getParamBoolean("sockTcpNoDelay", true);
        autoReconnect = getParamBoolean("autoReconnect", false);
        sockTimeout = getParamInt("sockTimeout", 0);
        connectionNum = getParamInt("connectionNum", 1);
    }

    public boolean isUseZookeeper() {
        return useZookeeper;
    }

    public int getSockTimeout() {
        return sockTimeout;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public int getConnectionNum() {
        return connectionNum;
    }

    public String getHost() {
        return host;
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
