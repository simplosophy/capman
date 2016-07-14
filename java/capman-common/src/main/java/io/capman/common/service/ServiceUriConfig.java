package io.capman.common.service;


import io.capman.common.conf.URIConfig;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by flying on 5/17/16.
 */
public class ServiceUriConfig extends URIConfig {


    private boolean useZookeeper=true;
    private String host;
    private int port;
    private boolean sockKeepAlive;
    private boolean sockTcpNoDelay;
    private String logLevel;
    private int clientIdleSeconds;//客户端空闲时间秒,超过时间将会被服务器关闭连接
    private long taskTimeoutMillis;
    private long queueTimeoutMillis;
    private Executor executor;//biz executor


    public ServiceUriConfig(String u)  {
        super(u);
        useZookeeper = super.uri.getScheme().equalsIgnoreCase("zookeeper");
        if(useZookeeper){
            host = getParamString("host", "0.0.0.0");
            port = getParamInt("port", 0);
        }else {
            host = super.uri.getHost();
            port = super.uri.getPort();
        }

        sockKeepAlive = getParamBoolean("sockKeepAlive", true);
        sockTcpNoDelay = getParamBoolean("sockTcpNoDelay", true);
        logLevel = getParamString("logLevel", null);
        clientIdleSeconds = getParamInt("clientIdleSeconds",0);
        taskTimeoutMillis = getParamLong("taskTimeoutMillis",0);
        queueTimeoutMillis = getParamLong("queueTimeoutMillis", 0);

        // TODO: 7/8/16  make executor configurable
        executor = new ThreadPoolExecutor(100,100, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(1024));
    }

    public boolean isUseZookeeper() {
        return useZookeeper;
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


    public long getTaskTimeoutMillis() {
        return taskTimeoutMillis;
    }

    public long getQueueTimeoutMillis() {
        return queueTimeoutMillis;
    }

    public boolean isSockTcpNoDelay() {
        return sockTcpNoDelay;
    }

    public int getClientIdleSeconds() {
        return clientIdleSeconds;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public Executor getExecutor() {
        return executor;
    }
}
