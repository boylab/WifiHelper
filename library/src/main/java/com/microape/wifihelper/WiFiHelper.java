package com.microape.wifihelper;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import com.microape.wifihelper.callback.OnWifiConnCallBack;
import com.microape.wifihelper.callback.OnWifiOpenCallBack;
import com.microape.wifihelper.callback.OnWifiScanCallBack;
import com.microape.wifihelper.receiver.WiFiAction;
import com.microape.wifihelper.receiver.WiFiReceiverManager;
import com.microape.wifihelper.receiver.WiFiStatus;
import com.microape.wifihelper.receiver.WiFiUnit;
import com.microape.wifihelper.utils.WiFiUtil;

public class WiFiHelper {

    private static volatile WiFiHelper instance = null;
    private Context context;
    private WiFiReceiverManager wiFiReceiverManager = new WiFiReceiverManager();

    private WifiManager wifiManager;
    private WiFiUtil wiFiUtil;

    private WiFiHelper() {

    }

    private static class SingletonInstance {
        private static final WiFiHelper INSTANCE = new WiFiHelper();
    }

    public static WiFiHelper newInstance() {
        return WiFiHelper.SingletonInstance.INSTANCE;
    }

    public WiFiHelper init(Application context){
        this.context = context.getApplicationContext();
        wiFiReceiverManager.registerReceiver(context);

        wifiManager= (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wiFiUtil = WiFiUtil.newInstance().initUtil(context, wifiManager);

        /*WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
        wiFiUartStatus().setEnabled(wifiManager.isWifiEnabled());*/
        return this;
    }

    public WiFiStatus wiFiStatus() {
        return wiFiReceiverManager.getWiFiStatus();
    }

    public void setOpenCallBack(OnWifiOpenCallBack onWifiOpenCallBack) {
        wiFiReceiverManager.setOpenCallBack(onWifiOpenCallBack);
    }

    public void setSearchCallBack(OnWifiScanCallBack onWifiScanCallBack) {
        wiFiReceiverManager.setSearchCallBack(onWifiScanCallBack);
    }

    public void setConnCallBack(OnWifiConnCallBack onWifiConnCallBack) {
        wiFiReceiverManager.setConnCallBack(onWifiConnCallBack);
    }

    public boolean isEnabled(){
        return wifiManager.isWifiEnabled();
    }

    public boolean isScaning(){
        return WiFiStatus.newInstance().isScan();
    }

    //open wifi
    @SuppressLint("MissingPermission")
    public void enable(){
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    //close wifi
    @SuppressLint("MissingPermission")
    public void disable(){
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    public void startScan(){
        if (!wifiManager.isWifiEnabled()){
            Toast.makeText(context, "请先打开WiFi！", Toast.LENGTH_SHORT).show();
            return;
        }

        LocationManager locManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
            Toast.makeText(context,"未打开GPS,无法扫描", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!wiFiStatus().isScan()){
            Intent intent = new Intent();
            intent.setAction(WiFiAction.SCAN_RESULTS_START_ACTION);
            context.sendBroadcast(intent);

            // TODO: 2019/3/10  目前暂时可用、Android P之后看如何处理
            @SuppressLint("MissingPermission") boolean startScan = wifiManager.startScan();
        }
    }

    public boolean connDevice(ScanResult wifiResult, String pwd){
        boolean isWiFiConn = false;
        try {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") NetworkInfo mWifiInfo = connManager.getActiveNetworkInfo();
            if (mWifiInfo != null && mWifiInfo.isConnected()){
                @SuppressLint("MissingPermission") DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                @SuppressLint("MissingPermission") WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                if (connectionInfo.getBSSID().equals(wifiResult.BSSID)){
                    // TODO: 2018-09-21 如果是已经连上指定wifi、则直接监听通讯
                    Intent intent = new Intent();
                    intent.setAction(WiFiAction.NETWORK_STATE_CONNECTED);
                    intent.putExtra(WiFiUnit.TARGET_SSID, wifiResult.SSID);
                    context.sendBroadcast(intent);
                    isWiFiConn = true;
                }else {
                    wiFiReceiverManager.startConnectTimer(context, wifiResult, WiFiAction.timeDelay);
                    isWiFiConn = wiFiUtil.connectWifi(wifiResult, pwd);
                }
            }else {
                wiFiReceiverManager.startConnectTimer(context, wifiResult, WiFiAction.timeDelay);
                isWiFiConn = wiFiUtil.connectWifi(wifiResult, pwd);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return isWiFiConn;
    }

    public void clearDSWiFi(){
        wiFiUtil.clearConnectWifi();
    }


    public void unRegister(){
        if (wiFiReceiverManager != null){
            wiFiReceiverManager.unRegisterReceiver();
        }
    }

    // 添加一个网络并连接
    @SuppressLint("MissingPermission")
    private void addNetWork(WifiConfiguration configuration) {
        @SuppressLint("MissingPermission") int wcgId = wifiManager.addNetwork(configuration);
        wifiManager.enableNetwork(wcgId, true);
    }

    // 断开指定ID的网络
    @SuppressLint("MissingPermission")
    private void disConnectionWifi(int netId) {
        wifiManager.disableNetwork(netId);
        wifiManager.disconnect();
    }


    
    
    
}
