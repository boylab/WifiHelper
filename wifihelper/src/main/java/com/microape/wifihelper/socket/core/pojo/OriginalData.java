package com.microape.wifihelper.socket.core.pojo;

import java.io.Serializable;

/**
 * 原始数据结构体
 * Created by xuhao on 2017/5/16.
 */
public final class OriginalData implements Serializable {
    /**
     * 原始数据包头字节数组
     */
    private byte[] mHeadBytes;
    /**
     * 原始数据包体字节数组
     */
    private byte[] mBodyBytes;

    public byte[] getHeadBytes() {
        return mHeadBytes;
    }

    public void setHeadBytes(byte[] headBytes) {
        mHeadBytes = headBytes;
    }

    public void setHeadBytes(int headBytes) {
        this.mHeadBytes = new byte[4];
        mHeadBytes[0] = (byte) (headBytes & 0xFF);
        mHeadBytes[0] = (byte) ((headBytes >> 8) & 0xFF);
        mHeadBytes[0] = (byte) ((headBytes >> 16) & 0xFF);
        mHeadBytes[0] = (byte) ((headBytes >> 24) & 0xFF);
    }

    public byte[] getBodyBytes() {
        return mBodyBytes;
    }

    public void setBodyBytes(byte[] bodyBytes) {
        mBodyBytes = bodyBytes;
    }
}
