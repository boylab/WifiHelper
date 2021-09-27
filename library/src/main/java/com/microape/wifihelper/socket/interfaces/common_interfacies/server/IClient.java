package com.microape.wifihelper.socket.interfaces.common_interfacies.server;

import com.microape.wifihelper.socket.core.protocol.IReaderProtocol;
import com.microape.wifihelper.socket.interfaces.common_interfacies.client.IDisConnectable;
import com.microape.wifihelper.socket.interfaces.common_interfacies.client.ISender;

import java.io.Serializable;

public interface IClient extends IDisConnectable, ISender<IClient>, Serializable {

    String getHostIp();

    String getHostName();

    String getUniqueTag();

    void setReaderProtocol(IReaderProtocol protocol);

    void addIOCallback(IClientIOCallback clientIOCallback);

    void removeIOCallback(IClientIOCallback clientIOCallback);

    void removeAllIOCallback();

}
