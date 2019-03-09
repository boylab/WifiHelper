package com.microape.wifihelper.callback;

import com.microape.wifihelper.receiver.WiFiStatus;

/**
 * Created by pengle on 2018-06-28.
 * email:pengle609@163.com
 */

public class OnWifiOpenCallBack {

    public void onWifiStateOpen(){
        WiFiStatus.newInstance().setEnabled(true);
    }

    public void onWifiStateClose(){
        WiFiStatus.newInstance().setEnabled(false);
    }
}
