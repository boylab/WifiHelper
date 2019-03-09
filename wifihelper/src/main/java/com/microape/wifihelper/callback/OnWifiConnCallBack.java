package com.microape.wifihelper.callback;

import com.microape.wifihelper.receiver.WiFiAction;
import com.microape.wifihelper.receiver.WiFiStatus;

/**
 * Created by pengle on 2018-06-28.
 * email:pengle609@163.com
 */

public class OnWifiConnCallBack {

    public void onWifiConnectFail(){
        WiFiStatus.newInstance().setConn(false);
        WiFiAction.setIsConnecting(false);
    }

    //设备
    public void onWifiConnected(){
        WiFiStatus.newInstance().setConn(true);
        WiFiAction.setIsConnecting(false);
    }

    public void onWifiDisConnected(){
        WiFiStatus.newInstance().setConn(false);
        WiFiAction.setIsConnecting(false);
    }

}
