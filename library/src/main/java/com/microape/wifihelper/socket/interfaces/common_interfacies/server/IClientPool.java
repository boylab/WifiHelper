package com.microape.wifihelper.socket.interfaces.common_interfacies.server;


import com.microape.wifihelper.socket.core.iocore.interfaces.ISendable;

public interface IClientPool<T, K> {

    void cache(T t);

    T findByUniqueTag(K key);

    int size();

    void sendToAll(ISendable sendable);
}
