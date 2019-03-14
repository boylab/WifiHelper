package com.microape.wifihelper.socket.common.interfaces.common_interfacies.server;


import com.microape.wifihelper.socket.core.iocore.interfaces.ISendable;
import com.microape.wifihelper.socket.core.pojo.OriginalData;

public interface IClientIOCallback {

    void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool);

    void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool);

}
