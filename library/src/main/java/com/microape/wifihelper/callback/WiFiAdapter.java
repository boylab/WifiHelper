package com.microape.wifihelper.callback;

import android.net.wifi.ScanResult;

import com.microape.wifihelper.receiver.WiFiAction;
import com.microape.wifihelper.receiver.WiFiStatus;

import java.util.List;

/**
 *  * Author：pengl on 2019/3/13 15:18
 *  * Email ：pengle609@163.com
 *  
 */
public class WiFiAdapter{

    private WiFiStatus wiFiStatus ;
    private OnWifiOpenCallBack onWifiOpenCallBack;
    private OnWifiScanCallBack onWifiScanCallBack;
    private OnWifiConnCallBack onWifiConnCallBack;

    public WiFiAdapter(WiFiStatus wiFiStatus) {
        this.wiFiStatus = wiFiStatus;
    }

    public void setOnWifiOpenCallBack(OnWifiOpenCallBack onWifiOpenCallBack) {
        this.onWifiOpenCallBack = onWifiOpenCallBack;
    }

    public void setOnWifiScanCallBack(OnWifiScanCallBack onWifiScanCallBack) {
        this.onWifiScanCallBack = onWifiScanCallBack;
    }

    public void setOnWifiConnCallBack(OnWifiConnCallBack onWifiConnCallBack) {
        this.onWifiConnCallBack = onWifiConnCallBack;
    }

    public void wifiStateOpen(){
        if (onWifiOpenCallBack != null){
            onWifiOpenCallBack.onWifiStateOpen();
        }
        wiFiStatus.setEnabled(true);
    }

    public void wifiStateClose(){
        if (onWifiOpenCallBack != null){
            onWifiOpenCallBack.onWifiStateClose();
        }
        wiFiStatus.setEnabled(false);
    }

    public void wifiScanStarted(){
        if (onWifiScanCallBack != null){
            onWifiScanCallBack.onWifiScanStarted();
        }
        wiFiStatus.setScan(true);
    }

    public void wifiScanFound(List<ScanResult> scanResults){
        if (onWifiScanCallBack != null){
            onWifiScanCallBack.onWifiScanFound(scanResults);
        }
        wiFiStatus.setScan(false);
    }

    public void wifiConnectFail(){
        if (onWifiConnCallBack != null){
            onWifiConnCallBack.onWifiConnectFail();
        }
        wiFiStatus.setConn(false);
        WiFiAction.setIsConnecting(false);
    }

    //设备
    public void wifiConnected(){
        if (onWifiConnCallBack != null) {
            onWifiConnCallBack.onWifiConnected();
        }
        wiFiStatus.setConn(true);
        WiFiAction.setIsConnecting(false);
    }

    public void wifiDisConnected(){
        if (onWifiConnCallBack != null) {
            onWifiConnCallBack.onWifiDisConnected();
        }
        wiFiStatus.setConn(false);
        WiFiAction.setIsConnecting(false);
    }

}
