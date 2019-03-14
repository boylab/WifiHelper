package com.microape.wifihelper.callback;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by pengle on 2018-09-13.
 * email:pengle609@163.com
 */

public interface OnWifiScanCallBack {

    void onWifiScanStarted();

    void onWifiScanFound(List<ScanResult> scanResults);

}
