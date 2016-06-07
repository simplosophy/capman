package io.capman.srv;

import io.netty.channel.EventLoopGroup;

/**
 * Created by flying on 5/30/16.
 */
public class AppContext {


    EventLoopGroup clientWorkerGroup;

    private static final AppContext instance = new AppContext();

    public static AppContext getInstance() {
        return instance;
    }

    public EventLoopGroup getClientWorkerGroup() {
        return clientWorkerGroup;
    }

    private static ServiceUriConfig serviceUriConfig;

    public  static ServiceUriConfig getServiceUriConfig(){
        return serviceUriConfig;
    }

    public static void setServiceUriConfig(ServiceUriConfig serviceUriConfig) {
        AppContext.serviceUriConfig = serviceUriConfig;
    }
}
