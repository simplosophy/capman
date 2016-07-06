package io.capman.service;

import java.net.SocketAddress;

/**
 * Created by flying on 7/5/16.
 */
public class RpcRequestContext {
    private int uin;
    private String method;
    private SocketAddress remoteAddress;

    public int getUin() {
        return uin;
    }

    public void setUin(int uin) {
        this.uin = uin;
    }

    public SocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setRemoteAddress(SocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    public String toString() {
        return "RpcRequestContext{" +
                "uin=" + uin +
                ", method='" + method + '\'' +
                ", remoteAddress=" + remoteAddress +
                '}';
    }
}
