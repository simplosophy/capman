package io.capman.app;

import io.capman.client.Client;
import io.capman.service.Service;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by flying on 7/3/16.
 */
public class App {

    private EventLoopGroup workerGroup;
    private EventLoopGroup bossGroup;
    private List<Client> clientList;
    private List<Service> serviceList;
    private Timer timer;
    private App(){}
    private static final App instance = new App();
    private boolean built = false;

    public static App getInstance() {
        return instance;
    }

    public void stop(){
        for (Service service : serviceList) {
            service.close();
        }
        for (Client client : clientList) {
            client.close();
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    public Timer getTimer() {
        return timer;
    }

    public void start(){

//        final ChannelGroup channels =
//                new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        for (Client client : clientList) {
            client.open();
        }
        for (Service service : serviceList) {
            service.open();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    System.err.println("Shutting Down...");
                    stop();
                    System.err.println("Shut Down Gracefully.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));


    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    public static final class Builder{

        private EventLoopGroup workerGroup;
        private EventLoopGroup bossGroup;
        private Timer timer;
        private List<Client> clientList = new ArrayList<Client>();
        private List<Service> serviceList = new ArrayList<Service>(2);


        public Builder setWorderGroup(EventLoopGroup eventLoopGroup){
            workerGroup = eventLoopGroup;
            return this;
        }

        public Builder setBossGroup(EventLoopGroup bossGroup) {
            this.bossGroup = bossGroup;
            return this;
        }

        public Builder setTimer(Timer timer){
            this.timer = timer;
            return this;
        }

        public Builder addClient(Client client){
            clientList.add(client);
            return this;
        }


        public Builder addService(Service service){
            serviceList.add(service);
            return this;
        }

        public synchronized App build(){
            if(instance.built){
                throw new RuntimeException("You can build only ONE App instance.");
            }
            if(workerGroup == null){
                workerGroup = new NioEventLoopGroup();
            }
            instance.workerGroup = workerGroup;
            if(bossGroup == null){
                bossGroup = new NioEventLoopGroup();
            }
            if(timer == null){
                timer = new HashedWheelTimer();
            }
            instance.timer = timer;
            instance.bossGroup = bossGroup;
            instance.clientList = clientList;
            instance.serviceList = serviceList;

            instance.built = true;
            return instance;
        }

    }

}
