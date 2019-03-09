package com.microape.wifihelper.receiver;

/**
 * Created by pengle on 2018-11-23.
 * email:pengle609@163.com
 */
public class WiFiStatus {
    private boolean isEnabled = false;

    private boolean isScan = false;    //控制扫描流程

    private boolean isConn = false;

    private WiFiStatus() {

    }

    private static class SingletonInstance {
        private static final WiFiStatus INSTANCE = new WiFiStatus();
    }

    public static WiFiStatus newInstance() {

        return WiFiStatus.SingletonInstance.INSTANCE;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        if (!enabled){
            setScan(false);
            setConn(false);
        }
        isEnabled = enabled;
    }

    public boolean isScan() {
        return isScan;
    }

    public void setScan(boolean scan) {
        isScan = scan;
    }

    public boolean isConn() {
        return isConn;
    }

    public void setConn(boolean conn) {
        isConn = conn;
    }
}
