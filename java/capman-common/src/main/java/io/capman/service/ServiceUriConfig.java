package io.capman.service;

import io.capman.conf.URIConfig;
import io.netty.util.Timer;
import io.netty.util.concurrent.DefaultEventExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by flying on 5/17/16.
 */
public class ServiceUriConfig extends URIConfig {


    private boolean useZookeeper=true;
    private String listenHost;
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
            listenHost = getParamString("listenHost", "0.0.0.0");
            port = getParamInt("port", 0);
        }else {
            listenHost = super.uri.getHost();
            port = super.uri.getPort();
        }

        sockKeepAlive = getParamBoolean("sockKeepAlive", true);
        sockTcpNoDelay = getParamBoolean("sockTcpNoDelay", true);
        logLevel = getParamString("logLevel", null);
        clientIdleSeconds = getParamInt("clientIdleSeconds",0);
        taskTimeoutMillis = getParamLong("taskTimeoutMillis",0);
        queueTimeoutMillis = getParamLong("queueTimeoutMillis", 0);

        executor = new ThreadPoolExecutor(100,100, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(1024));
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
