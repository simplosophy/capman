package io.capman.srv;

/**
 * Created by flying on 5/30/16.
 */
public class ServerContext {

    private static ServiceUriConfig serviceUriConfig;

    public  static ServiceUriConfig getServiceUriConfig(){
        return serviceUriConfig;
    }

    public static void setServiceUriConfig(ServiceUriConfig serviceUriConfig) {
        ServerContext.serviceUriConfig = serviceUriConfig;
    }
}
