package com.microape.wifihelper.socket.interfaces.common_interfacies.server;


import com.microape.wifihelper.socket.core.iocore.interfaces.IIOCoreOptions;


public interface IServerManagerPrivate<E extends IIOCoreOptions> extends IServerManager<E> {
    void initServerPrivate(int serverPort);
}
