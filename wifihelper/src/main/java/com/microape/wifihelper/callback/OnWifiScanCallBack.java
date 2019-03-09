package com.microape.wifihelper.callback;

import android.net.wifi.ScanResult;

import com.microape.wifihelper.receiver.WiFiStatus;

import java.util.List;

/**
 * Created by pengle on 2018-09-13.
 * email:pengle609@163.com
 */

public class OnWifiScanCallBack {

    public void onWifiScanStarted(){
        WiFiStatus.newInstance().setScan(true);
    }

    public void onWifiScanFound(List<ScanResult> scanResults){
        WiFiStatus.newInstance().setScan(false);
    }

}
