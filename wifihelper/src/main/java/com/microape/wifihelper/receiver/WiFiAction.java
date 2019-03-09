package com.microape.wifihelper.receiver;

import android.net.wifi.WifiManager;

public class WiFiAction {

    /**
     * wifi开关变化广播
     */
    public static final String WIFI_STATE_CHANGED_ACTION = WifiManager.WIFI_STATE_CHANGED_ACTION;


    /**
     * 热点开始扫描通知广播
     * 热点扫描结果通知广播
     */
    public static final String SCAN_RESULTS_START_ACTION = "android.net.wifi.SCANING_RESULTS";
    public static final String SCAN_RESULTS_AVAILABLE_ACTION = WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;

    /**
     * 开始连接指定WiFi
     * 连接指定WiFi超时
     */
    public static final String ACTION_CONNECT_STARTED = "android.net.wifi.STATE_CONNECT_STARTED";
    public static final String ACTION_CONNECT_TIMEOUT = "android.net.wifi.STATE_CONNECT_TIMEOUT";

    private static boolean isConnecting = false;

    /**
     * 已经连接上指定WiFi
     */
    public static final String NETWORK_STATE_CONNECTED = "android.net.wifi.STATE_CONNECTED";

    /**
     * 热点连接结果通知广播
     * 网络状态变化广播（与上一广播协同完成连接过程通知）
     */
    public static final String SUPPLICANT_STATE_CHANGED_ACTION = WifiManager.SUPPLICANT_STATE_CHANGED_ACTION;
    public static final String NETWORK_STATE_CHANGED_ACTION = WifiManager.NETWORK_STATE_CHANGED_ACTION;

    private static boolean willConnect = false;
    private static boolean willDisConnect = false;

    public static boolean isConnecting() {
        return isConnecting;
    }

    public static void setIsConnecting(boolean isConnecting) {
        WiFiAction.isConnecting = isConnecting;
    }

    public static boolean isWillConnect() {
        return willConnect;
    }

    public static void setWillConnect(boolean willConnect) {
        WiFiAction.willConnect = willConnect;
    }

    public static boolean isWillDisConnect() {
        return willDisConnect;
    }

    public static void setWillDisConnect(boolean willDisConnect) {
        WiFiAction.willDisConnect = willDisConnect;
    }
}
