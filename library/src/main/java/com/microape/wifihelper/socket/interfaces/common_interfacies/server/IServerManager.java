package com.microape.wifihelper.socket.interfaces.common_interfacies.server;

import com.microape.wifihelper.socket.core.iocore.interfaces.IIOCoreOptions;

public interface IServerManager<E extends IIOCoreOptions> extends IServerShutdown {

    void listen();

    void listen(E options);

    boolean isLive();

    IClientPool<String, IClient> getClientPool();
}
