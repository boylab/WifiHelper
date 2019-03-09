package com.microape.wifihelper.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;

import com.microape.wifihelper.callback.OnWifiConnCallBack;
import com.microape.wifihelper.callback.OnWifiOpenCallBack;
import com.microape.wifihelper.callback.OnWifiScanCallBack;

/**
 * Created by pengle on 2018-11-23.
 * email:pengle609@163.com
 */
public class WiFiReceiverManager {

    private boolean registerTag = false;
    private WiFiStatus wiFiStatus = new WiFiStatus();
    private WiFiReceiver wiFiReceiver = new WiFiReceiver(wiFiStatus);

    public WiFiReceiverManager() {

    }

    public void registerWiFiReceiver(Context context){
        if (registerTag){
            return;
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(WiFiAction.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WiFiAction.SCAN_RESULTS_START_ACTION);
        filter.addAction(WiFiAction.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WiFiAction.ACTION_CONNECT_STARTED);
        filter.addAction(WiFiAction.ACTION_CONNECT_TIMEOUT);
        filter.addAction(WiFiAction.NETWORK_STATE_CONNECTED);
        filter.addAction(WiFiAction.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WiFiAction.NETWORK_STATE_CHANGED_ACTION);
        context.getApplicationContext().registerReceiver(wiFiReceiver, filter);
        registerTag = true;
    }

    public WiFiStatus getWiFiStatus() {
        return wiFiStatus;
    }

    public void setOpenCallBack(OnWifiOpenCallBack onWifiOpenCallBack) {
        wiFiReceiver.setOnWifiOpenCallBack(onWifiOpenCallBack);
    }

    public void setSearchCallBack(OnWifiScanCallBack onWifiScanCallBack) {
        wiFiReceiver.setOnWifiScanCallBack(onWifiScanCallBack);
    }

    public void setConnCallBack(OnWifiConnCallBack onWifiConnCallBack) {
        wiFiReceiver.setOnWifiConnCallBack(onWifiConnCallBack);
    }

    //开始连接WiFi的超时广播
    public void startConnectTimer(Context context, ScanResult wifiResult,  long delay) {
        Intent intent01 = new Intent(WiFiAction.ACTION_CONNECT_STARTED);
        intent01.putExtra(WiFiReceiver.TARGET_SSID, wifiResult.SSID);
        intent01.putExtra(WiFiReceiver.TARGET_BSSID, wifiResult.BSSID);
        context.sendBroadcast(intent01);

        Intent intent02 = new Intent(WiFiAction.ACTION_CONNECT_TIMEOUT);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent02,0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long time = System.currentTimeMillis() + delay;
        alarm.setExact(AlarmManager.RTC_WAKEUP, time, sender);
    }

    public void cancelConnectTimer(Context context) {
        Intent intent = new Intent(WiFiAction.ACTION_CONNECT_TIMEOUT);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent,0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(sender);

    }

    public void unRegisterWiFiReceiver(){
        try {
            if (registerTag){
                // TODO: 2018-11-23 似乎不需要注销
                //unregisterReceiver(wiFiReceiver);
                registerTag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
